package ru.spbau.mit
import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import ru.spbau.mit.interp.Scope
import ru.spbau.mit.interp.Transformer
import ru.spbau.mit.parser.LangLexer
import ru.spbau.mit.parser.LangParser
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.assertEquals


class TestInterpreter {

    @ParameterizedTest
    @CsvSource("simple.lang, simple.out",
               "recursion.lang, recursion.out",
               "nestedFunctions.lang, nestedFunctions.out",
               "closure.lang, closure.out",
               "return.lang, return.out")
    fun testInterpreter(inputFile: String, outputFile: String) {
        val inputFilePath = javaClass.classLoader.getResource(inputFile).path
        val outputFilePath = javaClass.classLoader.getResource(outputFile).path
        val lexer = LangLexer(CharStreams.fromFileName(inputFilePath))
        val parser = LangParser(BufferedTokenStream(lexer))
        val transformer = Transformer()

        val testByteArray = ByteArrayOutputStream()
        val testPrintStream = PrintStream(testByteArray, true, "utf-8")

        transformer.visit(parser.file()).run(Scope(null, testPrintStream))

        val testOutput = String(testByteArray.toByteArray(), StandardCharsets.UTF_8)
        val expectedOutput = String(Files.readAllBytes(Paths.get(outputFilePath)), StandardCharsets.UTF_8)

        assertEquals(expectedOutput, testOutput)
    }
}