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
import java.util.function.Function;
import java.util.stream.Collector;
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
        System.out.println("Get analytics");
        return rawPaymentsConsumer.getUserPayments()
                .stream()
                .collect(Collectors.groupingBy(up -> up.userId()))
                .entrySet()
                .stream()
                .map(e -> Map.entry(e.getKey(), e.getValue().stream()
                        .collect(Collectors.groupingBy(up -> up.categoryId()))
                        .entrySet().stream()
                        .map(ce -> Map.entry(ce.getKey(), processUserPayments(ce.getValue())))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)).entrySet().stream()
                .map(e -> ImmutableUserAnalytics.builder().userId(e.getKey()).analyticInfo(e.getValue())
                        .totalSum(e.getValue().entrySet().stream().map(ue -> ue.getValue().sum()).reduce(BigDecimal.ZERO, BigDecimal::add))
                        .build())
                .collect(Collectors.toList());
    }

    @GetMapping("analytic/{userId}")
    public ResponseEntity<?> reportAnalyticsByUserId(@PathVariable("userId") String userId) {
        System.out.println("Get analytics by user " + userId);
        Optional<UserAnalytics> userAnalytics = reportAnalytics().stream()
                .filter(u -> u.userId().equals(userId))
                .findAny();
        if (userAnalytics.isPresent()) {
            return ResponseEntity.ok(userAnalytics.get());
        } else {
            return ResponseEntity.status(404).body(ImmutableStatusResponse.builder()
                    .status("user not found")
                    .build());
        }
    }

    @GetMapping("analytic/{userId}/templates")
    public ResponseEntity<?> reportAnalyticsTemplate(@PathVariable("userId") String userId) {
        System.out.println("Get analytics template by user " + userId);
        return ResponseEntity.status(404).build();
    }

    @GetMapping("analytic/{userId}/stats")
    public ResponseEntity<?> reportAnalyticsStats(@PathVariable("userId") String userId) {
        List<UserPayment> userPayments = rawPaymentsConsumer.getUserPayments();
        Map<Long, Long> categoriesFreq = userPayments
                .stream()
                .filter(up -> up.userId().equals(userId))
                .map(up -> up.categoryId())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        if (!categoriesFreq.isEmpty()) {
            long oftenCategoryId = categoriesFreq.entrySet()
                    .stream()
                    .max(Map.Entry.comparingByValue())
                    .get()
                    .getKey();
            long rareCategoryId = categoriesFreq.entrySet()
                    .stream()
                    .min(Map.Entry.comparingByValue())
                    .get()
                    .getKey();
            Map<Long, BigDecimal> categoriesAmount = userPayments.stream()
                    .filter(up -> up.userId().equals(userId))
                    .collect(Collectors.groupingBy(up -> up.categoryId()))
                    .entrySet()
                    .stream()
                    .map(e -> Map.entry(e.getKey(), e.getValue().stream().map(up -> up.amount()).reduce(BigDecimal.ZERO, BigDecimal::add)))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            long maxAmountCategoryId = categoriesAmount.entrySet()
                    .stream()
                    .max(Map.Entry.comparingByValue())
                    .get()
                    .getKey();
            long minAmountCategoryId = categoriesAmount.entrySet()
                    .stream()
                    .min(Map.Entry.comparingByValue())
                    .get()
                    .getKey();
            return ResponseEntity.ok(ImmutableUserStats.builder()
                    .oftenCategoryId(oftenCategoryId)
                    .rareCategoryId(rareCategoryId)
                    .maxAmountCategoryId(maxAmountCategoryId)
                    .minAmountCategoryId(minAmountCategoryId)
                    .build());
        } else {
            return ResponseEntity.status(404).body(ImmutableStatusResponse.builder()
                    .status("user not found")
                    .build());
        }
    }

    AnalyticsInfo processUserPayments(List<UserPayment> userPayments) {
        BigDecimal min = userPayments.stream().map(up -> up.amount()).min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        BigDecimal max = userPayments.stream().map(up -> up.amount()).max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        BigDecimal sum = userPayments.stream().map(up -> up.amount()).reduce(BigDecimal.ZERO, BigDecimal::add);
        return ImmutableAnalyticsInfo.builder()
                .min(min)
                .max(max)
                .sum(sum)
                .build();
    }
}
