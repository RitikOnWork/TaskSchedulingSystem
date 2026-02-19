package com.promanage.service;

import java.util.List;

public class ForecastingEngine {

    /**
     * Predicts the next value in a series using Simple Linear Regression.
     * y = alpha + beta * x
     * 
     * @param history List of historical values (ordered by time)
     * @return Predicted next value
     */
    public double predictNextValue(List<Double> history) {
        int n = history.size();
        if (n < 2)
            return n == 1 ? history.get(0) : 0.0; // Not enough data

        double sumX = 0;
        double sumY = 0;
        double sumXY = 0;
        double sumX2 = 0;

        for (int i = 0; i < n; i++) {
            double x = i + 1; // Time step 1, 2, 3...
            double y = history.get(i);

            sumX += x;
            sumY += y;
            sumXY += (x * y);
            sumX2 += (x * x);
        }

        double denominator = (n * sumX2) - (sumX * sumX);
        if (denominator == 0)
            return history.get(n - 1); // Flat line fallback

        double beta = ((n * sumXY) - (sumX * sumY)) / denominator;
        double alpha = (sumY - (beta * sumX)) / n;

        // Predict for next time step (n + 1)
        return alpha + (beta * (n + 1));
    }

    /**
     * Calculates the average value from a list.
     */
    public double calculateAverage(List<Double> values) {
        if (values.isEmpty())
            return 0.0;
        double sum = 0;
        for (Double v : values)
            sum += v;
        return sum / values.size();
    }
}
