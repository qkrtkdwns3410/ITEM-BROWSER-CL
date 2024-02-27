package com.psj.itembrowser.security.common.config.querydsl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 *packageName    : com.psj.itembrowser.security.common.config.querydsl
 * fileName       : QuerydslConfig
 * author         : ipeac
 * date           : 2024-02-26
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-02-26        ipeac       최초 생성
 */
@Configuration
public class QuerydslConfig {
	
	@PersistenceContext
	private EntityManager em;
	
	@Bean
	public JPAQueryFactory jpaQueryFactory() {
		return new JPAQueryFactory(em);
	}
}