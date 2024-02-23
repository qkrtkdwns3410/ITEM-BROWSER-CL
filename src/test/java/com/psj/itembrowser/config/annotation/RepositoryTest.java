package com.psj.itembrowser.config.annotation;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.psj.itembrowser.config.TestQueryDslConfig;

/**
 *packageName    : com.psj.itembrowser.config.annotation
 * fileName       : RepositoryTest
 * author         : ipeac
 * date           : 2024-02-22
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-02-22        ipeac       최초 생성
 */

// 테스트용 어노테이션 생성
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@AutoConfigureTestDatabase(replace = NONE)
@Import(TestQueryDslConfig.class)
@DataJpaTest
public @interface RepositoryTest {
}