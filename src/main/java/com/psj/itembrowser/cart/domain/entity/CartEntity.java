package com.psj.itembrowser.cart.domain.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "cart")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CartEntity {
	@Id
	@Column(name = "id", nullable = false)
	private Long id;
	
	@Column(name = "user_id")
	private String userId;
	
	@OneToMany(mappedBy = "cartEntity", cascade = CascadeType.PERSIST)
	private List<CartProductRelationEntity> cartProductRelations = new ArrayList<>();
	
	@Column(name = "created_date")
	private LocalDateTime createdDate;
	
	@Column(name = "updated_date")
	private LocalDateTime updatedDate;
	
	@Column(name = "deleted_date")
	private LocalDateTime deletedDate;
}