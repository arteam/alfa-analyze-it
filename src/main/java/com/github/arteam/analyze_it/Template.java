package com.github.arteam.analyze_it;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
@JsonSerialize(as = ImmutableTemplate.class)
public interface Template {
    String recipientId();
    long categoryId();
    BigDecimal amount();
}
