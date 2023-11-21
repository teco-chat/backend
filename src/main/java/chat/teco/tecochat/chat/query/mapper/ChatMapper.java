package chat.teco.tecochat.chat.query.mapper;

import chat.teco.tecochat.chat.query.usecase.QueryChatByIdUseCase.QueryChatByIdResponse;
import chat.teco.tecochat.chat.query.usecase.QueryChatByIdUseCase.QueryChatByIdResponse.QueryKeywordDto;
import chat.teco.tecochat.chat.query.usecase.QueryChatByIdUseCase.QueryChatByIdResponse.QueryMessageDto;
import chat.teco.tecochat.chat.query.usecase.SearchChatUseCase.SearchChatResponse;
import chat.teco.tecochat.chat.query.usecase.SearchChatUseCase.SearchChatResponse.SearchKeywordDto;
import chat.teco.tecochat.domain.chat.Chat;
import chat.teco.tecochat.domain.chat.Message;
import chat.teco.tecochat.domain.chat.QuestionAndAnswer;
import chat.teco.tecochat.domain.keyword.Keyword;
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
                chat.getId(),
                member.getId(),
                member.getName(),
                member.getCourse(),
                chat.getTitle(),
                chat.getLikeCount(),
                chat.getCommentCount(),
                chat.getQuestionAndAnswers().getQuestionAndAnswers().size(),
                keywordQueryDtos,
                chat.getCreatedAt()
        );
    }

    private static SearchKeywordDto mapToKeywordSearchQueryDto(Keyword keyword) {
        return new SearchKeywordDto(keyword.getKeyword());
    }

    public static QueryChatByIdResponse mapToQueryResponse(
            Chat chat,
            Member member,
            boolean isAlreadyClickLike,
            List<Keyword> keywords
    ) {
        List<QueryMessageDto> messages = new ArrayList<>();
        for (QuestionAndAnswer qna : chat.getQuestionAndAnswers().getQuestionAndAnswers()) {
            messages.add(mapToMessageQueryDto(qna.getQuestion(), qna.getCreatedAt()));
            messages.add(mapToMessageQueryDto(qna.getAnswer(), qna.getCreatedAt()));
        }
        List<QueryKeywordDto> queryKeywordDtos = keywords.stream()
                .map(ChatMapper::mapToKeywordQueryDto)
                .toList();
        return new QueryChatByIdResponse(
                chat.getId(),
                member.getName(),
                member.getCourse(),
                chat.getTitle(),
                chat.getLikeCount(),
                isAlreadyClickLike,
                chat.getCreatedAt(),
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
        return new QueryKeywordDto(keyword.getKeyword());
    }
}
