package com.psj.itembrowser.product.service;

import com.psj.itembrowser.product.domain.dto.request.ProductQuantityUpdateRequestDTO;
import com.psj.itembrowser.product.domain.dto.request.ProductRequestDTO;
import com.psj.itembrowser.product.domain.dto.response.ProductResponseDTO;
import com.psj.itembrowser.product.domain.vo.Product;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * packageName    : com.psj.itembrowser.product.service
 * fileName       : ProductService
 * author         : ipeac
 * date           : 2023-10-09
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-10-09        ipeac       최초 생성
 */
@Service
public interface ProductService {

    boolean modifyProductQuantity(ProductQuantityUpdateRequestDTO productQuantityUpdateRequestDTO);

    ProductResponseDTO getProduct(Long productId);

    List<Product> getProducts(Long orderId);

    void createProduct(ProductRequestDTO productRequestDTO);

    void updateProduct(ProductRequestDTO productRequestDTO);
}