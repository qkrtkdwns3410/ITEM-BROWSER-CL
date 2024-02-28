package com.psj.itembrowser.product.domain.vo;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.psj.itembrowser.product.domain.entity.ProductEntity;
import com.psj.itembrowser.security.common.BaseDateTimeEntity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_image")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ProductImageEntity extends BaseDateTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "product_id", nullable = false)
	private Long productId;
	
	@Column(name = "file_name", nullable = false)
	private String fileName;
	
	@Column(name = "file_path", nullable = false)
	private String filePath;
	
	@Column(name = "type", nullable = false, length = 50)
	private String type;
	
	@Column(name = "size", nullable = false)
	private Long size;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", insertable = false, updatable = false, referencedColumnName = "id")
	private ProductEntity product;
	
	@Builder
	private ProductImageEntity(Long id, Long productId, String fileName, String filePath, String type, Long size, LocalDateTime deletedDate,
		ProductEntity product) {
		super.deletedDate = deletedDate;
		
		this.id = id;
		this.productId = productId;
		this.fileName = fileName;
		this.filePath = filePath;
		this.type = type;
		this.size = size;
		this.product = product;
	}
}