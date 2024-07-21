package jpabook.jpashop.domain.order.simplequery;

import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {
    private final EntityManager em;

    public List<OrderSimpleQueryDto> findOrderDtos() {
        return em.createQuery(
                        "select new jpabook.jpashop.domain.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address)"
                                + " from Order o"
                                + " join o.member m"
                                + " join o.delivery d", OrderSimpleQueryDto.class)
                .getResultList();
    }
}
