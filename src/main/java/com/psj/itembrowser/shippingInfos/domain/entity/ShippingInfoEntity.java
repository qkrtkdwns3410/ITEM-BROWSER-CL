package com.psj.itembrowser.shippingInfos.domain.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import com.psj.itembrowser.member.domain.entity.MemberEntity;
import com.psj.itembrowser.order.domain.entity.OrderEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "shipping_infos")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
public class ShippingInfoEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MEMBER_EMAIL", referencedColumnName = "EMAIL")
	private MemberEntity memberEmail;

	@Size(max = 45)
	@Column(name = "RECEIVER", length = 45)
	private String receiver;

	@Size(max = 45)
	@Column(name = "MAIN_ADDRESS", length = 45)
	private String mainAddress;

	@Size(max = 45)
	@Column(name = "SUB_ADDRESS", length = 45)
	private String subAddress;

	@Size(max = 45)
	@Column(name = "PHONE_NUMBER", length = 45)
	private String phoneNumber;

	@Column(name = "ALTERNATIVE_NUMBER")
	private Integer alternativeNumber;

	@Size(max = 200)
	@Column(name = "SHIPPING_REQUEST_MSG", length = 200)
	private String shippingRequestMsg;

	@Column(name = "CREATED_DATE")
	private LocalDateTime createdDate;

	@Column(name = "UPDATED_DATE")
	private LocalDateTime updatedDate;

	@Column(name = "DELETED_DATE")
	private LocalDateTime deletedDate;

	@OneToMany(mappedBy = "shippingInfo")
	private List<OrderEntity> orders = new ArrayList<>();
}