package com.psj.itembrowser.security.common.openfeign.service;

import java.time.Duration;

import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;

/**
 *packageName    : com.psj.itembrowser.security.common.openfeign.service
 * fileName       : AddressApiOpenFeignConfig
 * author         : ipeac
 * date           : 2024-03-10
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-03-10        ipeac       최초 생성
 */
@Configuration
public class AddressApiOpenFeignConfig {
	
	@Bean
	public Customizer<Resilience4JCircuitBreakerFactory> globalCustomConfiguration() {
		return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
			.circuitBreakerConfig(CircuitBreakerConfig.custom()
				.slidingWindowSize(10)
				.minimumNumberOfCalls(5)
				.permittedNumberOfCallsInHalfOpenState(3)
				.automaticTransitionFromOpenToHalfOpenEnabled(true)
				.waitDurationInOpenState(Duration.ofSeconds(10))
				.failureRateThreshold(50)
				.recordException(e -> true)
				.build()
			)
			.timeLimiterConfig(
				TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(4)).build()
			)
			.build());
	}
}