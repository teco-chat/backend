package chat.teco.tecochat.ui

import chat.teco.tecochat.application.MemberData
import chat.teco.tecochat.application.MemberService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class MemberController(
    private val memberService: MemberService
) {

    @PostMapping("/members")
    fun signUp(@RequestBody memberData: MemberData): ResponseEntity<Void> {
        memberService.signUp(memberData)
        return ResponseEntity.status(HttpStatus.CREATED.value()).build()
    }
}
