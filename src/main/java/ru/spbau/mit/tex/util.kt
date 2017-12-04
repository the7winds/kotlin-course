package ru.spbau.mit.tex

fun argsString(vararg args: String) =
        if (args.isNotEmpty()) args.joinToString(separator = ", ", prefix = "[", postfix = "]") else ""
