package chat.teco.tecochat.member.fixture;

import chat.teco.tecochat.application.SignUpRequest;
import chat.teco.tecochat.member.domain.Course;
import chat.teco.tecochat.member.domain.Member;

@SuppressWarnings("NonAsciiCharacters")
public class MemberFixture {

    public static class 말랑 {
        public static final Long ID = 1L;
        public static final String 이름 = "말랑";
        public static final Course 과정 = Course.BACKEND;
        public static final SignUpRequest 회원가입_요청 = new SignUpRequest(이름, 과정);

        public static Member 회원() {
            return new Member(ID, 이름, 과정);
        }
    }

    public static class 허브 {
        public static final Long ID = 2L;
        public static final String 이름 = "허브";
        public static final Course 과정 = Course.FRONTEND;
        public static final SignUpRequest 회원가입_요청 = new SignUpRequest(이름, 과정);

        public static Member 회원() {
            return new Member(ID, 이름, 과정);
        }
    }
}
