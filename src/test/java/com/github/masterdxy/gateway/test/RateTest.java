package com.github.masterdxy.gateway.test;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Date;

public class RateTest {
    @Test void testRateLimit() throws InterruptedException {
        Refill refill = Refill.greedy(1, Duration.ofSeconds(5));
        Bandwidth limit = Bandwidth.classic(2, refill);
        Bucket bucket = Bucket4j.builder().addLimit(limit).build();

        for (; ; ) {
            ConsumptionProbe consumptionProbe = bucket.tryConsumeAndReturnRemaining(1);
            System.out.println(
                new Date() + " consumed : " + consumptionProbe.isConsumed() + ", remain : " + consumptionProbe
                                                                                                  .getRemainingTokens());
            Thread.sleep(1000);
        }
    }
}
