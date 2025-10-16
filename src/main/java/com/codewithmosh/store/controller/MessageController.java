package com.codewithmosh.store.controller;

import com.codewithmosh.store.entities.Message;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {
        @RequestMapping("/message")
    public Message firsMethod(){
            return new Message("java obj is automatically converted" +
                    "to java object");
        }
}
