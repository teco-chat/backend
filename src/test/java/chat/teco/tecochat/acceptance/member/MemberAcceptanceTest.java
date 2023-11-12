package chat.teco.tecochat.acceptance.member;

import static chat.teco.tecochat.acceptance.common.AcceptanceTestSteps.요청_결과의_상태를_검증한다;
import static chat.teco.tecochat.acceptance.common.AcceptanceTestSteps.정상_생성;
import static chat.teco.tecochat.acceptance.member.MemberSteps.회원_가입_요청;
import static chat.teco.tecochat.member.domain.Course.BACKEND;
import static chat.teco.tecochat.member.domain.Course.FRONTEND;
import static org.assertj.core.api.Assertions.assertThat;

import chat.teco.tecochat.domain.member.MemberRepository;
import chat.teco.tecochat.member.domain.Member;
import io.restassured.RestAssured;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("MemberController 인수 테스트")
@Sql("/sql/h2ChatTruncate.sql")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MemberAcceptanceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void 회원_가입을_진행한다() {
        // given
        var 회원_가입_응답 = 회원_가입_요청("말랑", BACKEND);

        // then
        요청_결과의_상태를_검증한다(회원_가입_응답, 정상_생성);
        assertThat(memberRepository.findAll()).hasSize(1);
    }

    @Test
    void 이름이_이미_있으면_코스를_변경한다() {
        // given
        회원_가입_요청("말랑", BACKEND);

        // when
        회원_가입_요청("말랑", FRONTEND);

        // then
        // TODO 나중에 회원가입 제대로 만들고, 조회 기능 만들면 그때 수정
        List<Member> members = memberRepository.findAll();
        assertThat(members).hasSize(1);
        Member member = members.get(0);
        assertThat(member.course()).isEqualTo(FRONTEND);
    }
}
