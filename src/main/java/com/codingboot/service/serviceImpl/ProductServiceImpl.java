package com.codingboot.service.serviceImpl;

import com.codingboot.entity.Product;
import com.codingboot.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService{
    private final ProductRepository
    productRepository;
    @Override
    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }
}
