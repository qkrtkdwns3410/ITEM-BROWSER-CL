package com.psj.itembrowser.product.domain.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import com.psj.itembrowser.product.domain.vo.DeliveryFeeType;
import com.psj.itembrowser.product.domain.vo.ProductStatus;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductRequestDTO {
	
	@NotBlank
	@Length(min = 1, max = 100, message = "The product name must be at least 1 character and less than 100 characters.")
	private String name;
	
	private Integer category;
	
	@NotBlank
	@Length(min = 10, max = 1000, message = "The product detail must be at least 10 character and less than 1000 characters.")
	private String detail;
	
	private ProductStatus status;
	
	@Min(value = 0, message = "Quantity must be greater than 0.")
	private Integer quantity;
	
	@Min(value = 0, message = "Price must be greater than 0.")
	private Integer unitPrice;
	
	private String sellerId;
	
	@NotNull(message = "Sell start date must not be null.")
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@FutureOrPresent(message = "The sales start date must be present or in the future")
	private LocalDateTime sellStartDatetime;
	
	@NotNull(message = "Sell sell end date must not be null.")
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@FutureOrPresent(message = "The sales end date must be present or in the future")
	private LocalDateTime sellEndDatetime;
	
	private String displayName;
	
	private String brand;
	
	private DeliveryFeeType deliveryFeeType;
	
	private String deliveryMethod;
	
	private Integer deliveryDefaultFee;
	
	private Integer freeShipOverAmount;
	
	private String returnCenterCode;
	
	private List<MultipartFile> multipartFiles;
	
	@Builder
	private ProductRequestDTO(String name, Integer category, String detail, ProductStatus status, Integer quantity, Integer unitPrice,
		String sellerId, LocalDateTime sellStartDatetime, LocalDateTime sellEndDatetime, String displayName, String brand,
		DeliveryFeeType deliveryFeeType, String deliveryMethod, Integer deliveryDefaultFee, Integer freeShipOverAmount, String returnCenterCode,
		List<MultipartFile> multipartFiles) {
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
		this.multipartFiles = multipartFiles == null ? List.of() : multipartFiles;
	}
}