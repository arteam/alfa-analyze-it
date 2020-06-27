package com.github.arteam.analyze_it;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
@JsonDeserialize(as = ImmutableUserPayment.class)
public interface UserPayment {

    String ref();

    String userId();

    long categoryId();

    String desc();

    BigDecimal amount();
}
