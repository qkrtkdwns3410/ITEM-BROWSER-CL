package com.psj.itembrowser.member.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

import com.psj.itembrowser.member.domain.vo.Role;
import com.psj.itembrowser.member.domain.vo.Status;
import com.psj.itembrowser.member.factory.MockMemberSecurityContextFactory;

/**
 * packageName    : com.psj.itembrowser.member.annotation
 * fileName       : MockMember
 * author         : ipeac
 * date           : 2024-02-01
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-02-01        ipeac       최초 생성
 */
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = MockMemberSecurityContextFactory.class)
public @interface MockMember {
	String email() default "mockMember3410@gmail.com";

	Role role() default Role.ROLE_CUSTOMER;

	Status status() default Status.ACTIVE;

}