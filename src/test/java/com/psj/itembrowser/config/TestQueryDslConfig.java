package com.psj.itembrowser.config;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.psj.itembrowser.order.repository.CustomOrderRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 *packageName    : com.psj.itembrowser.config
 * fileName       : TestQueryDslConfig
 * author         : ipeac
 * date           : 2024-02-22
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-02-22        ipeac       최초 생성
 */
@TestConfiguration
public class TestQueryDslConfig {
	@PersistenceContext
	private EntityManager em;
	
	@Bean
	public JPAQueryFactory jpaQueryFactory() {
		return new JPAQueryFactory(em);
	}
	
	@Bean
	public CustomOrderRepository customOrderRepository() {
		return new CustomOrderRepository();
	}
}