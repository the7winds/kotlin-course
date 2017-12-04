package ru.spbau.mit.tex

import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.TimeUnit

class File : TexElement() {
    fun documentClass(name: String) = addTag(DocumentClass(name), { })

    fun usepackage(name: String, vararg args: String) = addTag(Usepackage(name, *args), {  })

    fun document(content: DocumentScope.() -> Unit) = addScope(Document(), {
        - DocumentScope().apply(content).toString()
    })

    fun compile(outputDirectory: String, timeout: Long = 2) {
        Paths.get(outputDirectory).toFile().deleteRecursively()
        val outputDirectoryPath = Files.createDirectory(Paths.get(outputDirectory))
        val outputTexSourcePath = outputDirectoryPath.resolve("source.tex")
        Files.write(outputTexSourcePath, toString().toByteArray())
        ProcessBuilder("pdflatex ${outputDirectoryPath.relativize(outputTexSourcePath)}".split(" "))
                .directory(outputDirectoryPath.toFile())
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()
                .waitFor(timeout, TimeUnit.SECONDS)
    }
}

fun file(content: File.() -> Unit): File = File().also(content)