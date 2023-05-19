package chat.teco.tecochat.chat.domain.chat;

import static chat.teco.tecochat.chat.domain.chat.Answer.answer;
import static chat.teco.tecochat.chat.domain.chat.Chat.FREE_TOKEN;
import static chat.teco.tecochat.chat.domain.chat.GptModel.GPT_3_5_TURBO;
import static chat.teco.tecochat.chat.domain.chat.GptModel.GPT_4;
import static chat.teco.tecochat.chat.domain.chat.Question.question;
import static chat.teco.tecochat.chat.domain.chat.SettingMessage.BACK_END_SETTING;
import static chat.teco.tecochat.chat.exception.chat.ChatExceptionType.NO_AUTHORITY_CHANGE_TITLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import chat.teco.tecochat.chat.exception.chat.ChatException;
import chat.teco.tecochat.chat.fixture.ChatFixture;
import chat.teco.tecochat.common.exception.BaseExceptionType;
import chat.teco.tecochat.member.domain.Member;
import chat.teco.tecochat.member.fixture.MemberFixture.말랑;
import chat.teco.tecochat.member.fixture.MemberFixture.허브;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("Chat 은")
class ChatTest {

    @Test
    void QnA를_추가할_수_있다() {
        // given
        final Chat chat = ChatFixture.defaultChat();

        // when
        chat.addQuestionAndAnswer(new QuestionAndAnswer(
                question("안녕"),
                answer("응 안녕"),
                1
        ));

        // then
        final QuestionAndAnswer qna = chat.questionAndAnswers().get(0);
        assertAll(
                () -> assertThat(qna.answer())
                        .isEqualTo(answer("응 안녕")),
                () -> assertThat(qna.question())
                        .isEqualTo(question("안녕"))
        );
    }

    @Test
    void 주어진_가용_토큰만큼의_공간_이상을_확보하도록_오래된_순으로_메시지를_제외한_후_세팅_메세지를_포함하여_반환한다() {
        // given
        final List<QuestionAndAnswer> messages = List.of(
                new QuestionAndAnswer(
                        question("Q1"),
                        answer("A1"),
                        1500
                ),
                new QuestionAndAnswer(
                        question("Q2"),
                        answer("A2"),
                        200
                ),
                new QuestionAndAnswer(
                        question("Q3"),
                        answer("A3"),
                        1500
                ));
        final Chat chat = ChatFixture.chat(messages);

        // when
        final List<Message> messageInterfaces = chat.qnaWithFreeToken()
                .messagesWithSettingMessage(chat.settingMessage());

        // then
        assertThat(messageInterfaces).extracting(Message::content)
                .containsExactly(
                        chat.settingMessage().message(),
                        "Q2",
                        "A2",
                        "Q3",
                        "A3");
    }

    @Test
    void 주어진_가용_토큰만큼의_공간_이상을_확보하도록_오래된_순으로_메시지를_제외한_후_세팅_메세지를_포함하여_반환한다_엣지_케이스() {
        // given
        final List<QuestionAndAnswer> messages = List.of(
                new QuestionAndAnswer(
                        question("Q1"),
                        answer("A1"),
                        1500
                ),
                new QuestionAndAnswer(
                        question("Q2"),
                        answer("A2"),
                        200
                ),
                new QuestionAndAnswer(
                        question("Q3"),
                        answer("A3"),
                        GPT_3_5_TURBO.maxTokens() - FREE_TOKEN
                ));
        final Chat chat = ChatFixture.chat(messages);

        // when
        final List<Message> result = chat.qnaWithFreeToken()
                .messagesWithSettingMessage(chat.settingMessage());

        // then
        assertThat(result).extracting(Message::content)
                .containsExactly(chat.settingMessage().message(),
                        "Q3",
                        "A3"
                );
    }

    @Nested
    class 제목_수정_시 {

        @Test
        void 작성자가_아니라면_수정할_수_없다() {
            // given
            Member member = 말랑.회원();
            Member other = 허브.회원();
            Chat chat = new Chat(GPT_4, BACK_END_SETTING, "제목", member.id());

            // when
            BaseExceptionType baseExceptionType = assertThrows(ChatException.class, () ->
                    chat.updateTitle(other.id(), "변경 제목")
            ).exceptionType();

            // then
            assertThat(chat.title()).isEqualTo("제목");
            assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_CHANGE_TITLE);
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
}
