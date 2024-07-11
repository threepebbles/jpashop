package jpabook.jpashop.test;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController     // tymleaf 사용안함
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {
    // 연관관계의 주인 테스트
    @GetMapping("")
    @Transactional
    public String test() {
        // test code

        return "success";
    }
}
