package com.psj.itembrowser.product.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import com.psj.itembrowser.product.domain.entity.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
	@Lock(LockModeType.PESSIMISTIC_READ)
	Optional<ProductEntity> findWithPessimisticLockById(Long productId);
	
	@Lock(LockModeType.PESSIMISTIC_READ)
	List<ProductEntity> findWithPessimisticLockByIdIn(List<Long> orderProductsIds);
}