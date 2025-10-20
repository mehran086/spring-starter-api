package com.codewithmosh.store.dtos;

import jakarta.validation.constraints.NotNull;

public class AddItemToCartRequest {
    @NotNull
    private Long productId;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
