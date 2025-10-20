package com.codewithmosh.store.mapper;

import com.codewithmosh.store.entities.Product;
import com.codewithmosh.store.dtos.ProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target="categoryId", source="category.id")
    ProductDto toDto(Product product);

    @Mapping(target="category.id", source="categoryId")
    Product toEntity(ProductDto request);

    @Mapping(target ="id" ,ignore =true)
    void update(ProductDto request, @MappingTarget Product product);

}
