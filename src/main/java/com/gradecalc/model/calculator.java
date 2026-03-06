package com.gradecalc.model;

import java.util.ArrayList;
import java.util.List;

// BASE CLASS — general calculator
public abstract class Calculator {
    protected List<Subject> subjects = new ArrayList<>();

    // Add a subject
    public void addSubject(Subject subject) {
        subjects.add(subject);
    }

    // Delete one subject by index
    public void deleteSubject(int index) {
        if (index >= 0 && index < subjects.size()) {
            subjects.remove(index);
        }
    }

    // Delete ALL subjects
    public void deleteAll() {
        subjects.clear();
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    // Abstract — child class must implement
    public abstract double calculate();
}