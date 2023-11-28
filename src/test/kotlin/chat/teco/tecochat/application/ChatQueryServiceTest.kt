package chat.teco.tecochat.application

import chat.teco.tecochat.createChat
import chat.teco.tecochat.createChatLike
import chat.teco.tecochat.createMember
import chat.teco.tecochat.createQuestionAndAnswer
import chat.teco.tecochat.domain.chat.ChatRepository
import chat.teco.tecochat.domain.chat.getByIdOrThrow
import chat.teco.tecochat.domain.chatlike.ChatLikeRepository
import chat.teco.tecochat.domain.keyword.KeywordRepository
import chat.teco.tecochat.domain.member.Course
import chat.teco.tecochat.domain.member.MemberRepository
import chat.teco.tecochat.query.ChatQueryRepository
import chat.teco.tecochat.query.ChatSearchCond
import chat.teco.tecochat.query.SearchChatResponse
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.extracting
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.support.PageableExecutionUtils

class ChatQueryServiceTest : StringSpec({
    val memberRepository = mockk<MemberRepository>()
    val chatRepository = mockk<ChatRepository>()
    val chatQueryRepository = mockk<ChatQueryRepository>()
    val chatLikeRepository = mockk<ChatLikeRepository>()
    val keywordRepository = mockk<KeywordRepository>()

    val chatQueryService = ChatQueryService(
        memberRepository,
        chatRepository,
        chatQueryRepository,
        chatLikeRepository,
        keywordRepository
    )

    "채팅을 단일 조회한다" {
        val member = createMember(name = "herb", id = 1L)
        val chat = createChat(
            memberId = member.id,
            questionAndAnswers = listOf(
                createQuestionAndAnswer("질문1", "답변1"),
                createQuestionAndAnswer("질문2", "답변2"),
                createQuestionAndAnswer("질문3", "답변3")
            )
        )
        every { chatRepository.getByIdOrThrow(any()) } returns chat
        every { memberRepository.getById(any()) } returns member
        every { chatLikeRepository.findByMemberIdAndChatId(any(), any()) } returns createChatLike()
        every { keywordRepository.findAllByChatId(any()) } returns emptyList()

        val result = chatQueryService.findById(1L, 1L)

        assertSoftly(result) {
            it.crewName shouldBe "herb"
            it.course shouldBe Course.BACKEND
            it.likeCount shouldBe 0
            it.keywords shouldBe emptyList()
            it.title shouldBe chat.title
            it.isAlreadyClickLike shouldBe true
            extracting(it.messages) { content }
                .shouldContainExactly(
                    "질문1", "답변1", "질문2", "답변2", "질문3", "답변3"
                )
        }
    }

    "채팅을 검색한다" {
        val mallang = createMember(name = "mallang", id = 1L)
        val herb = createMember(name = "herb", id = 2L)
        val mallangChat = createChat(
            memberId = mallang.id,
            questionAndAnswers = listOf(createQuestionAndAnswer(), createQuestionAndAnswer())
        )
        val herbChat = createChat(
            memberId = herb.id,
            questionAndAnswers = listOf(createQuestionAndAnswer())
        )
        every { chatQueryRepository.search(any(), any()) } returns PageableExecutionUtils.getPage(
            listOf(mallangChat, herbChat), PageRequest.of(0, 10)
        ) { 0 }
        every { keywordRepository.findAllInChatIds(any()) } returns emptyList()
        every { memberRepository.findAllById(any()) } returns listOf(mallang, herb)

        val result: Page<SearchChatResponse> = chatQueryService.search(
            ChatSearchCond(null, null, null, null),
            PageRequest.of(1, 1)
        )

        assertSoftly(result.content[0]) {
            it.crewName shouldBe "mallang"
            it.course shouldBe Course.BACKEND
            it.commentCount shouldBe 0
            it.likeCount shouldBe 0
            it.keywords shouldBe emptyList()
            it.title shouldBe mallangChat.title
            it.totalQnaCount shouldBe 2
        }
        assertSoftly(result.content[1]) {
            it.crewName shouldBe "herb"
            it.course shouldBe Course.BACKEND
            it.commentCount shouldBe 0
            it.likeCount shouldBe 0
            it.keywords shouldBe emptyList()
            it.title shouldBe herbChat.title
            it.totalQnaCount shouldBe 1
        }
    }
})
