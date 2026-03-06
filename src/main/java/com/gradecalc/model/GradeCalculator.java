package com.gradecalc.model;

// DERIVED CLASS — inherits from Calculator
public class GradeCalculator extends Calculator {

    // Lambdas stored as fields for reuse
    private final java.util.function.Function<Double, String>
            gradeLetterFn = percentage -> {
        if (percentage >= 90) return "A+";
        else if (percentage >= 80) return "A";
        else if (percentage >= 70) return "B";
        else if (percentage >= 60) return "C";
        else if (percentage >= 50) return "D";
        else return "F";
    };

    private final java.util.function.Predicate<Double>
            passFailFn = percentage -> percentage >= 50;

    // Required override — calculates average percentage
    @Override
    public double calculate() {
        return subjects.stream()
                .mapToDouble(Subject::getPercentage)
                .average()
                .orElse(0.0);
    }

    // GPA on a 4.0 scale
    public double calculateGPA() {
        double avg = calculate();
        if (avg >= 90) return 4.0;
        else if (avg >= 80) return 3.7;
        else if (avg >= 70) return 3.3;
        else if (avg >= 60) return 3.0;
        else if (avg >= 50) return 2.0;
        else return 0.0;
    }

    // Uses lambda
    public String getGradeLetter(double percentage) {
        return gradeLetterFn.apply(percentage);
    }

    // Uses lambda predicate
    public String getPassFail(double percentage) {
        return passFailFn.test(percentage) ? "PASS ✅" : "FAIL ❌";
    }

    // Overall pass/fail based on average
    public String getOverallPassFail() {
        return getPassFail(calculate());
    }
}