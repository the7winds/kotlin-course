package ru.spbau.mit

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import ru.spbau.mit.debugger.Command
import ru.spbau.mit.debugger.Debugger
import ru.spbau.mit.parser.DebugLexer
import ru.spbau.mit.parser.DebugParser
import java.util.*

fun main(args: Array<String>) {
    val debugger = Debugger()
    val command = Command(debugger)
    val reader = Scanner(System.`in`)

    while (reader.hasNext()) {
        val input = reader.nextLine()
        val lexer = DebugLexer(CharStreams.fromString(input))
        val parser = DebugParser(BufferedTokenStream(lexer))

        if (parser.numberOfSyntaxErrors > 0) {
            System.err.println("syntax error")
            continue
        }

        try {
            command.visit(parser.command())
        } catch (e: Exception) {
            System.err.println("command failed: ${e.message}")
        }
    }
}
