package com.psj.itembrowser.product.domain.dto.response;

import java.time.LocalDateTime;

import com.psj.itembrowser.product.domain.vo.ProductImage;
import com.psj.itembrowser.product.domain.vo.ProductImageEntity;
import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.security.common.exception.NotFoundException;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for {@link ProductImageEntity}
 */
@Setter
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ProductImageResponseDTO {
	Long id;
	Long productId;
	String fileName;
	String filePath;
	String type;
	Long size;
	LocalDateTime createdDate;
	LocalDateTime updatedDate;
	LocalDateTime deletedDate;
	ProductResponseDTO productResponseDTO;
	
	@Builder
	private ProductImageResponseDTO(Long id, Long productId, String fileName, String filePath, String type, Long size, LocalDateTime createdDate,
		LocalDateTime updatedDate, LocalDateTime deletedDate, ProductResponseDTO productResponseDTO) {
		this.id = id;
		this.productId = productId;
		this.fileName = fileName;
		this.filePath = filePath;
		this.type = type;
		this.size = size;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
		this.deletedDate = deletedDate;
		this.productResponseDTO = productResponseDTO;
	}
	
	public static ProductImageResponseDTO from(ProductImage vo) {
		if (vo == null) {
			throw new NotFoundException(ErrorCode.PRODUCT_IMAGE_NOT_FOUND);
		}
		
		return ProductImageResponseDTO.builder()
			.id(vo.getId())
			.productId(vo.getProductId())
			.fileName(vo.getFileName())
			.filePath(vo.getFilePath())
			.type(vo.getType())
			.size(vo.getSize())
			.createdDate(vo.getCreatedDate())
			.updatedDate(vo.getUpdatedDate())
			.deletedDate(vo.getDeletedDate())
			.build();
	}
	
	public static ProductImageResponseDTO from(ProductImageEntity entity) {
		if (entity == null) {
			throw new NotFoundException(ErrorCode.PRODUCT_IMAGE_NOT_FOUND);
		}
		
		return ProductImageResponseDTO.builder()
			.id(entity.getId())
			.productId(entity.getProductId())
			.fileName(entity.getFileName())
			.filePath(entity.getFilePath())
			.type(entity.getType())
			.size(entity.getSize())
			.createdDate(entity.getCreatedDate())
			.updatedDate(entity.getUpdatedDate())
			.deletedDate(entity.getDeletedDate())
			.build();
	}
}