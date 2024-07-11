package jpabook.jpashop.domain.member;

import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepository {
    private final EntityManager em;

    public Long save(Member member) {
        em.persist(member);
        return member.getId();  // 사이드 이펙트를 방지하기 위해 id 정보만 리턴. 필요하면 id를 이용해서 다시 조회하도록.
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name) {
        // :는 변수 바인딩 문법. setParameter을 통해 동적으로 값 설정 가능
        return em.createQuery("select m from Member m where m.name = :memberName", Member.class)
                .setParameter("memberName", name)
                .getResultList();
    }
}
