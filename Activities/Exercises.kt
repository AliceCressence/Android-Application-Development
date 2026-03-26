fun main() {
 
    // ── Exercise 1 ────────────────────────────────────────────────────────────
    println("=" .repeat(55))
    println("EXERCISE 1 — Higher-Order Function: processList")
    println("=".repeat(55))
 
    val nums = listOf(1, 2, 3, 4, 5, 6)
 
    val evens    = processList(nums) { it % 2 == 0 }
    val odds     = processList(nums) { it % 2 != 0 }
    val bigThan3 = processList(nums) { it > 3 }
 
    println("Source list  : $nums")
    println("Even numbers : $evens")       // [2, 4, 6]
    println("Odd numbers  : $odds")        // [1, 3, 5]
    println("Greater than 3: $bigThan3")   // [4, 5, 6]
    println()
 
    // ── Exercise 2 ────────────────────────────────────────────────────────────
    println("=".repeat(55))
    println("EXERCISE 2 — Word Length Map (length > 4)")
    println("=".repeat(55))
 
    val words = listOf("apple", "cat", "banana", "dog", "elephant")
    val longWords = wordLengthMap(words)
 
    println("Source words : $words")
    println("Filtered map :")
    // forEach on a Map gives us (key, value) destructuring
    longWords.forEach { (word, length) ->
        println("  $word has length $length")
    }
    println()
 
    // ── Exercise 3 ────────────────────────────────────────────────────────────
    println("=".repeat(55))
    println("EXERCISE 3 — Average Age (names starting A or B)")
    println("=".repeat(55))
 
    val people = listOf(
        Person("Alice",   25),
        Person("Bob",     30),
        Person("Charlie", 35),
        Person("Anna",    22),
        Person("Ben",     28)
    )
 
    val avg = averageAgeByNameStart(people)
 
    println("People       : $people")
    println("Filtered (A/B): ${people.filter { it.name.first() in setOf('A','B') }}")
 
    // Elvis operator provides a fallback message if no matches found
    println("Average age  : ${"%.1f".format(avg ?: 0.0)}")  // → 26.3
    println()
 
    // ── Bonus: chained pipeline (from slide 16) ───────────────────────────────
    println("=".repeat(55))
    println("BONUS — Chained Pipeline (filter → map → fold)")
    println("=".repeat(55))
 
    val result = listOf(1, 2, 3, 4)
        .filter { it > 2 }
        .map    { it * 10 }
        .fold("") { acc, i -> acc + i.toString() }
 
    println("listOf(1,2,3,4).filter{>2}.map{*10}.fold → \"$result\"")  // "3040"
}