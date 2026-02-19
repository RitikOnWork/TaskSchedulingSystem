package com.promanage.service;

import com.promanage.dao.PredictionDAO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PredictionService {

    private final PredictionDAO predictionDAO = new PredictionDAO();
    private final ForecastingEngine forecastingEngine = new ForecastingEngine();

    public PredictionResult predictNextWeek() throws SQLException {
        // 1. Fetch Weekly Data
        Map<LocalDate, double[]> weeklyStats = predictionDAO.getWeeklyStats();

        List<Double> revenueHistory = new ArrayList<>();
        List<Double> countHistory = new ArrayList<>();

        // Sort by date (already sorted by SQL but ensuring it here)
        TreeMap<LocalDate, double[]> sortedStats = new TreeMap<>(weeklyStats);
        for (double[] stats : sortedStats.values()) {
            revenueHistory.add(stats[0]);
            countHistory.add(stats[1]);
        }

        // 2. Predict Revenue & Count
        double predictedRevenue = forecastingEngine.predictNextValue(revenueHistory);
        double predictedCount = forecastingEngine.predictNextValue(countHistory);

        double currentAvgRevenue = forecastingEngine.calculateAverage(revenueHistory);

        // 3. Predict Deadline Distribution (Simple Average for now)
        // In a real regression, we'd predict the count for each deadline bucket (1-5
        // days)
        List<Integer> historicalDeadlines = predictionDAO.getHistoricalDeadlines();
        double avgDeadline = 0;
        if (!historicalDeadlines.isEmpty()) {
            avgDeadline = historicalDeadlines.stream().mapToInt(Integer::intValue).average().orElse(0);
        }

        return new PredictionResult(
                Math.max(0, predictedRevenue),
                Math.max(0, (int) Math.round(predictedCount)),
                currentAvgRevenue,
                avgDeadline);
    }

    // Recommendation Logic
    public String getRecommendation(double predictedRevenue, double currentAvgRevenue) {
        if (predictedRevenue < currentAvgRevenue * 0.8) {
            return "LOW REVENUE ALERT: Predicted revenue is significantly lower than average. \n" +
                    "RECOMMENDATION: Extend deadlines by +15 days to encourage more project completions.";
        } else if (predictedRevenue > currentAvgRevenue * 1.2) {
            return "HIGH DEMAND ALERT: Predicted revenue is booming! \n" +
                    "RECOMMENDATION: Reduce flexibility. Stick to tight deadlines to maximize turnover.";
        } else {
            return "STABLE MARKET: Predication aligns with average. \n" +
                    "RECOMMENDATION: Maintain standard scheduling parameters.";
        }
    }

    // DTO for result
    public static class PredictionResult {
        public final double predictedRevenue;
        public final int predictedCount;
        public final double currentAvgRevenue;
        public final double avgDeadline;

        public PredictionResult(double predictedRevenue, int predictedCount, double currentAvgRevenue,
                double avgDeadline) {
            this.predictedRevenue = predictedRevenue;
            this.predictedCount = predictedCount;
            this.currentAvgRevenue = currentAvgRevenue;
            this.avgDeadline = avgDeadline;
        }
    }
}
