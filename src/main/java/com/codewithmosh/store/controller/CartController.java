package com.codewithmosh.store.controller;


import com.codewithmosh.store.dtos.AddItemToCartRequest;
import com.codewithmosh.store.dtos.CartDto;
import com.codewithmosh.store.dtos.CartItemDto;
import com.codewithmosh.store.dtos.UpdateCartItemRequest;
import com.codewithmosh.store.entities.Cart;
import com.codewithmosh.store.entities.CartItem;
import com.codewithmosh.store.exceptions.CartNotFoundException;
import com.codewithmosh.store.exceptions.ProductNotFoundException;
import com.codewithmosh.store.mapper.CartMapper;
import com.codewithmosh.store.repositories.CartRepository;
import com.codewithmosh.store.repositories.ProductRepository;
import com.codewithmosh.store.services.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/carts")
@Tag(name="Carts")   // it gives name for this CartController as Carts in the swagger api
public class CartController {

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final ProductRepository productRepository;
    private CartService cartService;

    @Autowired
    public CartController(CartService cartService, ProductRepository productRepository, CartMapper cartMapper, CartRepository cartRepository) {
        this.cartService = cartService;
        this.productRepository = productRepository;
        this.cartMapper = cartMapper;
        this.cartRepository = cartRepository;
    }

    @Autowired


    @PostMapping
    public ResponseEntity<CartDto> createCart(
//            UriComponentsBuilder uriComponentsBuilder
    ){
//        var cart= new Cart();
//        cartRepository.save(cart);
//        var cartDto = cartMapper.toDto(cart);
        var cartDto = cartService.createCart();
//        var uri = uriComponentsBuilder.path("/carts/{id}").buildAndExpand(cartDto.getId());

        return ResponseEntity.ok(cartDto);

    }

    @PostMapping("/{cartId}/items")
    @Operation(summary = "Adds a product to the cart ")
    public ResponseEntity<CartItemDto> addToCart
            (@PathVariable UUID cartId,
             @RequestBody AddItemToCartRequest request
            ){
        var cartItemDto = cartService.addToCart(cartId, request.getProductId());
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemDto);

    }

    @GetMapping("/{cartId}")
    public ResponseEntity<?> getCart(
            @PathVariable UUID cartId
    ){
//        var cart = cartRepository.findById(cartId).orElse(null);
//        if(cart==null){
//            return ResponseEntity.notFound().build();
//        }
//        else {
//            return ResponseEntity.ok(cartMapper.toDto(cart));
//        }
        var cartDto = cartService.getCart(cartId);
        return ResponseEntity.ok(cartDto);
    }

    @PutMapping("/{cartId}/items/{productId}")
    public ResponseEntity<?> updateItem(
            @PathVariable("cartId") UUID cartId,
            @PathVariable("productId") Long productId,
            @Valid @RequestBody UpdateCartItemRequest request
    ){

        var cartItem = cartService.updateCart(cartId,productId, request.getQuantity());
        return ResponseEntity.ok(cartItem);
    }

    @DeleteMapping("{cartId}/items/{productId}")
    public ResponseEntity<?> deleteCart(
            @PathVariable("cartId") UUID cardId,
            @PathVariable("productId") Long productId  ){
//        var cart = cartRepository.findById(cardId).orElse(null);
//        if(cart==null){
//            return ResponseEntity
//                    .status(HttpStatus.NOT_FOUND)
//                    .body(Map.of("error","Cart not found"));
//        }
//
//        cart.removeItem(productId);
//        cartRepository.save(cart);
        cartService.removeItem(cardId,productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/carts/{cartId}/items")
    public ResponseEntity<?> clearCart(
            @PathVariable("cartId") UUID cartId
    ){
//        var cart = cartRepository.findById(cartId).orElse(null);
//        if(cart==null){
//            return ResponseEntity.status(404).build();
//        }
////        cartRepository.delete(cart);
//        cart.clear();
//        cartRepository.save(cart);
        cartService.clearCart(cartId);
        return ResponseEntity.status(204).build();
    }


    // to handle exception
    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<Map<String,String>> handleCartNotFound(){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error","Cart Not found."));

    }
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String,String>> handleProductNotFound(){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error","Product Not found in the cart ."));

    }
}
