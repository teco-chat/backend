package chat.teco.tecochat.application

import chat.teco.tecochat.domain.chat.Chat
import chat.teco.tecochat.domain.chat.ChatRepository
import chat.teco.tecochat.domain.chatlike.ChatLikeRepository
import chat.teco.tecochat.domain.keyword.KeywordRepository
import chat.teco.tecochat.domain.member.MemberRepository
import chat.teco.tecochat.query.ChatQueryRepository
import chat.teco.tecochat.query.ChatSearchCond
import chat.teco.tecochat.query.QueryChatByIdResponse
import chat.teco.tecochat.query.QueryKeywordDto
import chat.teco.tecochat.query.QueryMessageDto
import chat.teco.tecochat.query.SearchChatResponse
import chat.teco.tecochat.query.SearchKeywordDto
import chat.teco.tecochat.support.domain.BaseEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Transactional(readOnly = true)
@Service
class ChatQueryService(
    private val memberRepository: MemberRepository,
    private val chatRepository: ChatRepository,
    private val chatQueryRepository: ChatQueryRepository,
    private val chatLikeRepository: ChatLikeRepository,
    private val keywordRepository: KeywordRepository,
) {
    fun findById(id: Long, requesterMemberId: Long): QueryChatByIdResponse {
        val chat = chatRepository.getById(id)
        val member = memberRepository.getById(chat.memberId)
        val isAlreadyClickLike = chatLikeRepository.findByMemberIdAndChatId(requesterMemberId, id) != null
        val keywords = keywordRepository.findAllByChatId(id)
        val messages = chat.questionAndAnswers.questionAndAnswers.flatMap { qna ->
            listOf(
                QueryMessageDto(qna.question.content(), qna.question.roleName(), qna.createdAt),
                QueryMessageDto(qna.answer.content(), qna.answer.roleName(), qna.createdAt)
            )
        }
        return with(chat) {
            QueryChatByIdResponse(
                id,
                member.name,
                member.course,
                title,
                likeCount,
                isAlreadyClickLike,
                createdAt,
                messages,
                keywords.map { QueryKeywordDto(it.keyword) }
            )
        }
    }

    fun search(cond: ChatSearchCond, pageable: Pageable): Page<SearchChatResponse> {
        val result = chatQueryRepository.search(cond, pageable)
        val chatIds = result.map(BaseEntity::id).toList()
        val chatIdByKeywords = keywordRepository.findAllInChatIds(chatIds)
            .groupBy { it.chat.id }
        val memberIds = result.map(Chat::memberId).toList()
        val members = memberRepository.findAllById(memberIds)
            .associateBy { it.id }
        return result.map {
            with(it) {
                val member = members[it.memberId]!!
                val keywords = chatIdByKeywords[it.id] ?: emptyList()
                SearchChatResponse(
                    id,
                    member.id,
                    member.name,
                    member.course,
                    title,
                    likeCount,
                    commentCount,
                    questionAndAnswers.questionAndAnswers.size,
                    keywords.map { SearchKeywordDto(it.keyword) },
                    createdAt
                )
            }
        }
    }
}
