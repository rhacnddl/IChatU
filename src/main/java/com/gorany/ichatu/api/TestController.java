package com.gorany.ichatu.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tests")
public class TestController {

    @GetMapping("/do")
    public String test1() throws Exception{

        Thread.sleep(5000);

        return "first";
    }
}
