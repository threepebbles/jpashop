package jpabook.jpashop.domain.item;

import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
    private final EntityManager em;

    public void save(Item item) {
        // item은 jpa에 저장하기 전까지 id값이 없음
        // 새로 생성한 객체라는 뜻
        if (item.getId() == null) {
            // 신규 등록인 경우
            em.persist(item);
        } else {
            // 이미 DB에 있는 item인 경우
            em.merge(item);
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
