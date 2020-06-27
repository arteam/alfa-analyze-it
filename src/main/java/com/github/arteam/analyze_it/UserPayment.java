package com.github.arteam.analyze_it;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUserPayment.class)
public interface UserPayment {

    String ref();

    String userId();

    String recipientId();

    String desc();

    double amount();
}
