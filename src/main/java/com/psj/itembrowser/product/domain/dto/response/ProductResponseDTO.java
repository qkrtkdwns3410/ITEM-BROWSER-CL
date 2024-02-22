package com.psj.itembrowser.product.domain.dto.response;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.psj.itembrowser.product.domain.entity.ProductEntity;
import com.psj.itembrowser.product.domain.vo.Product;
import com.psj.itembrowser.product.domain.vo.ProductStatus;
import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.security.common.exception.NotFoundException;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponseDTO {
	
	private Long id;
	
	private String name;
	
	private Integer category;
	
	private String detail;
	
	private ProductStatus status;
	
	private Integer quantity;
	
	private Integer unitPrice;
	
	private String sellerId;
	
	private LocalDateTime sellStartDatetime;
	
	private LocalDateTime sellEndDatetime;
	
	private String displayName;
	
	private String brand;
	
	private String deliveryMethod;
	
	private Integer deliveryDefaultFee;
	
	private Integer freeShipOverAmount;
	
	private String returnCenterCode;
	
	private List<ProductImageResponseDTO> productImages;
	
	public static ProductResponseDTO from(Product product) {
		if (product == null) {
			throw new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND);
		}
		
		List<ProductImageResponseDTO> productImages = Optional.ofNullable(product.getProductImages())
			.map(images -> images.stream()
				.map(ProductImageResponseDTO::from)
				.collect(Collectors.toUnmodifiableList())
			).orElse(Collections.emptyList());
		
		return ProductResponseDTO.builder()
			.id(product.getId())
			.name(product.getName())
			.category(product.getCategory())
			.detail(product.getDetail())
			.status(product.getStatus())
			.quantity(product.getQuantity())
			.unitPrice(product.getUnitPrice())
			.sellerId(product.getSellerId())
			.sellStartDatetime(product.getSellStartDatetime())
			.sellEndDatetime(product.getSellEndDatetime())
			.displayName(product.getDisplayName())
			.brand(product.getBrand())
			.deliveryMethod(product.getDeliveryMethod())
			.deliveryDefaultFee(product.getDeliveryDefaultFee())
			.freeShipOverAmount(product.getFreeShipOverAmount())
			.returnCenterCode(product.getReturnCenterCode())
			.productImages(productImages)
			.build();
	}
	
	public static ProductResponseDTO from(ProductEntity entity) {
		if (entity == null) {
			throw new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND);
		}
		
		List<ProductImageResponseDTO> productImages = Optional.ofNullable(entity.getProductImages())
			.map(images -> images.stream()
				.map(ProductImageResponseDTO::from)
				.collect(Collectors.toUnmodifiableList())
			).orElse(Collections.emptyList());
		
		return ProductResponseDTO.builder()
			.id(entity.getId())
			.name(entity.getName())
			.category(entity.getCategory())
			.detail(entity.getDetail())
			.status(entity.getStatus())
			.quantity(entity.getQuantity())
			.unitPrice(entity.getUnitPrice())
			.sellerId(entity.getSellerId())
			.sellStartDatetime(entity.getSellStartDatetime())
			.sellEndDatetime(entity.getSellEndDatetime())
			.displayName(entity.getDisplayName())
			.brand(entity.getBrand())
			.deliveryMethod(entity.getDeliveryMethod())
			.deliveryDefaultFee(entity.getDeliveryDefaultFee())
			.freeShipOverAmount(entity.getFreeShipOverAmount())
			.returnCenterCode(entity.getReturnCenterCode())
			.productImages(productImages)
			.build();
	}
}