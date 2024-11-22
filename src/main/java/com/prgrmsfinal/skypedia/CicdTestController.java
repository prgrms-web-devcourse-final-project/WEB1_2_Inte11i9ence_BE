package com.prgrmsfinal.skypedia;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CicdTestController {

    @GetMapping("test")
    public String testnice() {
        return "It is working";
    }
}
