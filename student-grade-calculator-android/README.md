# Student Grade Calculator v2.0
### SE 3242 — Android Application Development | ICT University

A fully upgraded Kotlin console application demonstrating core OOP principles:
**inheritance**, **polymorphism**, **interfaces**, and the **factory method pattern**.

---

## OOP Design

```
Student (abstract class)
├── UndergraduateStudent (data class) — adds yearOfStudy
└── GraduateStudent      (data class) — adds thesisTitle

ReportExporter (interface)
├── ExcelExporter  — produces .xlsx via Apache POI
├── HtmlExporter   — produces styled .html page
└── JsonExporter   — produces .json array

FileManager (interface)
└── DefaultFileManager — writes to real filesystem

ExporterFactory (object) — Factory Method Pattern
└── create(ExportFormat) → ReportExporter
```

### Key OOP Concepts Used

| Concept | Where |
|---|---|
| **Inheritance** | `UndergraduateStudent` and `GraduateStudent` both extend `Student` |
| **Polymorphism** | `studentType()`, `describe()`, `export()` dispatch to correct subclass at runtime |
| **Interface** | `ReportExporter` and `FileManager` define contracts without implementation |
| **Factory Method** | `ExporterFactory.create(format)` returns the right exporter without exposing class names |
| **Abstraction** | `Main.kt` never imports concrete exporter classes — only the interface |
| **No Loops** | `map`, `filter`, `fold`, `forEachIndexed`, `joinToString` used throughout |

---

## Project Structure

```
src/main/kotlin/
├── Main.kt                          ← entry point
├── models/
│   ├── Student.kt                   ← abstract base class
│   ├── UndergraduateStudent.kt      ← concrete subclass (data class)
│   └── GraduateStudent.kt           ← concrete subclass (data class)
├── services/
│   └── GradeCalculator.kt           ← computes average, grade, GPA, status
├── exporters/
│   ├── ReportExporter.kt            ← interface
│   ├── ExcelExporter.kt             ← .xlsx implementation
│   ├── HtmlExporter.kt              ← .html implementation
│   └── JsonExporter.kt              ← .json implementation
├── factory/
│   └── ExporterFactory.kt           ← factory method + ExportFormat enum
└── io/
    ├── FileManager.kt               ← interface + DefaultFileManager
    └── ExcelReader.kt               ← reads .xlsx input
```

---

## Input File Format

Your `input.xlsx` must have this column layout:

| StudentID | Name | Type | Subject1 | Subject2 | … |
|---|---|---|---|---|---|
| S001 | Alice Martin | Undergraduate | 92 | 88 | … |
| S002 | Eva Nguyen | Graduate | 95 | 97 | … |

**Type column values:**
- `Undergraduate` or `ug` → creates an `UndergraduateStudent`
- `Graduate` or `gr` → creates a `GraduateStudent`

---

## Grading Scale

| Grade | GPA | Score Range |
|---|---|---|
| A | 4.0 | 80 – 100 |
| B+ | 3.5 | 70 – 79 |
| B | 3.0 | 60 – 69 |
| C+ | 2.5 | 55 – 59 |
| C | 2.0 | 50 – 54 |
| D+ | 1.5 | 45 – 49 |
| D | 1.0 | 40 – 44 |
| F | 0.0 | 0 – 39 |

---

## How to Run

### Prerequisites
- Java JDK 21+ (set `JAVA_HOME`)
- Gradle (via the included `gradlew` / `gradlew.bat` wrapper)

### Option 1 — Gradle run (no JAR needed)

**Windows:**
```bat
set JAVA_HOME=C:\Program Files\Java\jdk-24
gradlew.bat run --args="--input input.xlsx --output results --format all"
```

**Mac/Linux:**
```bash
./gradlew run --args="--input input.xlsx --output results --format all"
```

### Option 2 — Build a fat JAR and run anywhere

```bat
gradlew.bat shadowJar
java -jar build\libs\grade-calculator-2.0.0-all.jar --input input.xlsx --output results --format all
```

---

## CLI Flags

| Flag | Short | Description | Default |
|---|---|---|---|
| `--input` | `-i` | Path to input `.xlsx` | required |
| `--output` | `-o` | Base name for output files | required |
| `--format` | `-f` | `excel`, `html`, `json`, or `all` | `all` |
| `--help` | `-h` | Show usage info | — |

### Examples

```bat
REM Export all formats
gradlew.bat run --args="--input input.xlsx --output results --format all"

REM HTML only
gradlew.bat run --args="--input input.xlsx --output report --format html"

REM JSON only
gradlew.bat run --args="--input input.xlsx --output data --format json"

REM Excel only
gradlew.bat run --args="--input input.xlsx --output grades --format excel"
```

---

## Expected Output

### Console
```
📂 Reading from: input.xlsx

📋 Student Details:
──────────────────────────────────────────────────────────────────────
  Alice Martin (Year 1 Undergraduate) — Average: 91.50, Grade: A, Status: PASS
  Bob Johnson (Year 1 Undergraduate) — Average: 71.00, Grade: B+, Status: PASS
  Carol Dupont (Graduate — Thesis: "N/A") — Average: 87.50, Grade: A, Status: PASS
  David Lee (Year 1 Undergraduate) — Average: 36.25, Grade: F, Status: FAIL
  Eva Nguyen (Graduate — Thesis: "N/A") — Average: 94.00, Grade: A, Status: PASS
  Frank Mbarga (Year 1 Undergraduate) — Average: 56.25, Grade: C+, Status: PASS

📤 Exporting reports:
──────────────────────────────────────────────────────────────────────
  ✅ Excel → results.xlsx
  ✅ HTML  → results.html
  ✅ JSON  → results.json

=== Student Grade Calculator — Summary ===
Total students    : 6
  Undergraduates  : 4
  Graduates       : 2
Passed            : 5
Failed            : 1
```

### Output files produced
- `results.xlsx` — colour-coded spreadsheet (green = PASS, red = FAIL)
- `results.html` — open in any browser, styled table with stats cards
- `results.json` — structured JSON array, ready for any API or frontend

---

## Running Tests

```bat
gradlew.bat test
```

Test report: `build/reports/tests/test/index.html`