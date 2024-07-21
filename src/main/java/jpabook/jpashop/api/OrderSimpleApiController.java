package jpabook.jpashop.api;

import java.time.LocalDateTime;
import java.util.List;
import jpabook.jpashop.domain.common.Address;
import jpabook.jpashop.domain.order.Order;
import jpabook.jpashop.domain.order.OrderRepository;
import jpabook.jpashop.domain.order.OrderSearch;
import jpabook.jpashop.domain.order.OrderStatus;
import lombok.Data;
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

    // v2에서도 여전히 Lazy 로딩으로 인한 쿼리가 너무 많이 발생함
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        // N + 1 문제 발생
        // Order 1개 당 Member N개, Delivery N개, ... 연관 관계 설정된 엔티티들의 쿼리 발생
        // (영속성 컨텍스트에서 조회하는 경우는 쿼리를 생략하긴 하겠지만) 최악에 너무 많은 쿼리 발생
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());

        List<SimpleOrderDto> result = orders.stream()
                .map(SimpleOrderDto::new)   // 람다 레퍼런스
                .toList();
        return result;
    }

    // v2와 결과는 똑같지만, 페치 조인을 사용했기 때문에 호출되는 쿼리 수가 다름.
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> orderV3() {
        // 페치 조인 사용
        List<Order> orders = orderRepository.findAllWithMemberDelivery();

        List<SimpleOrderDto> result = orders.stream()
                .map(SimpleOrderDto::new)   // 람다 레퍼런스
                .toList();
        return result;
    }

    //==DTO==//
    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        // value object
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
        }
    }
}
