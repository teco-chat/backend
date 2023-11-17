package chat.teco.tecochat.chat.domain.chat;

import static chat.teco.tecochat.chat.domain.chat.Answer.answer;
import static chat.teco.tecochat.chat.domain.chat.GptModel.GPT_4;
import static chat.teco.tecochat.chat.domain.chat.Question.question;
import static chat.teco.tecochat.chat.domain.chat.SettingMessage.BACK_END_SETTING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import chat.teco.tecochat.chat.fixture.ChatFixture;
import chat.teco.tecochat.member.domain.Member;
import chat.teco.tecochat.member.fixture.MemberFixture.말랑;
import chat.teco.tecochat.member.fixture.MemberFixture.허브;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("Chat(채팅) 은")
class ChatTest {

    @Test
    void QnA를_추가할_수_있다() {
        // given
        Chat chat = ChatFixture.defaultChat();

        // when
        chat.addQuestionAndAnswer(new QuestionAndAnswer(
                "안녕", "응 안녕"));

        // then
        QuestionAndAnswer qna = chat.questionAndAnswers().get(0);
        assertAll(
                () -> assertThat(qna.answer())
                        .isEqualTo(answer("응 안녕")),
                () -> assertThat(qna.question())
                        .isEqualTo(question("안녕"))
        );
    }

    @Test
    void 마지막_3개의_질문과_답변만을_반환한다() {
        // given
        List<QuestionAndAnswer> questionAndAnswers = List.of(
                new QuestionAndAnswer("질문1", "답변1"),
                new QuestionAndAnswer("질문2", "답변2"),
                new QuestionAndAnswer("질문3", "답변3"),
                new QuestionAndAnswer("질문4", "답변4")
        );
        Chat chat = ChatFixture.chat(questionAndAnswers);

        // when
        QuestionAndAnswers result = chat.last3QuestionAndAnswers();

        // then
        assertThat(result.questionAndAnswers())
                .extracting(QuestionAndAnswer::question)
                .containsExactly(question("질문2"), question("질문3"), question("질문4"));
    }

    @Test
    void 질문답변이_3개보다_적다면_전부_반환한다() {
        // given
        List<QuestionAndAnswer> questionAndAnswers = List.of(
                new QuestionAndAnswer("질문1", "답변1"),
                new QuestionAndAnswer("질문2", "답변2")
        );
        Chat chat = ChatFixture.chat(questionAndAnswers);

        // when
        QuestionAndAnswers result = chat.last3QuestionAndAnswers();

        // then
        assertThat(result.questionAndAnswers())
                .extracting(QuestionAndAnswer::question)
                .containsExactly(question("질문1"), question("질문2"));
    }

    @Nested
    class 제목_수정_시 {

        @Test
        void 작성자가_아니라면_수정할_수_없다() {
            // given
            Member member = 말랑.회원();
            Member other = 허브.회원();
            Chat chat = new Chat(GPT_4, BACK_END_SETTING, "제목", member.id());

            // when + then
            assertThrows(IllegalStateException.class, () ->
                    chat.updateTitle(other.id(), "변경 제목")
            );
            assertThat(chat.title()).isEqualTo("제목");
        }

        @Test
        void 작성자라면_수정한다() {
            Member member = 말랑.회원();
            Chat chat = new Chat(GPT_4, BACK_END_SETTING, "제목", member.id());

            // when
            chat.updateTitle(member.id(), "변경 제목");

            // then
            assertThat(chat.title()).isEqualTo("변경 제목");
        }
    }

    @Nested
    class 채팅_복사_시 {

        private Chat chat;

        @BeforeEach
        void setUp() {
            chat = new Chat(GPT_4, BACK_END_SETTING, "제목", 1L);
            chat.addQuestionAndAnswer(new QuestionAndAnswer("질문1", "답변1"));
            chat.addQuestionAndAnswer(new QuestionAndAnswer("질문2", "답변2"));
            chat.increaseLike();
            chat.increaseLike();
            chat.increaseComment();
        }

        @Test
        void 진행한_채팅을_모두_복사한다_이때_좋아요_수와_댓글_수는_복사되지_않는다() {
            // given
            Long memberId = 100L;

            // when
            Chat copy = chat.copy(memberId);

            // then
            Chat expected = new Chat(GPT_4, BACK_END_SETTING, "제목", memberId);
            expected.addQuestionAndAnswer(new QuestionAndAnswer("질문1", "답변1"));
            expected.addQuestionAndAnswer(new QuestionAndAnswer("질문2", "답변2"));

            assertThat(copy)
                    .usingRecursiveComparison()
                    .isEqualTo(expected);
        }

        @Test
        void 자신의_채팅도_복사가_가능하다() {
            // when
            Chat copy = chat.copy(1L);

            // then
            Chat expected = new Chat(GPT_4, BACK_END_SETTING, "제목", 1L);
            expected.addQuestionAndAnswer(new QuestionAndAnswer("질문1", "답변1"));
            expected.addQuestionAndAnswer(new QuestionAndAnswer("질문2", "답변2"));

            assertThat(copy)
                    .usingRecursiveComparison()
                    .isEqualTo(expected);
        }
    }
}
