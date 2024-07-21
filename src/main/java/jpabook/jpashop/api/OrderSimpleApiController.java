package jpabook.jpashop.api;

import java.time.LocalDateTime;
import java.util.List;
import jpabook.jpashop.domain.common.Address;
import jpabook.jpashop.domain.order.Order;
import jpabook.jpashop.domain.order.OrderRepository;
import jpabook.jpashop.domain.order.OrderSearch;
import jpabook.jpashop.domain.order.OrderStatus;
import jpabook.jpashop.domain.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.domain.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

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

    // select 절에서 원하는 데이터만 조회. select 이후 쿼리문은 v3와 동일.
    // 딱 이 API 스펙에 맞는 DTO를 만들어서 사용했기 때문에 리포지토리의 재사용성은 떨어지지만 성능면에서 v3보다 약간 나음(거의 성능 차이 안남. 대부분의 성능 차이는 select 필드 개수가 아닌 join에서 발생)
    // 컨트롤러 API 스펙이 리포지토리에 반영되는 결과가 나왔으므로 별로 좋은 설계는 아님. 컨트롤러 API 스펙의 변화가 리포지토리 코드에 변화를 요구하게 됨. 되도록 리포지토리는 가급적이면 순수하게 엔티티를 조회하는 용도로만 사용되는게 좋음.
    // 이런 특수한 사용 용도의 복잡한 쿼리를 실행하고 싶은 경우는 복잡한 쿼리용 레포지토리(OrderSimpeQueryRepository)를 따로 생성해서 사용하면 유지보수성이 높아짐.

    // 실시간 응답이 중요하고, 집요하게 많이 호출되는 쿼리인 경우,
    // 통계용으로 이런 특정 쿼리를 호출해야 할 때 사용하면 좋은 방법임
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() {
        return orderSimpleQueryRepository.findOrderDtos();
    }

    /**
     * 정리
     * <p>
     * 1. 우선 엔티티를 DTO로 변환 (v2)
     * <p>
     * 2. 필요하면 페치 조인으로 성능 최적화 -> 대부분 이슈가 해결됨 (v3)
     * <p>
     * 3. 그래도 안되면 DTO로 직접 조회하는 방법 사용 (v4)
     * <p>
     * 4. 최후의 방법은 JPA가 제공하는 네이티브 SQL이나 Spring JDBC Template을 사용해서 SQL을 직접 작성
     */

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
