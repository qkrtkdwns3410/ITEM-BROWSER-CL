package com.psj.itembrowser.security.common;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.psj.itembrowser.config.annotation.ServiceWithDBTest;
import com.psj.itembrowser.member.domain.entity.MemberEntity;
import com.psj.itembrowser.member.domain.vo.Address;
import com.psj.itembrowser.member.domain.vo.Credentials;
import com.psj.itembrowser.member.domain.vo.Name;

@ServiceWithDBTest
@DisplayName("BaseTimeEntity 클래스 테스트")
class BaseTimeEntityTest {
	
	@Autowired
	private TestEntityManager em;
	
	private static final LocalDateTime NOW = LocalDateTime.now();
	
	@Test
	@DisplayName("생성일자, 수정일자가 정상적으로 생성되는지 확인한다.")
	void When_CreatedDate_UpdatedDate_DeletedDate_Then_Success() {
		// given
		
		MemberEntity member = MemberEntity.builder()
			.address(
				Address.builder()
					.addressMain("서울시 강남구 역삼동 123-456번지")
					.addressSub("빌딩 7층 701호")
					.zipCode("12345")
					.build()
			)
			.credentials(
				Credentials.builder()
					.email("qkrtkdwns3410@gmail.com")
					.password("JavaHelper123!@#")
					.build()
			)
			.name(
				Name.builder()
					.firstName("홍")
					.lastName("길동")
					.build()
			)
			.build();
		// when
		MemberEntity flushedMember = em.persistFlushFind(member);
		
		// then
		assertThat(flushedMember.getCreatedDate()).isNotNull().isAfter(NOW);
		assertThat(flushedMember.getUpdatedDate()).isNotNull().isAfter(NOW);
		assertThat(flushedMember.getDeletedDate()).isNull();
	}
}