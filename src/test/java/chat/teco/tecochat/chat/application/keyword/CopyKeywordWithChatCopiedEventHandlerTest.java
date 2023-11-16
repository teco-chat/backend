package chat.teco.tecochat.chat.application.keyword;

import static chat.teco.tecochat.chat.domain.chat.GptModel.GPT_4;
import static chat.teco.tecochat.chat.domain.chat.SettingMessage.BACK_END_SETTING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import chat.teco.tecochat.chat.domain.chat.Chat;
import chat.teco.tecochat.chat.domain.chat.ChatRepository;
import chat.teco.tecochat.chat.domain.chat.event.ChatCopiedEvent;
import chat.teco.tecochat.domain.chat.KeywordRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("CopyKeywordWithChatCopiedEventHandler(채팅 복제시 키워드 복제 핸들러) 은(는)")
class CopyKeywordWithChatCopiedEventHandlerTest {

    private final ChatRepository chatRepository = mock(ChatRepository.class);
    private final KeywordRepository keywordRepository = mock(KeywordRepository.class);
    private final CopyKeywordWithChatCopiedEventHandler handler =
            new CopyKeywordWithChatCopiedEventHandler(chatRepository, keywordRepository);

    @Test
    void 채팅_복제_이벤트를_받아_기존_채팅의_키워드도_복제한다() {
        // given
        Long originChatId = 1L;
        Long copiedChatId = 2L;
        Chat copiedChat = new Chat(2L, GPT_4, BACK_END_SETTING, "제목", 1L);
        ChatCopiedEvent event = new ChatCopiedEvent(originChatId, copiedChatId);
        given(chatRepository.findById(2L))
                .willReturn(Optional.of(copiedChat));

        // when
        handler.handle(event);

        // then
        then(keywordRepository)
                .should(times(1))
                .saveAll(any());
    }
}
