# Student Grade Calculator

A console application that reads student scores from an Excel (`.xlsx`) file, computes each student's **average**, **letter grade**, **GPA**, and **pass/fail status**, then writes the results to a new `.xlsx` file.

Implemented in both **Kotlin** with identical logic and output.

---

## Grading Scale

| Grade | GPA | Min Score | Max Score |
|-------|-----|-----------|-----------|
| A     | 4.0 | 80        | 100       |
| B+    | 3.5 | 70        | 79        |
| B     | 3.0 | 60        | 69        |
| C+    | 2.5 | 55        | 59        |
| C     | 2.0 | 50        | 54        |
| D+    | 1.5 | 45        | 49        |
| D     | 1.0 | 40        | 44        |
| F     | 0.0 | 0         | 39        |

> **Pass threshold:** average **≥ 40** (grade D or above). Below 40 = FAIL.

---

## Project Structure

```
student-grade-calculator/
├── kotlin/
│   ├── src/main/kotlin/
│   │   ├── Main.kt                     # CLI entry point
│   │   ├── models/Student.kt           # Student data class
│   │   ├── services/GradeCalculator.kt # Grade computation logic
│   │   └── io/
│   │       ├── ExcelReader.kt          # Reads .xlsx input (Apache POI)
│   │       └── ExcelWriter.kt          # Writes .xlsx output (Apache POI)
│   ├── src/test/kotlin/GradeCalculatorTest.kt  # Unit tests (JUnit 5)
│   └── build.gradle.kts
├── docs/
│   └── uml_class_diagram.puml          # PlantUML class diagram
├── sample_input.xlsx                   # Demo input file
└── README.md
```

---

## Prerequisites

### Kotlin
- [JDK 17+](https://adoptium.net/)
- [Gradle](https://gradle.org/) (wrapper included)

---

## Usage

### Kotlin

```bash
cd kotlin
./gradlew build
./gradlew run --args="--input ../sample_input.xlsx --output ../output_kotlin.xlsx"
```

Both commands will:
1. Read student data from `sample_input.xlsx`
2. Compute average, grade, GPA, and status for each student
3. Write the results to the specified output file
4. Print a summary to the console

---

## Running Tests

### Kotlin

```bash
cd kotlin
./gradlew test
```

---

## Sample Input

| StudentID | Name         | Math | Science | English | History |
|-----------|--------------|------|---------|---------|---------|
| S001      | Alice Martin | 85   | 90      | 78      | 88      |
| S002      | Bob Johnson  | 55   | 48      | 62      | 51      |
| S003      | Carol Dupont | 72   | 68      | 75      | 70      |
| S004      | David Lee    | 38   | 42      | 35      | 30      |
| S005      | Eva Nguyen   | 95   | 97      | 91      | 93      |

### Expected Output

| StudentID | Name         | Math | Science | English | History | Average | Grade | GPA | Status |
|-----------|--------------|------|---------|---------|---------|---------|-------|-----|--------|
| S001      | Alice Martin | 85   | 90      | 78      | 88      | 85.25   | A     | 4.0 | PASS   |
| S002      | Bob Johnson  | 55   | 48      | 62      | 51      | 54.00   | C     | 2.0 | PASS   |
| S003      | Carol Dupont | 72   | 68      | 75      | 70      | 71.25   | B+    | 3.5 | PASS   |
| S004      | David Lee    | 38   | 42      | 35      | 30      | 36.25   | F     | 0.0 | FAIL   |
| S005      | Eva Nguyen   | 95   | 97      | 91      | 93      | 94.00   | A     | 4.0 | PASS   |

---

## License

This project is for educational purposes.
