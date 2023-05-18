package chat.teco.tecochat.member.application.usecase;

import chat.teco.tecochat.member.domain.Course;
import chat.teco.tecochat.member.domain.Member;

public interface SignUpUseCase {

    void signUp(SignUpCommand signUpCommand);

    record SignUpCommand(
            String name,
            Course course
    ) {
        public Member toDomain() {
            return new Member(name, course);
        }
    }
}
