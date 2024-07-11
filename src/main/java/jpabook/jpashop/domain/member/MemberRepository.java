package jpabook.jpashop.domain.member;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class MemberRepository {
    @PersistenceContext
    private EntityManager em;

    public Long save(Member member) {
        em.persist(member);
        return member.getId();  // 사이드 이펙트를 방지하기 위해 id 정보만 리턴. 필요하면 id를 이용해서 다시 조회하도록.
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }
}
