package chat.teco.tecochat.member.presentation.controller.request;

import chat.teco.tecochat.member.application.usecase.SignUpUseCase.SignUpCommand;
import chat.teco.tecochat.member.domain.Course;

public record SignUpRequest(
        String name,
        Course course
) {
    public SignUpCommand toServiceDto() {
        return new SignUpCommand(name, course);
    }
}
