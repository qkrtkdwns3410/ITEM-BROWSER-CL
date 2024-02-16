package com.psj.itembrowser.cart.domain.vo;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "cart")
public class CartEntity {
	@Id
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "user_id")
	private String userId;

	@OneToMany(mappedBy = "cartEntity", cascade = CascadeType.PERSIST)
	private Set<CartProductRelationEntity> cartProductRelations = new LinkedHashSet<>();

	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@Column(name = "updated_date")
	private LocalDateTime updatedDate;

	@Column(name = "deleted_date")
	private LocalDateTime deletedDate;

}