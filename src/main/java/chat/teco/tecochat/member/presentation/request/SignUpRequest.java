package chat.teco.tecochat.member.presentation.request;

import chat.teco.tecochat.member.application.dto.SignUpCommand;
import chat.teco.tecochat.member.domain.Course;

public record SignUpRequest(
        String name,
        Course course
) {
    public SignUpCommand toServiceDto() {
        return new SignUpCommand(name, course);
    }
}
