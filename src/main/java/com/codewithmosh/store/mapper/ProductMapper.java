package com.codewithmosh.store.mapper;

import com.codewithmosh.store.entities.Product;
import com.codewithmosh.store.entities.ProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target="categoryId", source="category.id")
    ProductDto toDto(Product product);
}
