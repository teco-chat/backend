package chat.woowa.woowachat.member.presentation.controller;

import static org.springframework.http.HttpStatus.CREATED;

import chat.woowa.woowachat.member.application.MemberService;
import chat.woowa.woowachat.member.dto.SignUpDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberController {

    private final MemberService memberService;

    public MemberController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/members")
    ResponseEntity<Void> signUp(@RequestBody final SignUpDto signUpDto) {
        memberService.signUp(signUpDto);
        return ResponseEntity.status(CREATED.value()).build();
    }
}
