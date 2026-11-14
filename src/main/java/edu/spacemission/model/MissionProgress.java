package edu.spacemission.model;

public record MissionProgress(int missionId, String missionName, int score, int maxScore, boolean completed) { }
