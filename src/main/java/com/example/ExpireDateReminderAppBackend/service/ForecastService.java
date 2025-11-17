package com.example.ExpireDateReminderAppBackend.service;

import com.example.ExpireDateReminderAppBackend.dto.PredictionRequest;
import com.example.ExpireDateReminderAppBackend.dto.PredictionResponse;
import com.github.signaflo.timeseries.TimePeriod;
import com.github.signaflo.timeseries.TimeSeries;
import com.github.signaflo.timeseries.forecast.Forecast;
import com.github.signaflo.timeseries.model.arima.Arima;
import com.github.signaflo.timeseries.model.arima.ArimaOrder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 0H02009_カゥンセッリン
 */

@Service
public class ForecastService {

    public int[] forecast(double[] history, int months) {
        TimeSeries series = TimeSeries.from(TimePeriod.oneMonth(), history);

        // ARIMA(1,1,1) order
        ArimaOrder order = ArimaOrder.order(1, 1, 1);

        // Fit the model (this is correct for v0.4)
        Arima model = Arima.model(series, order);

        Forecast forecast = model.forecast(months);

        double[] predictedResult = forecast.pointEstimates().asArray();

        // Convert double to int (round off decimal point)
        return Arrays.stream(predictedResult)
                .mapToInt(x -> (int) Math.round(x))
                .toArray();
    }

    public PredictionResponse analyzePrediction(PredictionRequest request) {
        int[] consumedPrediction = forecast(
                request.getConsumedHistory(),
                request.getMonths()
        );

        int[] wastedPrediction = forecast(
                request.getWastedHistory(),
                request.getMonths()
        );

        List<String> suggestions = generateSuggestions(
                request.getWasteByCategory(),
                consumedPrediction,
                wastedPrediction
        );

        return PredictionResponse.builder()
                .consumedPrediction(consumedPrediction)
                .wastedPrediction(wastedPrediction)
                .suggestions(suggestions)
                .build();
    }

    public List<String> generateSuggestions(
            Map<String, Integer> wasteByCategory,
            int[] consumedPrediction,
            int[] wastedPrediction
    ) {
        List<String> result = new ArrayList<>();

        // ===== Basic calculations =====
        int totalWaste = 0;
        if (wasteByCategory != null) {
            for (int v : wasteByCategory.values()) totalWaste += v;
        }

        // Waste slope (trend)
        double wasteSlope = 0;
        if (wastedPrediction.length >= 2) {
            wasteSlope = wastedPrediction[wastedPrediction.length - 1]
                    - wastedPrediction[0];
        }

        // Consumption vs waste gap
        double consumptionWasteGap = 0;
        if (consumedPrediction.length > 0 && wastedPrediction.length > 0) {
            consumptionWasteGap =
                    average(consumedPrediction) - average(wastedPrediction);
        }

        // ===== 1. Trend suggestion (1 line max) =====
        if (wasteSlope > 0) {
            result.add("廃棄量が増加傾向にあります。購入量を少し抑えると改善できます。");
        } else if (wasteSlope < -3) {
            result.add("廃棄量が減少傾向です。この調子で管理を続けましょう。");
        }

        // ===== 2. Consumption–waste balance (1 line max) =====
        if (consumptionWasteGap < 0) {
            result.add("消費量より廃棄量が多い傾向があります。買いすぎにご注意ください。");
        }

        // ===== 3. Compact category-level analysis =====
        if (wasteByCategory != null && wasteByCategory.size() > 0) {

            // Sort categories by waste descending
            List<Map.Entry<String, Integer>> sorted = wasteByCategory.entrySet()
                    .stream()
                    .filter(e -> e.getValue() > 0)
                    .sorted((a, b) -> b.getValue() - a.getValue())
                    .toList();

            List<String> topCategories = new ArrayList<>();
            List<String> smallCategories = new ArrayList<>();

            // Pick top 2 categories; others go to "small"
            for (int i = 0; i < sorted.size(); i++) {
                var entry = sorted.get(i);
                // Wrap category names with <strong> tags
                String boldCategory = "<strong>" + entry.getKey() + "</strong>";
                if (i < 2) {
                    topCategories.add(boldCategory);
                } else {
                    smallCategories.add(boldCategory);
                }
            }

            // Top 1–2 categories → individual sentence
            if (!topCategories.isEmpty()) {
                result.add(
                        String.join("と", topCategories) +
                                "の廃棄が特に多い傾向です。購入量を見直すと効果的です。"
                );
            }

            // Small categories → grouped into one short line
            if (!smallCategories.isEmpty()) {
                result.add(
                        String.join("・", smallCategories) +
                                "も少量の廃棄が出ています。無理のない範囲で調整してみるのもおすすめです。"
                );
            }
        }

        // ===== 4. No suggestion case =====
        if (result.isEmpty()) {
            result.add("現在の食品管理は非常に良好です！引き続きこの調子で管理してください。");
        }

        return result;
    }

    private double average(int[] arr) {
        if (arr.length == 0) return 0;
        double sum = 0;
        for (int v : arr) sum += v;
        return sum / arr.length;
    }
}
