package com.psj.itembrowser.order.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.psj.itembrowser.member.domain.entity.MemberEntity;
import com.psj.itembrowser.member.domain.vo.Address;
import com.psj.itembrowser.member.domain.vo.Credentials;
import com.psj.itembrowser.member.domain.vo.Gender;
import com.psj.itembrowser.member.domain.vo.Member;
import com.psj.itembrowser.member.domain.vo.MemberShipType;
import com.psj.itembrowser.member.domain.vo.Name;
import com.psj.itembrowser.member.domain.vo.Role;
import com.psj.itembrowser.member.domain.vo.Status;
import com.psj.itembrowser.order.domain.entity.OrderEntity;
import com.psj.itembrowser.order.domain.vo.Order;
import com.psj.itembrowser.order.domain.vo.OrderStatus;
import com.psj.itembrowser.order.domain.vo.OrdersProductRelation;
import com.psj.itembrowser.order.mapper.OrderMapper;
import com.psj.itembrowser.order.persistence.OrderPersistence;
import com.psj.itembrowser.product.domain.vo.DeliveryFeeType;
import com.psj.itembrowser.product.domain.vo.Product;
import com.psj.itembrowser.product.domain.vo.ProductStatus;
import com.psj.itembrowser.security.data.config.MemberRepository;
import com.psj.itembrowser.security.data.config.OrderRepository;
import com.psj.itembrowser.security.data.config.ShippingInfoRepository;
import com.psj.itembrowser.shippingInfos.domain.entity.ShippingInfoEntity;
import com.psj.itembrowser.shippingInfos.domain.vo.ShippingInfo;

@DataJpaTest
@ActiveProfiles("test")
@Import({OrderPersistence.class})
public class OrderSelectWithDBServiceTest {
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private MemberRepository memberRepository;
	
	@Autowired
	private ShippingInfoRepository shippingInfoRepository;
	
	@Autowired
	private OrderPersistence orderPersistence;
	
	@MockBean
	private OrderMapper orderMapper;
	
	private Long validOrderId;
	
	private Long invalidOrderId;
	
	private OrderEntity validOrder;
	
	@BeforeEach
	public void setUp() {
		validOrderId = 1L;
		invalidOrderId = 2L;
		
		Member member = new Member(1L,
			Credentials.create("test@test.com", "test"),
			Name.create("홍", "길동"),
			"010-1234-1234",
			Gender.MEN,
			Role.ROLE_CUSTOMER, Status.ACTIVE,
			MemberShipType.REGULAR,
			Address.create("서울시 강남구", "김밥빌딩 101동 302호", "01012"),
			LocalDate.of(1995, 11, 3),
			LocalDateTime.now());
		
		MemberEntity expectedMember = MemberEntity.from(member, null, null);
		
		ShippingInfo expectedShppingInfo = new ShippingInfo(1L,
			1L,
			"홍길동",
			"test",
			"test",
			"010-1235-1234",
			"01111",
			"010-1234-1234", "test",
			LocalDateTime.now(),
			null,
			null);
		
		ShippingInfoEntity expectedShippingInfoEntity = ShippingInfoEntity.from(expectedShppingInfo);
		
		OrdersProductRelation expectedOrderRelation = OrdersProductRelation.of(1L, 1L, 1,
			LocalDateTime.now(),
			null,
			null,
			new Product(
				1L,
				"섬유유연제",
				1,
				"상품 디테일",
				ProductStatus.APPROVED,
				10,
				1000,
				"qkrtkdwns3410",
				LocalDateTime.now(),
				LocalDateTime.now().plusDays(10),
				"섬유유연제",
				"섬유나라",
				DeliveryFeeType.FREE,
				"배송방법",
				5000,
				15000,
				"returnCenterCode",
				Collections.emptyList(),
				Collections.emptyList()
			));
		
		this.validOrder = OrderEntity.from(Order.of(
			null,
			1L,
			OrderStatus.ACCEPT,
			LocalDateTime.now(),
			1L,
			LocalDateTime.now(),
			null,
			null,
			List.of(
				expectedOrderRelation
			),
			null,
			null
		));
		
		memberRepository.save(expectedMember);
		shippingInfoRepository.save(expectedShippingInfoEntity);
	}
	
	@Test
	@DisplayName("주문에 대한 단건 조회의 경우 메서드가 정상적으로 Order 를 반환하는지")
	void test() {
		//given
		OrderEntity saved = orderRepository.save(validOrder);
		
		//when
		OrderEntity orderWithNotDeleted = orderPersistence.getOrderWithNotDeleted(saved.getId());
		
		//then
		assertThat(orderWithNotDeleted.getId()).isEqualTo(saved.getId());
		assertThat(orderWithNotDeleted.getOrderStatus()).isEqualTo(saved.getOrderStatus());
		assertThat(orderWithNotDeleted.getPaidDate()).isEqualTo(saved.getPaidDate());
		assertThat(orderWithNotDeleted.getCreatedDate()).isEqualTo(saved.getCreatedDate());
		assertThat(orderWithNotDeleted.getUpdatedDate()).isEqualTo(saved.getUpdatedDate());
		assertThat(orderWithNotDeleted.getDeletedDate()).isEqualTo(saved.getDeletedDate());
	}
	
	@Test
	@DisplayName("주문 조회 시 주문 정보가 없을 경우 NotFoundException 발생")
	void When_GetOrder_Expect_ThrowNotFoundException() {
		//given
		
	}
}