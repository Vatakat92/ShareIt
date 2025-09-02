package ru.practicum;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // @Controller + @ResponseBody
public class TestController {

    @GetMapping("/test") // обрабатывает GET-запросы к /test
    public String test() {
        return "Spring Boot работает!";
    }
}
