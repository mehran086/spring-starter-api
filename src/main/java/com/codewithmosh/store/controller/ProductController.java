package com.codewithmosh.store.controller;

import com.codewithmosh.store.entities.Product;
import com.codewithmosh.store.dtos.ProductDto;
import com.codewithmosh.store.mapper.ProductMapper;
import com.codewithmosh.store.repositories.CategoryRepository;
import com.codewithmosh.store.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public List<ProductDto> getAllProducts(@RequestParam(required = false) Byte categoryId) {
        if (categoryId == null)
            return productRepository.findAllWithCategory().stream().map(productMapper::toDto).toList();
        else
            return productRepository.findByCategoryId(categoryId).stream().map(productMapper::toDto).toList();
    }

    @PostMapping("/addProduct")
    public ResponseEntity<ProductDto> addProduct(@RequestBody ProductDto request
    ,  UriComponentsBuilder uriBuilder){
        Product product = productMapper.toEntity(request);
        // check if the inserted category through request exists.
        if(!categoryRepository.existsById(request.getCategoryId())){
            return ResponseEntity.noContent().build();
        }
        // now find the category object using id and put it in product
        var category = categoryRepository.findById(request.getCategoryId()).get();
        product.setCategory(category);
        productRepository.save(product);
        var uri = uriBuilder.path("/products/{id}").buildAndExpand(product.getId()).toUri();
//        return ResponseEntity.ok(productMapper.toDto(product));
        return ResponseEntity.created(uri).body(request);
    }

    @PatchMapping("/updateProduct/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id,@RequestBody ProductDto request){
        Product product = productRepository.findById(id).orElse(null);
        if(product ==null){
            return ResponseEntity.notFound().build();
        }
        // now if the product exists change it
//        Product product = productMapper.toEntity(request);
        // fetch the category object using categoryid from request
        if(!categoryRepository.existsById(request.getCategoryId())){
            return ResponseEntity.noContent().build();
        }
        // now find the category object using id and put it in product
        var category = categoryRepository.findById(request.getCategoryId()).get();
        productMapper.update(request,product);
        product.setCategory(category);
        productRepository.save(product);
        return ResponseEntity.ok().body(request);
    }

    @DeleteMapping("/deleteProduct/{id}")
    public ResponseEntity<ProductDto> deleteproduct(@PathVariable Long id){
        // check if product exists.
        if(!productRepository.existsById(id))
        {
            return ResponseEntity.notFound().build();
        }
        productRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}