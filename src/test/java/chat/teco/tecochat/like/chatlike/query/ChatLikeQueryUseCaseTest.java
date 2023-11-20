package chat.teco.tecochat.like.chatlike.query;

import chat.teco.tecochat.chat.domain.keyword.Keyword;
import chat.teco.tecochat.chat.fixture.ChatFixture;
import chat.teco.tecochat.common.config.JpaConfig;
import chat.teco.tecochat.common.config.QueryDslConfig;
import chat.teco.tecochat.domain.chat.Chat;
import chat.teco.tecochat.domain.chat.ChatRepository;
import chat.teco.tecochat.domain.chatlike.ChatLike;
import chat.teco.tecochat.domain.chatlike.ChatLikeRepository;
import chat.teco.tecochat.domain.keyword.KeywordRepository;
import chat.teco.tecochat.domain.member.Course;
import chat.teco.tecochat.domain.member.Member;
import chat.teco.tecochat.domain.member.MemberRepository;
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
    protected KeywordRepository keywordRepository;

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
    protected String 말랑채팅_키워드1;
    protected String 말랑채팅_키워드2;
    protected String 말랑채팅_키워드3;
    protected String 허브채팅_키워드1;
    protected String 허브채팅_키워드2;
    protected String 허브채팅_키워드3;

    @BeforeEach
    void setUp() {
        말랑 = memberRepository.save(new Member("말랑_좋아요", Course.BACKEND, 0L));
        허브 = memberRepository.save(new Member("허브_좋아요", Course.FRONTEND, 0L));
        박스터 = memberRepository.save(new Member("박스터", Course.ANDROID, 0L));
        말랑_채팅 = chatRepository.save(ChatFixture.defaultChat(말랑.getId()));
        허브_채팅 = chatRepository.save(ChatFixture.defaultChat(허브.getId()));
        박스터_채팅 = chatRepository.save(ChatFixture.defaultChat(박스터.getId()));

        말랑_말랑채팅_좋아요 = chatLikeRepository.save(new ChatLike(말랑.getId(), 말랑_채팅.getId(), 0L));
        허브_말랑채팅_좋아요 = chatLikeRepository.save(new ChatLike(허브.getId(), 말랑_채팅.getId(), 0L));
        박스터_말랑채팅_좋아요 = chatLikeRepository.save(new ChatLike(박스터.getId(), 말랑_채팅.getId(), 0L));
        말랑_채팅.increaseLike();
        말랑_채팅.increaseLike();
        말랑_채팅.increaseLike();

        말랑_허브채팅_좋아요 = chatLikeRepository.save(new ChatLike(말랑.getId(), 허브_채팅.getId(), 0L));
        허브_허브채팅_좋아요 = chatLikeRepository.save(new ChatLike(허브.getId(), 허브_채팅.getId(), 0L));
        박스터_허브채팅_좋아요 = chatLikeRepository.save(new ChatLike(박스터.getId(), 허브_채팅.getId(), 0L));
        허브_채팅.increaseLike();
        허브_채팅.increaseLike();
        허브_채팅.increaseLike();

        말랑_박스터채팅_좋아요 = chatLikeRepository.save(new ChatLike(말랑.getId(), 박스터_채팅.getId(), 0L));
        허브_박스터채팅_좋아요 = chatLikeRepository.save(new ChatLike(허브.getId(), 박스터_채팅.getId(), 0L));
        박스터_박스터채팅_좋아요 = chatLikeRepository.save(new ChatLike(박스터.getId(), 박스터_채팅.getId(), 0L));
        박스터_채팅.increaseLike();
        박스터_채팅.increaseLike();
        박스터_채팅.increaseLike();

        말랑채팅_키워드1 = keywordRepository.save(new Keyword("말랑1", 말랑_채팅)).keyword();
        말랑채팅_키워드2 = keywordRepository.save(new Keyword("말랑2", 말랑_채팅)).keyword();
        말랑채팅_키워드3 = keywordRepository.save(new Keyword("말라3", 말랑_채팅)).keyword();
        허브채팅_키워드1 = keywordRepository.save(new Keyword("허브1", 허브_채팅)).keyword();
        허브채팅_키워드2 = keywordRepository.save(new Keyword("허브2", 허브_채팅)).keyword();
        허브채팅_키워드3 = keywordRepository.save(new Keyword("허브3", 허브_채팅)).keyword();

        em.flush();
        em.clear();
    }
}
