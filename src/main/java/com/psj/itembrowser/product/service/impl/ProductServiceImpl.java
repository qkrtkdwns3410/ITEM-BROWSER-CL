package com.psj.itembrowser.product.service.impl;

import static com.psj.itembrowser.common.exception.ErrorCode.PRODUCT_VALIDATION_FAIL;

import com.psj.itembrowser.common.exception.BadRequestException;
import com.psj.itembrowser.product.domain.dto.request.ProductQuantityUpdateRequestDTO;
import com.psj.itembrowser.product.domain.dto.request.ProductRequestDTO;
import com.psj.itembrowser.product.domain.dto.response.ProductResponseDTO;
import com.psj.itembrowser.product.domain.vo.Product;
import com.psj.itembrowser.product.domain.vo.ProductImage;
import com.psj.itembrowser.product.mapper.ProductMapper;
import com.psj.itembrowser.product.persistence.ProductPersistence;
import com.psj.itembrowser.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * packageName    : com.psj.itembrowser.product.service.impl fileName       : ProductServiceImpl
 * author         : ipeac date           : 2023-10-09 description    :
 * =========================================================== DATE              AUTHOR NOTE
 * ----------------------------------------------------------- 2023-10-09        ipeac       최초 생성
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final ProductPersistence productPersistence;

    @Override
    @Transactional(readOnly = false)
    public boolean modifyProductQuantity(
        ProductQuantityUpdateRequestDTO productQuantityUpdateRequestDTO) {
        return productMapper.updateProduct(productQuantityUpdateRequestDTO);
    }

    @Override
    public ProductResponseDTO getProduct(Long productId) {
        return productPersistence.findProductById(productId);
    }

    @Override
    public List<Product> getProducts(Long orderId) {
        return productPersistence.findProductsByOrderId(orderId);
    }

    @Override
    @Transactional(readOnly = false)
    public void createProduct(ProductRequestDTO productRequestDTO) {
        Product product = productRequestDTO.toProduct();
        product.validateSellDates();
        List<ProductImage> productImages = productRequestDTO.getFiles();

        if (productRequestDTO.getSellStartDatetime()
            .isBefore(productRequestDTO.getSellEndDatetime())) {
            throw new BadRequestException(PRODUCT_VALIDATION_FAIL);
        }

        productPersistence.createProduct(product);
        productPersistence.createProductImages(productImages);
    }
}