package wordsvirtuoso

import java.io.File
import kotlin.random.Random
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size != 2) {
        println(INVALID_ARGS)
        exitProcess(242)
    }
    val words = checkFile(args[0])
    val candidates = checkFile(args[1], " candidate")
    var notFounded = 0
    for (c in candidates)
        if (!words.contains(c)) notFounded++
    if (notFounded > 0) {
        println("Error: $notFounded candidate words are not included in the ${args[0]} file.")
        exitProcess(342)
    } else println("Words Virtuoso")
    gameLoop(candidates[Random.nextInt(candidates.size)], words)
}

fun gameLoop(secretWord: String, wordsList: List<String>) {
    val start = System.currentTimeMillis()
    var turns = 0
    val clues = mutableListOf<String>()
    val unusedLetters = mutableSetOf<Char>()
    loop@ while (true) {
        println("\nInput a 5-letter word:")
        val input = readln().lowercase()
        turns++
        if (input == "exit") {
            println("The game is over.")
            exitProcess(442)
        }
        val checkResult = isValidInput(input)
        if (checkResult != VALID_STRING) {
            println(checkResult)
            continue@loop
        }
        if (!wordsList.contains(input)) {
            println(OUT_OF_WORDS)
            continue@loop
        }
        var clue = ""
        for (i in 0 until LENGTH) {
            when {
                input[i] == secretWord[i] -> clue += greenChar(input[i].uppercaseChar())
                secretWord.contains(input[i]) -> clue += yellowChar(input[i].uppercaseChar())
                else -> {
                    clue += greyChar(input[i].uppercaseChar())
                    unusedLetters.add(input[i].uppercaseChar())
                }
            }
        }
        clues.add(clue)
        println()
        for (c in clues) println(c)
        println()
        if (input == secretWord) {
            println("Correct!")
            val end = System.currentTimeMillis()
            val duration = end - start  // Milliseconds as a Long
            if (turns == 1) println(AMAZING_STRING)
            else println("The solution was found after $turns tries in ${ duration/1000 } seconds.")
            exitProcess(542)
        }
        println(azureString(unusedLetters.sorted().joinToString("")))
    }
}

fun greenChar(char: Char) = "\u001B[48:5:10:38:5:0m$char\u001B[0m"
fun yellowChar(char: Char) = "\u001B[48:5:11:38:5:0m$char\u001B[0m"
fun greyChar(char: Char) = "\u001B[48:5:7:38:5:0m$char\u001B[0m"
fun azureString(string: String) = "\u001B[48:5:14:38:5:0m$string\u001B[0m"

fun checkFile(filename: String, addition: String = ""): List<String> {
    val file = File(filename)
    if (!file.exists()) {
        println("Error: The$addition words file $filename doesn't exist.")
        exitProcess(42)
    }
    var invalidWordsCount = 0
    val words = file.readLines().toMutableList()
    for (i in words.indices) {
        words[i] = words[i].lowercase()
        if (isValidInput(words[i]) != VALID_STRING) invalidWordsCount++
    }
    if (invalidWordsCount > 0) {
        println("Error: $invalidWordsCount invalid words were found in the $filename file.")
        exitProcess(142)
    } //else println("All words are valid!")
    return words
}

const val AMAZING_STRING = "Amazing luck! The solution was found at once."
const val OUT_OF_WORDS = "The input word isn't included in my words list."
const val INVALID_ARGS = "Error: Wrong number of arguments."
const val VALID_STRING = "The input is a valid string."
const val INVALID_ISNT5 = "The input isn't a 5-letter word."
const val INVALID_CHARS = "One or more letters of the input aren't valid."
const val INVALID_DUPLC = "The input has duplicated letters."
const val LENGTH = 5

fun isValidInput(input: String): String {
    if (input.length != LENGTH) return INVALID_ISNT5
    for (char in input)
        if (char !in 'a'..'z') return INVALID_CHARS
    for (char in input)
        if (input.replace(char.toString(), "").length != LENGTH - 1) return INVALID_DUPLC
    return VALID_STRING
}