package com.codewithmosh.store.controller;


import com.codewithmosh.store.dtos.AddItemToCartRequest;
import com.codewithmosh.store.dtos.CartDto;
import com.codewithmosh.store.dtos.CartItemDto;
import com.codewithmosh.store.dtos.UpdateCartItemRequest;
import com.codewithmosh.store.entities.Cart;
import com.codewithmosh.store.entities.CartItem;
import com.codewithmosh.store.mapper.CartMapper;
import com.codewithmosh.store.repositories.CartRepository;
import com.codewithmosh.store.repositories.ProductRepository;
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
public class CartController {

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final ProductRepository productRepository;

    @Autowired
    public CartController(CartRepository cartRepository, CartMapper cartMapper,
                          ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartMapper = cartMapper;
        this.productRepository = productRepository;
    }

    @PostMapping
    public ResponseEntity<CartDto> createCart(
            UriComponentsBuilder uriComponentsBuilder
    ){
        var cart= new Cart();
        cartRepository.save(cart);
        var cartDto = cartMapper.toDto(cart);
        var uri = uriComponentsBuilder.path("/carts/{id}").buildAndExpand(cartDto.getId());

        return ResponseEntity.ok(cartDto);

    }

    @PostMapping("/{cartId}/items")
    public ResponseEntity<CartItemDto> addToCart
            (@PathVariable UUID cartId,
             @RequestBody AddItemToCartRequest request
            ){
        var cart = cartRepository.findById(cartId).orElse(null);

        if(cart==null){
            return ResponseEntity.notFound().build();
        }

        var product =productRepository.findById(request.getProductId()).orElse(null);
        if(product==null){
//            return ResponseEntity.notFound().build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // check if product exists ,
        // if the product exist increment its count or else insert it.

//       var cartItem= cart.getItem(product.getId());
        var cartItem = cart.addItem(product);

        cartRepository.save(cart);

        var cartItemDto = cartMapper.toDto1(cartItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemDto);

    }

    @GetMapping("/{cartId}")
    public ResponseEntity<?> getCart(
            @PathVariable UUID cartId
    ){
        var cart = cartRepository.findById(cartId).orElse(null);
        if(cart==null){
            return ResponseEntity.notFound().build();
        }
        else {
            return ResponseEntity.ok(cartMapper.toDto(cart));
        }
    }

    @PutMapping("/{cartId}/items/{productId}")
    public ResponseEntity<?> updateItem(
            @PathVariable("cartId") UUID cartId,
            @PathVariable("productId") Long productId,
            @Valid @RequestBody UpdateCartItemRequest request
    ){
        var cart = cartRepository.findById(cartId).orElse(null);
        if(cart==null){
           return ResponseEntity
                   .status(HttpStatus.NOT_FOUND)
                   .body(Map.of("error","Cart not found"));

        }

        var cartItem= cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst().orElse(null);
        if(cartItem==null){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error","product was not found in the cart"));
        }
        if(cartItem!=null){
//            cartItem.setQuantity(cartItem.getQuantity()+1);
            cartItem.setQuantity(request.getQuantity());
        }
//        else{
//            cartItem = new CartItem();
//            cartItem.setProduct(product);
//            cartItem.setCart(cart);
//            cartItem.setQuantity(1);
//            cart.getItems().add(cartItem);
//        }
        cartRepository.save(cart);

        var cartItemDto = cartMapper.toDto1(cartItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemDto);


    }

    @DeleteMapping("{cartId}/items/{productId}")
    public ResponseEntity<?> deleteCart(
            @PathVariable("cartId") UUID cardId,
            @PathVariable("productId") Long productId  ){
        var cart = cartRepository.findById(cardId).orElse(null);
        if(cart==null){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error","Cart not found"));
        }
        var product = productRepository.findById(productId).orElse(null);
        if(product==null){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error","Product not found"));
        }
//        cartRepository.delete(cart);
        cart.removeItem(productId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/carts/{cartId}/items")
    public ResponseEntity<?> deletecart(
            @PathVariable("cartId") UUID cartId
    ){
        var cart = cartRepository.findById(cartId).orElse(null);
        if(cart==null){
            return ResponseEntity.status(404).build();
        }
        cartRepository.delete(cart);
        return ResponseEntity.status(204).build();
    }
}
