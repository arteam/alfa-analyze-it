package com.github.arteam.analyze_it;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableAnalyticsInfo.class)
public interface AnalyticsInfo {
    double min();

    double max();

    double sum();
}
