package com.github.arteam.analyze_it;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.util.Map;

@Value.Immutable
@JsonSerialize(as = ImmutableUserAnalytics.class)
public interface UserAnalytics {
    String userId();

    double totalSum();

    Map<String, AnalyticsInfo> analyticInfo();
}
