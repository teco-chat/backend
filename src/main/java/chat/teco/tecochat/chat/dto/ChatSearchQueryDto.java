package chat.teco.tecochat.chat.dto;

import chat.teco.tecochat.chat.domain.Chat;
import chat.teco.tecochat.chat.domain.keyword.Keyword;
import chat.teco.tecochat.chat.dto.ChatQueryDto.KeywordQueryDto;
import chat.teco.tecochat.member.domain.Course;
import chat.teco.tecochat.member.domain.Member;
import java.time.LocalDateTime;
import java.util.List;

public record ChatSearchQueryDto(
        Long id,
        Long crewId,
        String crewName,
        Course course,
        String title,
        int likeCount,
        int totalQnaCount,
        List<KeywordQueryDto> keywords,
        LocalDateTime createdAt
) {
    public static ChatSearchQueryDto of(final Chat chat, final Member member, final List<Keyword> keywords) {
        final List<KeywordQueryDto> keywordQueryDtos = keywords.stream()
                .map(KeywordQueryDto::from)
                .toList();
        return new ChatSearchQueryDto(
                chat.id(),
                member.id(),
                member.name(),
                member.course(),
                chat.title(),
                chat.likeCount(),
                chat.questionAndAnswers().size(),
                keywordQueryDtos,
                chat.createdAt()
        );
    }
}
