package chat.teco.tecochat.chat.query.mapper;

import chat.teco.tecochat.chat.domain.chat.Chat;
import chat.teco.tecochat.chat.domain.chat.Message;
import chat.teco.tecochat.chat.domain.chat.QuestionAndAnswer;
import chat.teco.tecochat.chat.domain.keyword.Keyword;
import chat.teco.tecochat.chat.query.usecase.QueryChatByIdUseCase.QueryChatByIdResponse;
import chat.teco.tecochat.chat.query.usecase.QueryChatByIdUseCase.QueryChatByIdResponse.QueryKeywordDto;
import chat.teco.tecochat.chat.query.usecase.QueryChatByIdUseCase.QueryChatByIdResponse.QueryMessageDto;
import chat.teco.tecochat.chat.query.usecase.SearchChatUseCase.SearchChatResponse;
import chat.teco.tecochat.chat.query.usecase.SearchChatUseCase.SearchChatResponse.SearchKeywordDto;
import chat.teco.tecochat.domain.member.Member;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChatMapper {

    public static SearchChatResponse mapToSearchQueryResponse(
            Chat chat,
            Member member,
            List<Keyword> keywords
    ) {
        List<SearchKeywordDto> keywordQueryDtos = keywords.stream()
                .map(ChatMapper::mapToKeywordSearchQueryDto)
                .toList();
        return new SearchChatResponse(
                chat.id(),
                member.id(),
                member.getName(),
                member.getCourse(),
                chat.title(),
                chat.likeCount(),
                chat.commentCount(),
                chat.questionAndAnswers().size(),
                keywordQueryDtos,
                chat.createdAt()
        );
    }

    private static SearchKeywordDto mapToKeywordSearchQueryDto(Keyword keyword) {
        return new SearchKeywordDto(keyword.keyword());
    }

    public static QueryChatByIdResponse mapToQueryResponse(
            Chat chat,
            Member member,
            boolean isAlreadyClickLike,
            List<Keyword> keywords
    ) {
        List<QueryMessageDto> messages = new ArrayList<>();
        for (QuestionAndAnswer qna : chat.questionAndAnswers()) {
            messages.add(mapToMessageQueryDto(qna.question(), qna.createdAt()));
            messages.add(mapToMessageQueryDto(qna.answer(), qna.createdAt()));
        }
        List<QueryKeywordDto> queryKeywordDtos = keywords.stream()
                .map(ChatMapper::mapToKeywordQueryDto)
                .toList();
        return new QueryChatByIdResponse(
                chat.id(),
                member.getName(),
                member.getCourse(),
                chat.title(),
                chat.likeCount(),
                isAlreadyClickLike,
                chat.createdAt(),
                messages,
                queryKeywordDtos);
    }

    private static QueryMessageDto mapToMessageQueryDto(
            Message message,
            LocalDateTime createdAt
    ) {
        return new QueryMessageDto(message.content(), message.roleName(), createdAt);
    }

    private static QueryKeywordDto mapToKeywordQueryDto(Keyword keyword) {
        return new QueryKeywordDto(keyword.keyword());
    }
}
