package ru.spbau.mit

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import ru.spbau.mit.interp.Scope
import ru.spbau.mit.interp.Transformer
import ru.spbau.mit.parser.LangLexer
import ru.spbau.mit.parser.LangParser

fun main(args: Array<String>) {
    if (args.size != 1) {
        System.err.println("usage: ./app FILENAME")
    }

    val lexer = LangLexer(CharStreams.fromFileName(args[1]))
    val parser = LangParser(BufferedTokenStream(lexer))

    if (parser.numberOfSyntaxErrors > 0) {
        System.err.println("parser error")
        System.exit(1)
    }

    try {
        val transformer = Transformer()
        val ast = transformer.visit(parser.file())
        val scope = Scope(null)
        ast?.run(scope)
    } catch (e: Exception) {
        System.err.println("something has broken: ${e.message}")
        System.exit(1)
    }

}
