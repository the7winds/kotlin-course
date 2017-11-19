package ru.spbau.mit.tex

fun argsString(vararg args: String) = args.run {
    if (isNotEmpty()) "[${joinToString()}]" else ""
}