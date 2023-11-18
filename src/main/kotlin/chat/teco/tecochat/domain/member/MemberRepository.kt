package chat.teco.tecochat.domain.member

import org.springframework.data.jpa.repository.JpaRepository

// TODO
//fun MemberRepository.getById(id: Long) = findByIdOrNull(id)
//    ?: throw NoSuchElementException("사용자가 존재하지 않습니다. id: $id")
//
//fun MemberRepository.getByName(name: String) = findByName(name)
//    ?: throw NoSuchElementException("사용자가 존재하지 않습니다. name: $name")

interface MemberRepository : JpaRepository<Member, Long> {
    fun findByName(name: String): Member?

    override fun getById(id: Long): Member {
        return findById(id)
            .orElseThrow { NoSuchElementException("사용자가 존재하지 않습니다.") }
    }

    fun getByName(name: String): Member? {
        return findByName(name) ?: throw NoSuchElementException("사용자가 존재하지 않습니다.")
    }
}

