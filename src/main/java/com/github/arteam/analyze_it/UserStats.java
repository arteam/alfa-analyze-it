package com.github.arteam.analyze_it;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableUserStats.class)
public interface UserStats {

    long oftenCategoryId();
    long rareCategoryId();
    long maxAmountCategoryId();
    long minAmountCategoryId();
}
