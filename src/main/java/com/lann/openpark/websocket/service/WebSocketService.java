package com.lann.openpark.websocket.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class WebSocketService {

    private static final Logger log = LoggerFactory.getLogger(WebSocketService.class);


    public static void main(String[] args) {


    }

    public void testWs() {
        log.info("~~~~~~OK~~~~~~");
    }

}
