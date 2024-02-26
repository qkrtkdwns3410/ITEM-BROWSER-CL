package com.psj.itembrowser.cart.domain.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.psj.itembrowser.security.common.BaseDateTimeEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "cart")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartEntity extends BaseDateTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;
	
	@Column(name = "user_email", nullable = false, unique = true)
	private String userEmail;
	
	@OneToMany(mappedBy = "cartEntity", cascade = CascadeType.PERSIST)
	private List<CartProductRelationEntity> cartProductRelations = new ArrayList<>();
	
	@Builder
	private CartEntity(Long id, String userEmail, List<CartProductRelationEntity> cartProductRelations, LocalDateTime deletedDate) {
		this.id = id;
		this.userEmail = userEmail;
		this.cartProductRelations = cartProductRelations == null ? new ArrayList<>() : cartProductRelations;
		this.deletedDate = deletedDate;
	}
	
	public void addCartProductRelation(CartProductRelationEntity cartProductRelation) {
		if (cartProductRelations == null) {
			cartProductRelations = new ArrayList<>();
		}
		
		this.cartProductRelations.add(cartProductRelation);
		
		if (cartProductRelation.getCartEntity() != this) {
			cartProductRelation.setCartEntity(this); // 반대편 엔티티에도 자신을 설정
		}
	}
}