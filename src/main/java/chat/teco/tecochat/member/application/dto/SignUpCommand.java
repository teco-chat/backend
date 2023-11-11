package chat.teco.tecochat.member.application.dto;

import chat.teco.tecochat.member.domain.Course;
import chat.teco.tecochat.member.domain.Member;

public record SignUpCommand(
        String name,
        Course course
) {
    public Member toMember() {
        return new Member(name, course);
    }
}
