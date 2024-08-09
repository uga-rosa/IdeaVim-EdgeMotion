package com.github.ugarosa.idea.edgemotion

import com.maddyhome.idea.vim.VimPlugin
import com.maddyhome.idea.vim.api.ExecutionContext
import com.maddyhome.idea.vim.api.VimEditor
import com.maddyhome.idea.vim.api.injector
import com.maddyhome.idea.vim.command.MappingMode
import com.maddyhome.idea.vim.command.OperatorArguments
import com.maddyhome.idea.vim.extension.ExtensionHandler
import com.maddyhome.idea.vim.extension.VimExtension
import com.maddyhome.idea.vim.extension.VimExtensionFacade.executeNormalWithoutMapping
import com.maddyhome.idea.vim.newapi.ij
import kotlin.math.abs

class EdgeMotionExtension : VimExtension {
    private val pluginName = "edge-motion"

    private enum class Direction(val value: Int) {
        UP(-1), DOWN(1)
    }

    override fun getName(): String = pluginName

    override fun init() {
        VimPlugin.getKey().putKeyMapping(
            MappingMode.NXO,
            injector.parser.parseKeys("<Plug>(edgemotion-j)"),
            owner,
            MoveHandler(Direction.DOWN),
            false,
        )
        VimPlugin.getKey().putKeyMapping(
            MappingMode.NXO,
            injector.parser.parseKeys("<Plug>(edgemotion-k)"),
            owner,
            MoveHandler(Direction.UP),
            false,
        )
    }

    private class MoveHandler(private val dir: Direction) : ExtensionHandler {
        override fun execute(editor: VimEditor, context: ExecutionContext, operatorArguments: OperatorArguments) {
            val caretModel = editor.ij.caretModel
            val lnumOrg = caretModel.logicalPosition.line
            val col = caretModel.logicalPosition.column

            val islandStart = inInLand(editor, lnumOrg, col)
            val islandNext = inInLand(editor, lnumOrg + dir.value, col)

            val shouldMoveToLand = !(islandStart && islandNext)
            var lnum = lnumOrg
            val lastLnum = editor.lineCount()

            if (shouldMoveToLand) {
                if (islandStart) {
                    lnum += dir.value
                }
                while (lnum != 0 && lnum <= lastLnum && !inInLand(editor, lnum, col)) {
                    lnum += dir.value
                }
            } else {
                while (lnum != 0 && lnum <= lastLnum && inInLand(editor, lnum, col)) {
                    lnum += dir.value
                }
                lnum -= dir.value
            }

            // edge not found
            if (lnum == 0 || lnum == lastLnum + 1) return

            val moveCmd = when (dir) {
                Direction.UP -> "k"
                Direction.DOWN -> "j"
            }
            val cmd = injector.parser.parseKeys("${abs(lnum - lnumOrg)}${moveCmd}")
            executeNormalWithoutMapping(cmd, editor.ij)
        }

        // A `Land` consists of non-whitespace characters or whitespace characters sandwiched between non-whitespace.
        fun inInLand(editor: VimEditor, lnum: Int, col: Int): Boolean {
            val line = editor.getLineText(lnum)
            if (col < 0 || col >= line.length) return false
            val char = line[col]
            if (!isWhite(char)) return true

            if (col == 0 || col == line.length - 1) return false
            val prev = line[col - 1]
            val next = line[col + 1]
            return !isWhite(prev) && !isWhite(next)
        }

        fun isWhite(char: Char) = char == ' ' || char == '\t'
    }
}