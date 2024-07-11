package jpabook.jpashop.test;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hello")
public class HelloController {
    @GetMapping("/say-hello")
    @Transactional
    public String hello(Model model) {
        model.addAttribute("data", "hello!!");
        return "hello";
    }
}
