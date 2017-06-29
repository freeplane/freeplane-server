package org.freeplane.server.rest;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class FreeplaneController {

    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

}
