package com.gradecalc.model;

public class Subject {
    private String name;
    private double score;
    private double maxScore;

    public Subject(String name, double score, double maxScore) {
        this.name = name;
        this.score = score;
        this.maxScore = maxScore;
    }

    public String getName()     { return name; }
    public double getScore()    { return score; }
    public double getMaxScore() { return maxScore; }
    public double getPercentage() {
        return (score / maxScore) * 100;
    }
}