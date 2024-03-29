package com.psj.itembrowser.product.domain.entity;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.psj.itembrowser.product.domain.dto.request.ProductRequestDTO;
import com.psj.itembrowser.product.domain.dto.request.ProductUpdateDTO;
import com.psj.itembrowser.product.domain.dto.response.ProductResponseDTO;
import com.psj.itembrowser.product.domain.vo.DeliveryFeeType;
import com.psj.itembrowser.product.domain.vo.Product;
import com.psj.itembrowser.product.domain.vo.ProductImageEntity;
import com.psj.itembrowser.product.domain.vo.ProductStatus;
import com.psj.itembrowser.security.common.BaseDateTimeEntity;
import com.psj.itembrowser.security.common.exception.BadRequestException;
import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.security.common.exception.NotFoundException;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductEntity extends BaseDateTimeEntity {
	
	/**
	 * pk값
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 상품명
	 */
	@Column(name = "name", length = 500)
	private String name;
	
	/**
	 * 상품카테고리. CATEGORY 테이블 참조
	 */
	@Column(name = "category")
	private Integer category;
	
	/**
	 * 상품설명
	 */
	@Column(name = "DETAIL", length = 45)
	private String detail;
	
	/**
	 * 상품상태. 심사중/임시저장/승인대기/승인완료/부분승인/완료/승인반려/상품삭제
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = 200)
	private ProductStatus status;
	
	/**
	 * 재고
	 */
	@Column(name = "quantity")
	private Integer quantity;
	
	/**
	 * 가격
	 */
	@Column(name = "unit_price")
	private Integer unitPrice;
	
	/**
	 * 판매자ID
	 */
	@Column(name = "seller_id", length = 45)
	private String sellerId;
	
	/**
	 * 판매시작일시
	 */
	@Column(name = "sell_start_datetime")
	private LocalDateTime sellStartDatetime;
	
	/**
	 * 판매종료일시
	 */
	@Column(name = "sell_end_datetime")
	private LocalDateTime sellEndDatetime;
	
	/**
	 * 노출상품명. 실제노출되는 상품명
	 */
	@Column(name = "display_name", length = 500)
	private String displayName;
	
	/**
	 * 브랜드
	 */
	@Column(name = "brand", length = 300)
	private String brand;
	
	/**
	 * 배송비종류. DELIVERY_FEE_TYPE 테이블 참조
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "delivery_fee_type")
	private DeliveryFeeType deliveryFeeType;
	
	/**
	 * 배송방법. DELIVERY_METHOD 테이블 참조
	 */
	@Column(name = "delivery_method", length = 45)
	private String deliveryMethod;
	
	/**
	 * 기본배송비. 기본 배송
	 */
	@Column(name = "delivery_default_fee")
	private Integer deliveryDefaultFee;
	
	/**
	 * 무료배송금액. 무료 배송 기준 금액
	 */
	@Column(name = "free_ship_over_amount")
	private Integer freeShipOverAmount;
	
	/**
	 * 반품지 센터 코드. CENTER 테이블 참조
	 */
	@Column(name = "return_center_code", length = 255)
	private String returnCenterCode;
	
	@OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
	private List<ProductImageEntity> productImages;
	
	@Builder
	private ProductEntity(LocalDateTime deletedDate, Long id, String name, Integer category,
		String detail, ProductStatus status, Integer quantity, Integer unitPrice, String sellerId, LocalDateTime sellStartDatetime,
		LocalDateTime sellEndDatetime, String displayName, String brand, DeliveryFeeType deliveryFeeType, String deliveryMethod,
		Integer deliveryDefaultFee, Integer freeShipOverAmount, String returnCenterCode,
		List<ProductImageEntity> productImages) {
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
		this.productImages = productImages == null ? new ArrayList<>() : productImages;
		super.deletedDate = deletedDate;
	}
	
	public void validateSellDates() {
		if (this.sellStartDatetime != null && this.sellEndDatetime != null && this.sellEndDatetime.isBefore(this.sellStartDatetime)) {
			throw new IllegalArgumentException("The sell start datetime must not be before the sell end datetime.");
		}
	}
	
	// 상품 재고를 줄이는 메서드
	public void decreaseStock(int quantity) {
		if (quantity < 0) {
			throw new BadRequestException(ErrorCode.PRODUCT_QUANTITY_LESS_THAN_ZERO);
		}
		
		int restStock = this.quantity - quantity;
		
		if (restStock < 0) {
			throw new BadRequestException(ErrorCode.PRODUCT_QUANTITY_LESS_THAN_ZERO);
		}
		
		this.quantity = restStock;
	}
	
	// 상품 재고를 늘리는 메서드
	public void increaseStock(int quantity) {
		if (quantity < 0) {
			throw new BadRequestException(ErrorCode.PRODUCT_QUANTITY_LESS_THAN_ZERO);
		}
		
		this.quantity += quantity;
	}
	
	// 상품 재고가 충분한지 확인하는 메서드
	public boolean checkStockAvailability(int requestedQuantity) {
		if (requestedQuantity < 0) {
			throw new BadRequestException(ErrorCode.PRODUCT_QUANTITY_LESS_THAN_ZERO);
		}
		
		return requestedQuantity <= this.quantity;
	}
	
	public long calculateTotalPrice() {
		Objects.requireNonNull(this.unitPrice, "unitPrice must not be null");
		Objects.requireNonNull(this.quantity, "quantity must not be null");
		
		if (this.unitPrice < 0 || this.quantity < 0) {
			throw new BadRequestException(ErrorCode.PRODUCT_VALIDATION_FAIL);
		}
		
		return (long)this.unitPrice * this.quantity;
	}
	
	public BigDecimal calculateDiscount(int quantity, int discountRate) {
		Objects.requireNonNull(this.unitPrice, "unitPrice must not be null");
		
		if (this.unitPrice < 0 || quantity < 0 || !(0 <= discountRate && discountRate <= 100)) {
			throw new BadRequestException(ErrorCode.PRODUCT_VALIDATION_FAIL);
		}
		
		long totalAmount = (long)this.unitPrice * quantity;
		
		// 나눗셈을 위해 BigDecimal 사용
		return BigDecimal.valueOf(totalAmount)
			.multiply(BigDecimal.valueOf(discountRate))
			.divide(BigDecimal.valueOf(100), MathContext.DECIMAL128);
	}
	
	public static ProductEntity from(ProductRequestDTO productRequestDTO) {
		if (productRequestDTO == null) {
			throw new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND);
		}
		
		ProductEntity product = new ProductEntity();
		
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
	
	public static ProductEntity from(ProductUpdateDTO productUpdateDTO) {
		if (productUpdateDTO == null) {
			throw new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND);
		}
		
		ProductEntity product = new ProductEntity();
		
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
	
	public static ProductEntity from(ProductResponseDTO dto) {
		if (dto == null) {
			throw new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND);
		}
		
		return ProductEntity.builder()
			.id(dto.getId())
			.name(dto.getName())
			.category(dto.getCategory())
			.detail(dto.getDetail())
			.status(dto.getStatus())
			.quantity(dto.getQuantity())
			.unitPrice(dto.getUnitPrice())
			.sellerId(dto.getSellerId())
			.sellStartDatetime(dto.getSellStartDatetime())
			.sellEndDatetime(dto.getSellEndDatetime())
			.displayName(dto.getDisplayName())
			.brand(dto.getBrand())
			.deliveryFeeType(dto.getDeliveryFeeType())
			.deliveryMethod(dto.getDeliveryMethod())
			.deliveryDefaultFee(dto.getDeliveryDefaultFee())
			.freeShipOverAmount(dto.getFreeShipOverAmount())
			.returnCenterCode(dto.getReturnCenterCode())
			.build();
	}
	
	public static ProductEntity from(Product product) {
		if (product == null) {
			throw new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND);
		}
		
		return ProductEntity.builder()
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
			.deliveryFeeType(product.getDeliveryFeeType())
			.deliveryMethod(product.getDeliveryMethod())
			.deliveryDefaultFee(product.getDeliveryDefaultFee())
			.freeShipOverAmount(product.getFreeShipOverAmount())
			.returnCenterCode(product.getReturnCenterCode())
			.build();
	}
}