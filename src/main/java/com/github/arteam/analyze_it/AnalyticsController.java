package com.github.arteam.analyze_it;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class AnalyticsController {

    private RawPaymentsConsumer rawPaymentsConsumer;

    @Autowired
    public AnalyticsController(RawPaymentsConsumer rawPaymentsConsumer) {
        this.rawPaymentsConsumer = rawPaymentsConsumer;
    }

    @GetMapping("analytic")
    public List<UserAnalytics> reportAnalytics() {
        return rawPaymentsConsumer.getUserPayments()
                .stream()
                .collect(Collectors.groupingBy(up -> up.userId()))
                .entrySet()
                .stream()
                .map(e -> Map.entry(e.getKey(), e.getValue().stream()
                        .collect(Collectors.groupingBy(up -> up.recipientId()))
                        .entrySet().stream()
                        .map(ce -> Map.entry(ce.getKey(), processUserPayments(ce.getValue())))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)).entrySet().stream()
                .map(e -> ImmutableUserAnalytics.builder().userId(e.getKey()).analyticInfo(e.getValue())
                        .totalSum(e.getValue().entrySet().stream().mapToDouble(ue -> ue.getValue().sum()).sum())
                        .build())
                .collect(Collectors.toList());
    }

    @GetMapping("analytic/{userId}")
    public ResponseEntity<UserAnalytics> reportAnalyticsByUserId(@PathVariable("userId") String userId) {
        Optional<UserAnalytics> userAnalytics = reportAnalytics().stream()
                .filter(u -> u.userId().equals(userId))
                .findAny();
        return ResponseEntity.of(userAnalytics);
    }

    AnalyticsInfo processUserPayments(List<UserPayment> userPayments) {
        double min = userPayments.stream().mapToDouble(up -> up.amount()).min().orElse(0);
        double max = userPayments.stream().mapToDouble(up -> up.amount()).min().orElse(0);
        double sum = userPayments.stream().mapToDouble(up -> up.amount()).sum();
        return ImmutableAnalyticsInfo.builder()
                .min(min)
                .max(max)
                .sum(sum)
                .build();
    }
}
