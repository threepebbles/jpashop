package jpabook.jpashop.api;

import java.time.LocalDateTime;
import java.util.List;
import jpabook.jpashop.domain.common.Address;
import jpabook.jpashop.domain.order.Order;
import jpabook.jpashop.domain.order.OrderItem;
import jpabook.jpashop.domain.order.OrderRepository;
import jpabook.jpashop.domain.order.OrderSearch;
import jpabook.jpashop.domain.order.OrderStatus;
import jpabook.jpashop.domain.order.query.OrderQueryDto;
import jpabook.jpashop.domain.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        // iter치면 intelliJ 예약어 사용 가능
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();

            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.forEach(o -> o.getItem().getName());
        }
        return all;
    }

    /**
     * 엔티티를 직접 노출하는 문제 해결
     * <p>
     * 하지만, 쿼리가 많이 발생하므로 최적화가 필요함
     */
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        return orders.stream()
                .map(OrderDto::new)
                .toList();
    }

    /**
     * 컬렉션 페치 조인 시 주의할 점
     * <p>
     * 1. 컬렉션 페치 조인은 1개만 사용할 수 있음. 컬렉션 둘 이상에 페치 조인 사용하면 데이터가 부정합하게 조회될 수 있음.
     * <p>
     * 2. 컬렉션 페치 조인에 대해 페이징을 사용하면 안됨. 일대다에서 일(1)을 기준으로 페이징을 하는 것이 목적임. 그런데 데이터는 다(N)를 기준으로 row가 생성됨.
     * <p>
     * 데이터를 DB에서 읽어오고 메모리(distinct 처리가 되지 않은 상태)에서 페이징하기 때문. 하이버네이트는 경고 로그를 남김. 최악에 시스템에 장애가 생길 수 있음
     * <p>
     * WARN: firstResult/maxResults specified with collection fetch; applying in memory
     */
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();
        return orders.stream()
                .map(OrderDto::new)
                .toList();
    }

    /**
     * 페이징 한계 돌파
     * <p>
     * 1. "ToOne" 관계는 모두 페치 조인한다. row수를 증가시키지 않으므로 페이징 쿼리에 영향을 주지 않는다. (member, delivery)
     * <p>
     * 2. 컬렉션은 지연 로딩으로 조회한다.
     * <p>
     * 지연 로딩 성능 최적화를 위해 hibernate.default_batch_fetch_size`(글로벌 최적화), `@BatchSize`(개별 최적화) 를 사용한다. 보통 1000 사용. 크기는 DB가
     * 순간 부하를 얼마나 견딜 수 있는지 기준으로 결정할 것.
     * <p>
     * - 장점
     * <p>
     * 1. 쿼리 호출 수가 1 + N -> 1 + 1으로 최적화 된다.
     * <p>
     * 2. 일반 조인보다 DB 데이터 전송량이 최적화 된다.
     * <p>
     * 3. 페치 조인 방식과 비교해서 쿼리 호출 수가 약간 증가하지만, DB 데이터 전송량이 감소한다.
     * <p>
     * 4.컬렉션 페치 조인은 페이징이 불가능 하지만 이 방법은 페이징이 가능하다.
     */
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit) {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);

        List<OrderDto> result = orders.stream()
                .map(OrderDto::new)
                .toList();

        return result;
    }

    /**
     * Query 수: 루트 1번, 컬렉션 N번
     * <p>
     * ToOne 관계들을 페치 조인으로 먼저 한번에 조회하고, ToMany 관계는 각각 별도로 처리한다.
     * <p>
     * N + 1 문제 존재
     */
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }

    //==DTO==//
    @Getter
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        // OrderItem도 엔티티이므로 외부에 노출되면 안됨
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            // 프록시 객체 초기화
            orderItems = order.getOrderItems().stream().map(OrderItemDto::new)
                    .toList();
        }
    }

    @Data
    static class OrderItemDto {
        private String itemName;    // 상품명
        private int orderPrice; // 주문 가격
        private int count;  // 주문 수량

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
}
