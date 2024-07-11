package jpabook.jpashop.domain.member;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true) // 메소드들의 기본 세팅은 readOnly지만
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

//    @Autowired  // setter 빈주입을 하면 테스트 코드 작성시 모킹하기는 용이함. 단점은 런타임에 값이 바뀔 수 있음. 그래서 setter 주입은 안좋음. 웬만하면 생성자 주입을 쓸 것
//    public void setMemberRepository(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

//    @Autowired  // spring에서 필드가 1개인 경우는 Autowired 어노테이션이 없어도 자동으로 주입
//    RequiredArgsConstructor를 사용하면  final이 붙은 필드만 생성자 주입해줌. Lombok 많이 사용
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    /**
     * 회원 가입
     */
    @Transactional  // 따로 Transcational 어노테이션을 부여하면 이게 더 우선권을 가짐
    public Long join(Member member) {
        // 중복이름 검증

        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    /**
     * 중복 회원 검증
     */
    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    /**
     * 회원 조회
     */
    private List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }
}
