package com.psj.itembrowser.shippingInfos.domain.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import com.psj.itembrowser.member.domain.entity.MemberEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * packageName    : com.psj.itembrowser.shippingInfos.domain.vo fileName       : ShippingPolicy author         : ipeac date           : 2024-02-13 description    : =========================================================== DATE              AUTHOR             NOTE ----------------------------------------------------------- 2024-02-13        ipeac       최초 생성
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShippingPolicy implements Serializable {
	
	/**
	 * default delivery fee
	 */
	private DeliveryFee deliveryDefaultFee;
	/**
	 * free shipping over amount
	 */
	private Integer freeShipOverAmount;
	
	@Builder
	private ShippingPolicy(DeliveryFee deliveryDefaultFee, Integer freeShipOverAmount) {
		this.deliveryDefaultFee = deliveryDefaultFee;
		this.freeShipOverAmount = freeShipOverAmount;
	}
	
	public DeliveryFee calculateShippingFee(BigDecimal totalPrice, @NonNull MemberEntity member) {
		Objects.requireNonNull(deliveryDefaultFee, "deliveryDefaultFee must not be null");
		Objects.requireNonNull(freeShipOverAmount, "freeShipOverAmount must not be null");
		
		if (totalPrice.compareTo(BigDecimal.valueOf(freeShipOverAmount)) > 0 || member.isWowMember()) {
			return DeliveryFee.FREE;
		}
		return DeliveryFee.DEFAULT;
	}
	
	@Getter
	public enum DeliveryFee {
		FREE(0), DEFAULT(3000);
		
		private final String name;
		private final int fee;
		
		DeliveryFee(int fee) {
			this.name = name();
			this.fee = fee;
		}
	}
}