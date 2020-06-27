package com.github.arteam.analyze_it;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableStatusResponse.class)
public interface StatusResponse {
    String status();
}
