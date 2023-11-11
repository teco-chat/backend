package chat.teco.tecochat.domain.member

import chat.teco.tecochat.member.domain.Member
import chat.teco.tecochat.member.exception.MemberException
import chat.teco.tecochat.member.exception.MemberExceptionType
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Long> {
    fun findByName(name: String): Member?

    override fun getById(id: Long): Member {
        return findById(id)
            .orElseThrow { MemberException(MemberExceptionType.NOT_FOUND_MEMBER) }
    }

    fun getByName(name: String): Member? {
        return findByName(name) ?: throw MemberException(MemberExceptionType.NOT_FOUND_MEMBER)
    }
}

