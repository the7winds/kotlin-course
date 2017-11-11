package ru.spbau.mit

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import ru.spbau.mit.interp.LangLexer
import ru.spbau.mit.interp.LangParser
import ru.spbau.mit.interp.Scope
import ru.spbau.mit.interp.Transformer

fun main(args: Array<String>) {
    if (args.size != 2) {
        System.err.println("usage: ./app FILENAME")
    }

    val lexer = LangLexer(CharStreams.fromFileName(args[1]))
    val parser = LangParser(BufferedTokenStream(lexer))
    val transformer = Transformer()
    val ast = transformer.visit(parser.file())
    val scope = Scope(null)

    ast.run(scope)
}
