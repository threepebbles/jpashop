package jpabook.jpashop.api;

import jakarta.validation.Valid;
import java.util.List;
import jpabook.jpashop.api.dto.MemberForm;
import jpabook.jpashop.domain.common.Address;
import jpabook.jpashop.domain.member.Member;
import jpabook.jpashop.domain.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());

        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    // 원래 Valid에서 에러가 발생하면 Controller 코드가 실행안되고 에러페이지를 요청하는데,
    // BindingResult 가 파라미터에 있으면 코드가 그대로 실행된다. (에러도 코드 내에서 처리하겠다는 의미)
    public String create(@Valid MemberForm form, BindingResult result) {

        // 에러 발생시 members/createMemberForm 페이지로 넘어감
        // 파라미터로 넘어온 폼데이터와 BindingResult를 해당 페이지에서 사용할 수 있게 끌고 가줌.
        // 해당 페이지에서 Bindingresult에 담긴 에러 메세지를 가져다가 화면에 출력하는 행위 등을 할 수 있음
        if (result.hasErrors()) {
            return "members/createMemberForm";
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/";
    }

    @GetMapping("/members")
    public String list(Model model) {
        // 엔티티가 변화하면 템플릿 스펙이 변화할 수 있기 때문에
        // Member엔티티 객체를 그대로 사용하는 것보다 DTO를 반환해서 넘겨주는게 더 깔끔한 방법.
        List<Member> members = memberService.findMembers();

        model.addAttribute("members", members);
        return "members/memberList";
    }
}
