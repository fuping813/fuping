package com.tg.ums.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ScoreStatisticsDTO {
    private List<Integer> scoreDistribution;
    private List<Map<String, Object>> knowledgePointScores;
}
