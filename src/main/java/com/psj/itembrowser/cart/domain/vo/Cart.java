package com.psj.itembrowser.cart.domain.vo;

import java.util.List;

import com.psj.itembrowser.security.common.BaseDateTimeEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(of = {"id", "userId"}, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Cart extends BaseDateTimeEntity {

	/**
	 * pk값
	 */
	Long id;

	/**
	 * 유저ID
	 */
	String userId;

	List<CartProductRelation> cartProductRelations;
}