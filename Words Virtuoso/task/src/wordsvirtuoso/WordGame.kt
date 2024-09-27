package wordsvirtuoso

import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.system.exitProcess

class WordGame(args: Array<String>) {

    private val allWords = Path(args[0])
    private val candidates = Path(args[1])
    private var word = ""
    private var history = mutableListOf<String>()
    private var wrongChars = mutableSetOf<Char>()
    private var timeStart = 0L
    private var timeEnd = 0L
    private var duration = 0L
    private var tries = 0

    init {
        checkFile(allWords, false)
        checkFile(candidates, true)
        checkCandidateInFile(allWords, candidates)
        timeStart = System.currentTimeMillis()
        word = chooseWord()
    }

    private fun chooseWord(): String {
        return candidates.readLines().random()
    }

    fun checkGuess(guess: String): Boolean {
        when {
            guess.length != 5 -> println("The input isn't a 5-letter word.")
            !guess.matches(Regex("[a-z]{5}")) -> println("One or more letters of the input aren't valid.")
            guess.length != guess.toCharArray().toSet().size -> println("The input has duplicate letters.")
            guess !in allWords.readLines() -> println("The input word isn't included in my words list.")
            else -> {
                return printGuess(guess, word)
            }
        }
        return true
    }

    private fun printGuess(guess: String, word: String): Boolean {
        tries++
        val clue = buildString {
            for (i in guess.indices) {
                when (guess[i]) {
                    word[i] -> { append("\u001b[48:5:10m${guess[i].uppercase()}\u001b[0m"); continue }
                    in word -> { append("\u001b[48:5:11m${guess[i].uppercase()}\u001b[0m"); continue }
                    else -> append("\u001b[48:5:7m${guess[i].uppercase()}\u001b[0m")
                }
            }
        }.also { clueHistory(it) }
        return getResult(clue, guess)
    }

    private fun getResult(clue: String, guess: String): Boolean {
        val cleanClue = clue.filter { it.isUpperCase() }

        if (cleanClue.lowercase() == word) {
            timeEnd = System.currentTimeMillis()
            duration = (timeEnd - timeStart) / 1000
            if (history.size == 1) {
                print("\n")
                cleanClue.forEach { print("\u001b[48:5:10m${it.uppercase()}\u001b[0m") }
                println("\n\nCorrect!\nAmazing luck! The solution was found at once.")
            } else {
                println("")
                history.forEach { println(it) }
                println("\nCorrect!")
                println("The solution was found after $tries tries in $duration seconds.")
            }
            return false
        } else {
            println("")
            history.forEach { println(it) }
            println("\n${wrongChars(guess, word)}")
        }
        return true
    }

    private fun wrongChars(guess: String, word: String): String {
        return wrongChars.apply {
            for (ch in guess) {
                if (ch !in word) add(ch.uppercaseChar())
            }
        }.sorted().joinToString("", prefix = "\u001b[48:5:14m", postfix = "\u001b[0m")
    }

    private fun clueHistory(clue: String) {
        history.add(clue)
    }

    private fun checkFile(file: Path, candidate: Boolean) {
        val totalCount = try {
            file.readLines().count()
        } catch (e: IOException) {
            println("Error: The ${if (candidate) "candidate " else ""}words " +
                    "file $file doesn't exist.")
            exitProcess(0)
        }
        val validCount = file.readLines().asSequence()
            .filter { it.matches(Regex("[a-zA-Z]{5}"))
                    && it.length == it.toCharArray().toSet().size }
            .count()

        if (totalCount != validCount) {
            println("Error: ${totalCount - validCount} invalid words were found in the $file file.")
            exitProcess(0)
        }
    }

    private fun checkCandidateInFile(file: Path, candidates: Path) {
        val lCaseFile = file.readLines().map { it.lowercase() }
        val lCaseCandidates = candidates.readLines().map { it.lowercase() }
        if (lCaseFile.containsAll(lCaseCandidates)) {
            println(TITLE)
        } else {
            val notFound = lCaseCandidates.asSequence()
                .filterNot { it in lCaseFile }
                .count()
            println("Error: $notFound candidate words " +
                    "are not included in the $file file.")
            exitProcess(0)
        }
    }
}