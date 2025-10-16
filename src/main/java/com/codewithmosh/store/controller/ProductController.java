package com.codewithmosh.store.controller;

import com.codewithmosh.store.entities.Product;
import com.codewithmosh.store.entities.ProductDto;
import com.codewithmosh.store.mapper.ProductMapper;
import com.codewithmosh.store.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductMapper productMapper;
    @GetMapping
public List<ProductDto> getAllProducts(@RequestParam(required = false) Byte categoryId){
   if(categoryId==null)
    return productRepository.findAllWithCategory().stream().map(productMapper::toDto).toList();
    else
        return productRepository.findByCategoryId(categoryId).stream().map(productMapper::toDto).toList();
}
}
