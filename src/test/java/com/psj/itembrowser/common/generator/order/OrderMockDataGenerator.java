package com.psj.itembrowser.common.generator.order;

import java.time.LocalDateTime;
import java.util.List;

import com.psj.itembrowser.member.domain.vo.Member;
import com.psj.itembrowser.order.domain.vo.Order;
import com.psj.itembrowser.order.domain.vo.OrderStatus;
import com.psj.itembrowser.order.domain.vo.OrdersProductRelation;
import com.psj.itembrowser.product.domain.vo.Product;
import com.psj.itembrowser.shippingInfos.domain.vo.ShippingInfo;

/**
 * packageName    : com.psj.itembrowser.cart.data
 * fileName       : CartMockDataGenerator
 * author         : ipeac
 * date           : 2023-10-22
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-10-22        ipeac       최초 생성
 */
public class OrderMockDataGenerator {
	public static Order createOrder(
		Long id,
		Long ordererNo,
		OrderStatus orderStatus,
		LocalDateTime paidDate,
		Long shippingInfoId,
		LocalDateTime createdDate,
		List<OrdersProductRelation> ordersProductRelations,
		Member member,
		ShippingInfo shippingInfo
	) {
		return Order.builder()
			.id(id)
			.ordererNo(ordererNo)
			.orderStatus(orderStatus)
			.paidDate(paidDate)
			.shippingInfoId(shippingInfoId)
			.createdDate(createdDate)
			.updatedDate(null)
			.deletedDate(null)
			.products(ordersProductRelations)
			.member(member)
			.shippingInfo(shippingInfo)
			.build();
	}
	
	public static OrdersProductRelation createOrdersProductRelation(
		Long groupId,
		Long productId,
		int productQuantity,
		Product product
	) {
		return OrdersProductRelation.create(
			groupId,
			productId,
			productQuantity,
			LocalDateTime.now(),
			null,
			null,
			product
		);
	}
}