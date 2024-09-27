package wordsvirtuoso

import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size != 2) { println("Error: Wrong number of arguments."); exitProcess(0) }
    loop1@while (true) {
        val game = WordGame(args)
        loop2@while (true) {
            val guess = println("\nInput a 5-letter word:").let { readln().lowercase() }
            if (guess == "exit") { println("\nThe game is over."); break@loop1 }
            if (guess == "new") { break@loop2 }
            if (!game.checkGuess(guess)) {
                val command = println("\nNew game (new) or exit (exit):").let { readln().lowercase() }
                if (command == "exit") { println("\nThe game is over."); break@loop1 }
                if (command == "new") { println("\nNew game starts."); break@loop2 }
            }
        }
    }
}

const val TITLE = "Words Virtuoso"