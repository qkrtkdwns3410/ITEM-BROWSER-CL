package com.psj.itembrowser.product.domain.vo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.psj.itembrowser.cart.domain.vo.CartProductRelation;
import com.psj.itembrowser.product.domain.dto.request.ProductRequestDTO;
import com.psj.itembrowser.product.domain.dto.request.ProductUpdateDTO;
import com.psj.itembrowser.product.domain.dto.response.ProductResponseDTO;
import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.security.common.exception.NotFoundException;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {
	
	/**
	 * pk값
	 */
	private Long id;
	
	/**
	 * 상품명
	 */
	private String name;
	
	/**
	 * 상품카테고리. CATEGORY 테이블 참조
	 */
	private Integer category;
	
	/**
	 * 상품설명
	 */
	private String detail;
	
	/**
	 * 상품상태. 심사중/임시저장/승인대기/승인완료/부분승인/완료/승인반려/상품삭제
	 */
	private ProductStatus status;
	
	/**
	 * 재고
	 */
	private Integer quantity;
	
	/**
	 * 가격
	 */
	private Integer unitPrice;
	
	/**
	 * 판매자ID
	 */
	private String sellerId;
	
	/**
	 * 판매시작일시
	 */
	private LocalDateTime sellStartDatetime;
	
	/**
	 * 판매종료일시
	 */
	private LocalDateTime sellEndDatetime;
	
	/**
	 * 노출상품명. 실제노출되는 상품명
	 */
	private String displayName;
	
	/**
	 * 브랜드
	 */
	private String brand;
	
	/**
	 * 배송비종류. DELIVERY_FEE_TYPE 테이블 참조
	 */
	private DeliveryFeeType deliveryFeeType;
	
	/**
	 * 배송방법. DELIVERY_METHOD 테이블 참조
	 */
	private String deliveryMethod;
	
	/**
	 * 기본배송비. 기본 배송
	 */
	private Integer deliveryDefaultFee;
	
	/**
	 * 무료배송금액. 무료 배송 기준 금액
	 */
	private Integer freeShipOverAmount;
	
	/**
	 * 반품지 센터 코드. CENTER 테이블 참조
	 */
	private String returnCenterCode;
	
	private List<CartProductRelation> cartProductRelations;
	
	private List<ProductImage> productImages;
	
	private LocalDateTime createdDate;
	
	private LocalDateTime updatedDate;
	
	private LocalDateTime deletedDate;
	
	@Builder
	private Product(Long id, String name, Integer category, String detail, ProductStatus status, Integer quantity, Integer unitPrice, String sellerId,
		LocalDateTime sellStartDatetime, LocalDateTime sellEndDatetime, String displayName, String brand, DeliveryFeeType deliveryFeeType,
		String deliveryMethod, Integer deliveryDefaultFee, Integer freeShipOverAmount, String returnCenterCode,
		List<CartProductRelation> cartProductRelations, List<ProductImage> productImages, LocalDateTime createdDate, LocalDateTime updatedDate,
		LocalDateTime deletedDate) {
		this.id = id;
		this.name = name;
		this.category = category;
		this.detail = detail;
		this.status = status;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
		this.sellerId = sellerId;
		this.sellStartDatetime = sellStartDatetime;
		this.sellEndDatetime = sellEndDatetime;
		this.displayName = displayName;
		this.brand = brand;
		this.deliveryFeeType = deliveryFeeType;
		this.deliveryMethod = deliveryMethod;
		this.deliveryDefaultFee = deliveryDefaultFee;
		this.freeShipOverAmount = freeShipOverAmount;
		this.returnCenterCode = returnCenterCode;
		this.cartProductRelations = cartProductRelations == null ? new ArrayList<>() : cartProductRelations;
		this.productImages = productImages == null ? new ArrayList<>() : productImages;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
		this.deletedDate = deletedDate;
	}
	
	void addcartProductRelations(CartProductRelation cartProductRelation) {
		if (cartProductRelations == null) {
			cartProductRelations = new ArrayList<>();
		}
		
		this.cartProductRelations.add(cartProductRelation);
	}
	
	public void validateSellDates() {
		if (this.sellStartDatetime != null && this.sellEndDatetime != null && this.sellEndDatetime.isBefore(this.sellStartDatetime)) {
			throw new IllegalArgumentException("The sell start datetime must not be before the sell end datetime.");
		}
	}
	
	public static Product from(ProductResponseDTO productResponseDTO) {
		if (productResponseDTO == null) {
			throw new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND);
		}
		
		Product product = new Product();
		
		product.id = productResponseDTO.getId();
		product.name = productResponseDTO.getName();
		product.category = productResponseDTO.getCategory();
		product.detail = productResponseDTO.getDetail();
		product.status = productResponseDTO.getStatus();
		product.quantity = productResponseDTO.getQuantity();
		product.unitPrice = productResponseDTO.getUnitPrice();
		product.sellerId = productResponseDTO.getSellerId();
		product.sellStartDatetime = productResponseDTO.getSellStartDatetime();
		product.sellEndDatetime = productResponseDTO.getSellEndDatetime();
		product.displayName = productResponseDTO.getDisplayName();
		product.brand = productResponseDTO.getBrand();
		product.deliveryMethod = productResponseDTO.getDeliveryMethod();
		product.deliveryDefaultFee = productResponseDTO.getDeliveryDefaultFee();
		product.freeShipOverAmount = productResponseDTO.getFreeShipOverAmount();
		product.returnCenterCode = productResponseDTO.getReturnCenterCode();
		
		if (Objects.nonNull(productResponseDTO.getProductImages()) && !productResponseDTO.getProductImages().isEmpty()) {
			product.productImages = productResponseDTO.getProductImages()
				.stream()
				.map(ProductImage::from)
				.collect(Collectors.toList());
		}
		
		return product;
	}
	
	public static Product from(ProductRequestDTO productRequestDTO) {
		if (productRequestDTO == null) {
			throw new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND);
		}
		
		Product product = new Product();
		
		product.name = productRequestDTO.getName();
		product.category = productRequestDTO.getCategory();
		product.detail = productRequestDTO.getDetail();
		product.status = productRequestDTO.getStatus();
		product.quantity = productRequestDTO.getQuantity();
		product.unitPrice = productRequestDTO.getUnitPrice();
		product.sellerId = productRequestDTO.getSellerId();
		product.sellStartDatetime = productRequestDTO.getSellStartDatetime();
		product.sellEndDatetime = productRequestDTO.getSellEndDatetime();
		product.displayName = productRequestDTO.getDisplayName();
		product.brand = productRequestDTO.getBrand();
		product.deliveryFeeType = productRequestDTO.getDeliveryFeeType();
		product.deliveryMethod = productRequestDTO.getDeliveryMethod();
		product.deliveryDefaultFee = productRequestDTO.getDeliveryDefaultFee();
		product.freeShipOverAmount = productRequestDTO.getFreeShipOverAmount();
		product.returnCenterCode = productRequestDTO.getReturnCenterCode();
		
		return product;
	}
	
	public static Product from(ProductUpdateDTO productUpdateDTO) {
		if (productUpdateDTO == null) {
			throw new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND);
		}
		
		Product product = new Product();
		
		product.id = productUpdateDTO.getId();
		product.name = productUpdateDTO.getName();
		product.category = productUpdateDTO.getCategory();
		product.detail = productUpdateDTO.getDetail();
		product.status = productUpdateDTO.getStatus();
		product.quantity = productUpdateDTO.getQuantity();
		product.unitPrice = productUpdateDTO.getUnitPrice();
		product.sellerId = productUpdateDTO.getSellerId();
		product.sellStartDatetime = productUpdateDTO.getSellStartDatetime();
		product.sellEndDatetime = productUpdateDTO.getSellEndDatetime();
		product.displayName = productUpdateDTO.getDisplayName();
		product.brand = productUpdateDTO.getBrand();
		product.deliveryFeeType = productUpdateDTO.getDeliveryFeeType();
		product.deliveryMethod = productUpdateDTO.getDeliveryMethod();
		product.deliveryDefaultFee = productUpdateDTO.getDeliveryDefaultFee();
		product.freeShipOverAmount = productUpdateDTO.getFreeShipOverAmount();
		product.returnCenterCode = productUpdateDTO.getReturnCenterCode();
		
		return product;
	}
}