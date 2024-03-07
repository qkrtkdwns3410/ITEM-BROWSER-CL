package com.psj.itembrowser.order.domain.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.psj.itembrowser.order.domain.dto.request.OrderExchageRequestDTO;
import com.psj.itembrowser.security.common.BaseDateTimeEntity;
import com.psj.itembrowser.security.common.exception.BadRequestException;
import com.psj.itembrowser.security.common.exception.ErrorCode;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *packageName    : com.psj.itembrowser.order.domain.entity
 * fileName       : ExchangeOrderEntity
 * author         : ipeac
 * date           : 2024-03-07
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-03-07        ipeac       최초 생성
 */
@Entity
@Table(name = "exchange_order")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ExchangeOrderEntity extends BaseDateTimeEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private Long orderId;
	
	@Column(nullable = false)
	private Long productId;
	
	@Column(nullable = false)
	private Integer requestedQuantity;
	
	@Column(nullable = false, length = 2000)
	private String reason;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ExchangeStatus status;
	
	@Column(length = 500)
	private String customerNote;
	
	@Column(length = 500)
	private String adminNote;
	
	@Builder
	private ExchangeOrderEntity(Long id, Long orderId, Long productId, Integer requestedQuantity, String reason, ExchangeStatus status,
		String customerNote, String adminNote, LocalDateTime deletedDate) {
		super.deletedDate = deletedDate;
		
		this.id = id;
		this.orderId = orderId;
		this.productId = productId;
		this.requestedQuantity = requestedQuantity;
		this.reason = reason;
		this.status = status;
		this.customerNote = customerNote;
		this.adminNote = adminNote;
	}
	
	public static ExchangeOrderEntity from(OrderExchageRequestDTO requestDTO) {
		if (requestDTO == null) {
			throw new BadRequestException(ErrorCode.INVALID_REQUEST);
		}
		
		return ExchangeOrderEntity.builder()
			.orderId(requestDTO.getExchangeOrderId())
			.productId(requestDTO.getExchangeOrderProductId())
			.requestedQuantity(requestDTO.getExchangeQuantity())
			.reason(requestDTO.getExchangeReason())
			.status(ExchangeStatus.REQUESTED)
			.build();
	}
}