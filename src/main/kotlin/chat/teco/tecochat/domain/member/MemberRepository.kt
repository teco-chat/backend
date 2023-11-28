package chat.teco.tecochat.domain.member

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull

fun MemberRepository.getByIdOrThrow(id: Long) = findByIdOrNull(id)
    ?: throw NoSuchElementException("사용자가 존재하지 않습니다. id: $id")

interface MemberRepository : JpaRepository<Member, Long> {
    fun findByName(name: String): Member?
}
