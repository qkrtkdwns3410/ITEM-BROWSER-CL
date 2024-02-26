package com.psj.itembrowser.product.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.psj.itembrowser.product.domain.dto.request.ProductQuantityUpdateRequestDTO;
import com.psj.itembrowser.product.domain.dto.request.ProductRequestDTO;
import com.psj.itembrowser.product.domain.dto.request.ProductUpdateDTO;
import com.psj.itembrowser.product.domain.dto.response.ProductResponseDTO;
import com.psj.itembrowser.product.domain.entity.ProductEntity;
import com.psj.itembrowser.product.domain.vo.Product;
import com.psj.itembrowser.product.persistence.ProductPersistence;

import lombok.RequiredArgsConstructor;

/**
 * packageName    : com.psj.itembrowser.product.service.impl fileName       : ProductServiceImpl
 * author         : ipeac date           : 2023-10-09 description    :
 * =========================================================== DATE              AUTHOR NOTE
 * ----------------------------------------------------------- 2023-10-09        ipeac       최초 생성
 */
@Service
@RequiredArgsConstructor
public class ProductService {
	
	private final ProductPersistence productPersistence;
	private final FileService fileService;
	
	@Transactional
	public boolean modifyProductQuantity(
		ProductQuantityUpdateRequestDTO productQuantityUpdateRequestDTO) {
		return productPersistence.updateProductQuantity(productQuantityUpdateRequestDTO);
	}
	
	@Transactional(readOnly = true)
	public ProductResponseDTO getProduct(Long productId) {
		return ProductResponseDTO.from(productPersistence.findProductById(productId));
	}
	
	/**
	 * @deprecated
	 */
	@Deprecated(since = "2024-02-16", forRemoval = true)
	@Transactional(readOnly = true)
	public List<Product> getProducts(Long orderId) {
		return productPersistence.findProductsByOrderId(orderId);
	}
	
	@Transactional
	public void createProduct(ProductRequestDTO productRequestDTO) {
		Product product = Product.from(productRequestDTO);
		List<MultipartFile> files = productRequestDTO.getMultipartFiles();
		
		product.validateSellDates();
		
		productPersistence.createProduct(product);
		
		Long productId = product.getId();
		
		fileService.createProductImages(files, productId);
	}
	
	@Transactional
	public void updateProduct(ProductUpdateDTO productUpdateDTO, Long productId) {
		// Ensure data consistency using pessimistic locking
		Product product = Product.from(productUpdateDTO);
		
		productPersistence.findProductStatusForUpdate(productId);
		
		product.validateSellDates();
		
		productPersistence.updateProduct(product);
		
		fileService.updateProductImages(productUpdateDTO, productId);
	}
	
	public void decreaseStock(ProductEntity entity) {
		//TODO 재고 관련 로직 추가
	}
	
	@Transactional
	public void deleteProduct(Long productId) {
		// Ensure data consistency using pessimistic locking
		productPersistence.findProductStatusForUpdate(productId);
		
		productPersistence.softDeleteProduct(productId);
		
		fileService.deleteProductImages(productId);
	}
}