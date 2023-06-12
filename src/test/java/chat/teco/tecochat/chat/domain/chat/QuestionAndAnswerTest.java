package chat.teco.tecochat.chat.domain.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("QuestionAndAnswer 은(는)")
class QuestionAndAnswerTest {

    @Test
    void 복제할_수_있다() {
        // given
        QuestionAndAnswer qna = new QuestionAndAnswer("질문1", "답변1");

        // when
        QuestionAndAnswer copied = qna.copy();

        // then
        assertAll(
                () -> assertThat(copied.question().content()).isEqualTo("질문1"),
                () -> assertThat(copied.answer().content()).isEqualTo("답변1"),
                () -> assertThat(copied).isNotEqualTo(qna)
        );
    }
}
