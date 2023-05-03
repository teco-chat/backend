package chat.teco.tecochat.chat.fixture;

import static chat.teco.tecochat.chat.domain.GptModel.GPT_3_5_TURBO;
import static chat.teco.tecochat.chat.domain.SettingMessage.BACK_END_SETTING;

import chat.teco.tecochat.chat.domain.Chat;
import chat.teco.tecochat.chat.domain.GptModel;
import chat.teco.tecochat.chat.domain.QuestionAndAnswer;
import java.util.Arrays;
import java.util.List;

public class ChatFixture {

    public static Chat defaultChat() {
        return new Chat(GPT_3_5_TURBO,
                BACK_END_SETTING,
                "제목",
                1L);
    }

    public static Chat chat(final QuestionAndAnswer... questionAndAnswers) {
        final Chat chat = new Chat(GPT_3_5_TURBO,
                BACK_END_SETTING,
                questionAndAnswers[0].question().content(),
                1L);

        for (final QuestionAndAnswer questionAndAnswer : questionAndAnswers) {
            chat.addQuestionAndAnswer(questionAndAnswer);
        }
        return chat;
    }

    public static Chat chat(final List<QuestionAndAnswer> questionAndAnswers) {
        return chatWithModel(GPT_3_5_TURBO, questionAndAnswers);
    }

    public static Chat chatWithModel(final GptModel gptModel,
                                     final QuestionAndAnswer... questionAndAnswers) {
        return chatWithModel(gptModel, Arrays.asList(questionAndAnswers));
    }

    public static Chat chatWithModel(final GptModel gptModel,
                                     final List<QuestionAndAnswer> questionAndAnswers) {
        final Chat chat = new Chat(gptModel,
                BACK_END_SETTING,
                questionAndAnswers.get(0).question().content(),
                1L);

        for (final QuestionAndAnswer questionAndAnswer : questionAndAnswers) {
            chat.addQuestionAndAnswer(questionAndAnswer);
        }
        return chat;
    }
}
