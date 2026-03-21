import kotlin.math.sqrt

// Exo 1 - Predicates
val isEven: (Int) -> Boolean = { it % 2 == 0 }         
val isOdd: (Int) -> Boolean = { it % 2 != 0 }
val isPrime: (Int) -> Boolean = { n ->
    if (n < 2) false
    else (2..sqrt(n.toDouble()).toInt()).none { n % it == 0 }  }

fun processList(numbers: List<Int>, predicate: (Int) -> Boolean): List<Int> {
    val new_number: List<Int> = numbers.filter(predicate)
    print(new_number)
    return new_number
}

// Exo 2
fun transforming(words: List<String>) {
    words
        .filter { it.length > 4 }                     
        .forEach { word ->
            println("$word has length ${word.length}")
        }
}

// Exo 3
data class Person(val name: String, val age: Int)       

fun complexDataManip(personalities: List<Person>) {
    personalities
        .filter { it.name[0] == 'A' || it.name[0] == 'B' }
        .forEach { person ->
            println("The age of ${person.name} is ${person.age} years old")
        }
}

fun main() {
    val number = listOf(1, 2, 9, 10, 0, 12)            
    val words = listOf("apple", "cat", "banana", "dog", "elephant")
    val people = listOf(
        Person("Alice", 25),                           
        Person("Bob", 30),
        Person("Charlie", 35),
        Person("Anna", 22),
        Person("Ben", 28)
    )

    println("--- Even numbers ---")
    processList(number, isEven)

    println("\n--- Prime numbers ---")
    processList(number, isPrime)

    println("\n--- Words longer than 4 ---")
    transforming(words)

    println("\n--- People whose name starts with A or B ---")
    complexDataManip(people)
}