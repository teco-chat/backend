package chat.teco.tecochat.chat.application.keyword;

import static chat.teco.tecochat.domain.chat.GptModel.GPT_4;
import static chat.teco.tecochat.domain.chat.SettingMessage.BACK_END_SETTING;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import chat.teco.tecochat.chat.domain.chat.event.ChatCreatedEvent;
import chat.teco.tecochat.chat.domain.keyword.KeywordExtractor;
import chat.teco.tecochat.common.FakeTransactionTemplate;
import chat.teco.tecochat.common.event.EventHistoryRepository;
import chat.teco.tecochat.domain.chat.Chat;
import chat.teco.tecochat.domain.chat.ChatRepository;
import chat.teco.tecochat.domain.keyword.Keyword;
import chat.teco.tecochat.domain.keyword.KeywordRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.TransactionTemplate;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("CreateKeywordWithChatCreatedEventHandler(채팅 생성 이벤트를 받아 채팅의 키워드 추출) 은(는)")
class CreateKeywordWithChatCreatedEventHandlerTest {

    private final EventHistoryRepository eventHistoryRepository = mock(EventHistoryRepository.class);
    private final ChatRepository chatRepository = mock(ChatRepository.class);
    private final KeywordRepository keywordRepository = mock(KeywordRepository.class);
    private final TransactionTemplate transactionTemplate = FakeTransactionTemplate.spied();
    private final KeywordExtractor keywordExtractor = mock(KeywordExtractor.class);

    private final CreateKeywordWithChatCreatedEventHandler handler = new CreateKeywordWithChatCreatedEventHandler(
            eventHistoryRepository,
            chatRepository,
            keywordRepository,
            transactionTemplate,
            keywordExtractor
    );

    private final Chat chat = new Chat(GPT_4, BACK_END_SETTING, "제목", 1L);

    @Test
    void 채팅_생성_이벤트를_받아_해당_채팅의_키워드를_추출하여_저장한다() {
        // given
        given(chatRepository.findWithQuestionAndAnswersById(anyLong()))
                .willReturn(Optional.of(chat));
        given(keywordExtractor.extractKeywords(chat))
                .willReturn(List.of(
                        new Keyword("답변1", chat),
                        new Keyword("답변2", chat),
                        new Keyword("답변3", chat)));

        // when
        handler.handle(new ChatCreatedEvent(1L, LocalDateTime.MAX));

        // then
        verify(eventHistoryRepository, times(1)).save(any());
        verify(keywordRepository, times(1)).saveAll(anyCollection());
    }

    @Test
    void 키워드_추출시_오류가_발생하면_저장되지_않는다() {
        // given
        given(chatRepository.getWithQuestionAndAnswersById(anyLong()))
                .willReturn(chat);
        given(keywordExtractor.extractKeywords(chat))
                .willThrow(new IllegalStateException("키워드 추출에 실패했습니다."));

        // expect
        assertThrows(IllegalStateException.class, () ->
                handler.handle(new ChatCreatedEvent(1L, LocalDateTime.MAX))
        );
        verify(eventHistoryRepository, times(0)).save(any());
        verify(keywordRepository, times(0)).saveAll(anyCollection());
    }
}
