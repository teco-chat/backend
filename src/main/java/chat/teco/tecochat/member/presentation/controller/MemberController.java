package chat.teco.tecochat.member.presentation.controller;

import static org.springframework.http.HttpStatus.CREATED;

import chat.teco.tecochat.member.application.service.MemberService;
import chat.teco.tecochat.member.presentation.controller.request.SignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/members")
    ResponseEntity<Void> signUp(@RequestBody SignUpRequest signUpRequest) {
        memberService.signUp(signUpRequest.toServiceDto());
        return ResponseEntity.status(CREATED.value()).build();
    }
}
