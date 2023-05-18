package chat.teco.tecochat.like.chatlike.application;

import static chat.teco.tecochat.member.domain.Course.BACKEND;

import chat.teco.tecochat.chat.domain.chat.Chat;
import chat.teco.tecochat.chat.domain.chat.ChatRepository;
import chat.teco.tecochat.chat.fixture.ChatFixture;
import chat.teco.tecochat.common.config.JpaConfig;
import chat.teco.tecochat.common.config.QueryDslConfig;
import chat.teco.tecochat.like.chatlike.application.service.ChatLikeQueryService;
import chat.teco.tecochat.like.chatlike.domain.ChatLike;
import chat.teco.tecochat.like.chatlike.domain.ChatLikeRepository;
import chat.teco.tecochat.member.domain.Course;
import chat.teco.tecochat.member.domain.Member;
import chat.teco.tecochat.member.domain.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@SuppressWarnings("NonAsciiCharacters")
@Import({JpaConfig.class, QueryDslConfig.class, ChatLikeQueryService.class})
@DataJpaTest
public class ChatLikeQueryUseCaseTest {

    @Autowired
    protected ChatRepository chatRepository;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected ChatLikeRepository chatLikeRepository;

    @Autowired
    protected EntityManager em;

    protected Member 말랑;
    protected Member 허브;
    protected Member 박스터;
    protected Chat 말랑_채팅;
    protected Chat 허브_채팅;
    protected Chat 박스터_채팅;
    protected ChatLike 말랑_말랑채팅_좋아요;
    protected ChatLike 허브_말랑채팅_좋아요;
    protected ChatLike 박스터_말랑채팅_좋아요;
    protected ChatLike 말랑_허브채팅_좋아요;
    protected ChatLike 허브_허브채팅_좋아요;
    protected ChatLike 박스터_허브채팅_좋아요;
    protected ChatLike 말랑_박스터채팅_좋아요;
    protected ChatLike 허브_박스터채팅_좋아요;
    protected ChatLike 박스터_박스터채팅_좋아요;

    @BeforeEach
    void setUp() {
        말랑 = memberRepository.save(new Member("말랑_좋아요", BACKEND));
        허브 = memberRepository.save(new Member("허브_좋아요", Course.FRONTEND));
        박스터 = memberRepository.save(new Member("박스터", Course.ANDROID));
        말랑_채팅 = chatRepository.save(ChatFixture.defaultChat(말랑.id()));
        허브_채팅 = chatRepository.save(ChatFixture.defaultChat(허브.id()));
        박스터_채팅 = chatRepository.save(ChatFixture.defaultChat(박스터.id()));

        말랑_말랑채팅_좋아요 = chatLikeRepository.save(new ChatLike(말랑.id(), 말랑_채팅.id()));
        허브_말랑채팅_좋아요 = chatLikeRepository.save(new ChatLike(허브.id(), 말랑_채팅.id()));
        박스터_말랑채팅_좋아요 = chatLikeRepository.save(new ChatLike(박스터.id(), 말랑_채팅.id()));
        말랑_채팅.increaseLike();
        말랑_채팅.increaseLike();
        말랑_채팅.increaseLike();

        말랑_허브채팅_좋아요 = chatLikeRepository.save(new ChatLike(말랑.id(), 허브_채팅.id()));
        허브_허브채팅_좋아요 = chatLikeRepository.save(new ChatLike(허브.id(), 허브_채팅.id()));
        박스터_허브채팅_좋아요 = chatLikeRepository.save(new ChatLike(박스터.id(), 허브_채팅.id()));
        허브_채팅.increaseLike();
        허브_채팅.increaseLike();
        허브_채팅.increaseLike();

        말랑_박스터채팅_좋아요 = chatLikeRepository.save(new ChatLike(말랑.id(), 박스터_채팅.id()));
        허브_박스터채팅_좋아요 = chatLikeRepository.save(new ChatLike(허브.id(), 박스터_채팅.id()));
        박스터_박스터채팅_좋아요 = chatLikeRepository.save(new ChatLike(박스터.id(), 박스터_채팅.id()));
        박스터_채팅.increaseLike();
        박스터_채팅.increaseLike();
        박스터_채팅.increaseLike();
        em.flush();
        em.clear();
    }
}
