package chat.woowa.woowachat.member.dto;

import chat.woowa.woowachat.member.domain.Course;
import chat.woowa.woowachat.member.domain.Member;

public record SignUpDto(
        String name,
        Course course
) {
    public Member toDomain() {
        return new Member(name, course);
    }
}
