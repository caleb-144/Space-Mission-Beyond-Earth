package edu.spacemission.model;

import java.util.List;

public record Question(String text, List<String> options, String correctAnswer, String explanation) { }
