package jpabook.jpashop.api;

import java.util.List;
import jpabook.jpashop.domain.order.Order;
import jpabook.jpashop.domain.order.OrderRepository;
import jpabook.jpashop.domain.order.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;

    // 이렇게만 하면 json 파싱 오류 발생. jackson-datatype-hibernate 모듈을 사용하면 임시 해결이 가능하긴 하지만 근본적인 해결책이 아니므로 사용X
    // 지연 로딩(LAZY)을 피하기 위해 즉시 로딩(EAGER)로 설정하면 안됨! 즉시 로딩으로 설정하면 성능 튜닝이 매우 어려워짐.
    // 항상 지연 로딩을 기본으로, 성능 최적화가 필요한 부분만 페치 조인으로 해결할 것
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            // Lazy 강제 초기화
            order.getMember().getName();
            order.getDelivery().getAddress();
        }
        return all;
    }

    
}
