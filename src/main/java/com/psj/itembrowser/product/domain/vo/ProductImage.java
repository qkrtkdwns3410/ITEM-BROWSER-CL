package com.psj.itembrowser.product.domain.vo;

import java.nio.file.Path;

import org.springframework.web.multipart.MultipartFile;

import com.psj.itembrowser.product.domain.dto.response.ProductImageResponseDTO;
import com.psj.itembrowser.security.common.BaseDateTimeEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * DTO for {@link com.psj.itembrowser.product.domain.vo.ProductImageEntity}
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ProductImage extends BaseDateTimeEntity {

	private Long id;

	private Long productId;

	private String fileName;

	private String filePath;

	private String type;

	private Long size;

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
			return null;
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