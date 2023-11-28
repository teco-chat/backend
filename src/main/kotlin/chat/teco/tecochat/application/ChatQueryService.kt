package chat.teco.tecochat.application

import chat.teco.tecochat.chat.query.mapper.ChatMapper
import chat.teco.tecochat.domain.chat.Chat
import chat.teco.tecochat.domain.chat.ChatRepository
import chat.teco.tecochat.domain.chatlike.ChatLikeRepository
import chat.teco.tecochat.domain.keyword.KeywordRepository
import chat.teco.tecochat.domain.member.MemberRepository
import chat.teco.tecochat.query.ChatQueryRepository
import chat.teco.tecochat.query.ChatSearchCond
import chat.teco.tecochat.query.QueryChatByIdResponse
import chat.teco.tecochat.query.SearchChatResponse
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
        return ChatMapper.mapToQueryResponse(
            chat,
            member,
            isAlreadyClickLike,
            keywords
        )
    }

    fun search(cond: ChatSearchCond, pageable: Pageable): Page<SearchChatResponse> {
        val result = chatQueryRepository.search(cond, pageable)
        val chatIds = result.map(BaseEntity::id).toList()
        val chatIdKeywordMapping = keywordRepository.findAllInChatIds(chatIds)
            .groupBy { it.chat.id }
        return result.map { chat: Chat ->
            ChatMapper.mapToSearchQueryResponse(
                chat,
                memberRepository.getById(chat.memberId),
                chatIdKeywordMapping[chat.id] ?: emptyList()
            )
        }
    }
}
