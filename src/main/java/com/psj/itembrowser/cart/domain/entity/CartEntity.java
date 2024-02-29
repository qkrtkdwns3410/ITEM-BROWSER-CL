package com.psj.itembrowser.cart.domain.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.proxy.HibernateProxy;

import com.psj.itembrowser.security.common.BaseDateTimeEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

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
	
	@OneToMany(mappedBy = "cartEntity", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
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
	
	public static CartEntity from(@NonNull String userEmail) {
		return CartEntity.builder()
			.userEmail(userEmail)
			.build();
	}
	
	@Override
	public final boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null)
			return false;
		Class<?> oEffectiveClass =
			o instanceof HibernateProxy ? ((HibernateProxy)o).getHibernateLazyInitializer().getPersistentClass() :
				o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy ?
			((HibernateProxy)this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
		if (thisEffectiveClass != oEffectiveClass)
			return false;
		CartEntity entity = (CartEntity)o;
		return getId() != null && Objects.equals(getId(), entity.getId());
	}
	
	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy ? ((HibernateProxy)this).getHibernateLazyInitializer().getPersistentClass().hashCode() :
			getClass().hashCode();
	}
}