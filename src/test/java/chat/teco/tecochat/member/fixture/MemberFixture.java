package chat.teco.tecochat.member.fixture;

import chat.teco.tecochat.application.MemberData;
import chat.teco.tecochat.domain.member.Course;
import chat.teco.tecochat.domain.member.Member;

@SuppressWarnings("NonAsciiCharacters")
public class MemberFixture {

    public static class 말랑 {
        public static final Long ID = 1L;
        public static final String 이름 = "말랑";
        public static final Course 과정 = Course.BACKEND;
        public static final MemberData 회원가입_요청 = new MemberData(이름, 과정);

        public static Member 회원() {
            return new Member(이름, 과정, ID);
        }
    }

    public static class 허브 {
        public static final Long ID = 2L;
        public static final String 이름 = "허브";
        public static final Course 과정 = Course.FRONTEND;
        public static final MemberData 회원가입_요청 = new MemberData(이름, 과정);

        public static Member 회원() {
            return new Member(이름, 과정, ID);
        }
    }
}
