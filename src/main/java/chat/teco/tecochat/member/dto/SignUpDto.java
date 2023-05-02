package chat.teco.tecochat.member.dto;

import chat.teco.tecochat.member.domain.Course;
import chat.teco.tecochat.member.domain.Member;

public record SignUpDto(
        String name,
        Course course
) {
    public Member toDomain() {
        return new Member(name, course);
    }
}
