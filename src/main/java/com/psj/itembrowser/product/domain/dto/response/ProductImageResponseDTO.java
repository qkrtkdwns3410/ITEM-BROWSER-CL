package com.psj.itembrowser.product.domain.dto.response;

import java.time.LocalDateTime;

import com.psj.itembrowser.product.domain.vo.ProductImage;
import com.psj.itembrowser.product.domain.vo.ProductImageEntity;
import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.security.common.exception.NotFoundException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for {@link ProductImageEntity}
 */
@Data
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
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

	public static ProductImageResponseDTO from(ProductImage vo) {
		if (vo == null) {
			throw new NotFoundException(ErrorCode.PRODUCT_IMAGE_NOT_FOUND);
		}

		ProductImageResponseDTO dto = new ProductImageResponseDTO();

		dto.setId(vo.getId());
		dto.setProductId(vo.getProductId());
		dto.setFileName(vo.getFileName());
		dto.setFilePath(vo.getFilePath());
		dto.setType(vo.getType());
		dto.setSize(vo.getSize());
		dto.setCreatedDate(vo.getCreatedDate());
		dto.setUpdatedDate(vo.getUpdatedDate());
		dto.setDeletedDate(vo.getDeletedDate());

		return dto;
	}

	public static ProductImageResponseDTO from(ProductImageEntity entity) {
		if (entity == null) {
			throw new NotFoundException(ErrorCode.PRODUCT_IMAGE_NOT_FOUND);
		}

		ProductImageResponseDTO dto = new ProductImageResponseDTO();

		dto.setId(entity.getId());
		dto.setProductId(entity.getProductId());
		dto.setFileName(entity.getFileName());
		dto.setFilePath(entity.getFilePath());
		dto.setType(entity.getType());
		dto.setSize(entity.getSize());
		dto.setCreatedDate(entity.getCreatedDate());
		dto.setUpdatedDate(entity.getUpdatedDate());
		dto.setDeletedDate(entity.getDeletedDate());

		return dto;
	}
}