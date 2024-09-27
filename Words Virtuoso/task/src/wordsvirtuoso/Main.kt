package wordsvirtuoso

import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size != 2) { println("Error: Wrong number of arguments."); exitProcess(0) }
    val game = WordGame(args)
    while (true) {
        val guess = println("\nInput a 5-letter word:").let { readln().lowercase() }
        if (guess == "exit") { println("\nThe game is over."); break }
        game.checkGuess(guess)
    }
}

const val TITLE = "Words Virtuoso"