package jpabook.jpashop.domain.order.query;

import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> result = findOrders();

        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });
        return result;
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                        // QueryDSL을 사용하면 자동으로 위치를 잡아주므로 이렇게 절대 경로를 써주지 않아도 됨
                        "select new jpabook.jpashop.domain.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count) from OrderItem oi"
                                + " join oi.item i"
                                + " where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    private List<OrderQueryDto> findOrders() {
        return em.createQuery(
                        "select new jpabook.jpashop.domain.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address) from Order o"
                                + " join o.member m"
                                + " join o.delivery d", OrderQueryDto.class)
                .getResultList();
    }
}
