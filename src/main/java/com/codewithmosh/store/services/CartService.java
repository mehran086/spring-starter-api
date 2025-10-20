package com.codewithmosh.store.services;

import com.codewithmosh.store.dtos.CartDto;
import com.codewithmosh.store.dtos.CartItemDto;
import com.codewithmosh.store.dtos.UpdateCartItemRequest;
import com.codewithmosh.store.entities.Cart;
import com.codewithmosh.store.exceptions.CartNotFoundException;
import com.codewithmosh.store.exceptions.ProductNotFoundException;
import com.codewithmosh.store.mapper.CartMapper;
import com.codewithmosh.store.repositories.CartRepository;
import com.codewithmosh.store.repositories.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
import java.util.UUID;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final ProductRepository productRepository;

    @Autowired
    public CartService(CartRepository cartRepository, CartMapper cartMapper,
                          ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartMapper = cartMapper;
        this.productRepository = productRepository;
    }

    public CartDto createCart(){
        var cart = new Cart();
        cartRepository.save(cart);
        return cartMapper.toDto(cart);
    }

    public CartItemDto addToCart(UUID cartId, Long productId){
        var cart = cartRepository.findById(cartId).orElse(null);

        if(cart==null){
//            return ResponseEntity.notFound().build();
            throw new CartNotFoundException();
        }

        var product =productRepository.findById(productId).orElse(null);
        if(product==null){
//            return ResponseEntity.notFound().build();
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            throw  new ProductNotFoundException();
        }

        // check if product exists ,
        // if the product exist increment its count or else insert it.

//       var cartItem= cart.getItem(product.getId());
        var cartItem = cart.addItem(product);

        cartRepository.save(cart);

        var cartItemDto = cartMapper.toDto1(cartItem);
        return cartItemDto;
    }
    public CartDto getCart(UUID cartId){
        var cart = cartRepository.findById(cartId).orElse(null);
        if(cart==null){
//            return ResponseEntity.notFound().build();
            throw new CartNotFoundException();           
        }
        else {
//            return ResponseEntity.ok(cartMapper.toDto(cart));
            return cartMapper.toDto(cart);
//            throw new ProductNotFoundException();
        }
        
    }

    public CartItemDto updateCart(  UUID cartId,
                           Long productId,
                           Integer quantity){
        var cart = cartRepository.findById(cartId).orElse(null);
        if(cart==null){
//            return ResponseEntity
//                    .status(HttpStatus.NOT_FOUND)
//                    .body(Map.of("error","Cart not found"));
            throw new CartNotFoundException();
        }

//        var cartItem= cart.getItems().stream()
//                .filter(item -> item.getProduct().getId().equals(productId))
//                .findFirst().orElse(null);
        var cartItem= cart.getItem(productId);
        if(cartItem==null){
//            return ResponseEntity
//                    .status(HttpStatus.NOT_FOUND)
//                    .body(Map.of("error","product was not found in the cart"));
        throw new ProductNotFoundException();
        }

//            cartItem.setQuantity(cartItem.getQuantity()+1);
        cartItem.setQuantity(quantity);


        cartRepository.save(cart);

        var cartItemDto = cartMapper.toDto1(cartItem);
//        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemDto);
        return cartItemDto;

    }
    public void removeItem(   UUID cardId,
                              Long productId){
        var cart = cartRepository.findById(cardId).orElse(null);
        if(cart==null){
//            return ResponseEntity
//                    .status(HttpStatus.NOT_FOUND)
//                    .body(Map.of("error","Cart not found"));
            throw new CartNotFoundException();
        }

        cart.removeItem(productId);
        cartRepository.save(cart);
//        return ResponseEntity.noContent().build();
//        return cart;
    }

    public void clearCart(UUID cartId){
        var cart = cartRepository.findById(cartId).orElse(null);
        if(cart==null){
//            return ResponseEntity.status(404).build();
            throw new CartNotFoundException();
        }
//        cartRepository.delete(cart);
        cart.clear();
        cartRepository.save(cart);
//        return ResponseEntity.status(204).build();
    }



}
