package com.psj.itembrowser.product.domain.vo;

import java.nio.file.Path;
import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import com.psj.itembrowser.product.domain.dto.response.ProductImageResponseDTO;
import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.security.common.exception.NotFoundException;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO for {@link com.psj.itembrowser.product.domain.vo.ProductImageEntity}
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductImage {
	
	private Long id;
	
	private Long productId;
	
	private String fileName;
	
	private String filePath;
	
	private String type;
	
	private Long size;
	
	private LocalDateTime createdDate;
	
	private LocalDateTime updatedDate;
	
	private LocalDateTime deletedDate;
	
	@Builder
	private ProductImage(Long id, Long productId, String fileName, String filePath, String type, Long size) {
		this.id = id;
		this.productId = productId;
		this.fileName = fileName;
		this.filePath = filePath;
		this.type = type;
		this.size = size;
	}
	
	public static ProductImage from(MultipartFile file, Long productId, Path savePath) {
		return ProductImage.builder()
			.productId(productId)
			.fileName(savePath.getFileName().toString())
			.filePath(savePath.toString())
			.type(file.getContentType())
			.size(file.getSize())
			.build();
	}
	
	public static ProductImage from(ProductImageResponseDTO dto) {
		if (dto == null) {
			throw new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND);
		}
		
		return ProductImage.builder()
			.id(dto.getId())
			.productId(dto.getProductId())
			.fileName(dto.getFileName())
			.filePath(dto.getFilePath())
			.type(dto.getType())
			.size(dto.getSize())
			.build();
	}
}