package chat.teco.tecochat.domain.comment

import chat.teco.tecochat.common.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity

@Entity
class Comment(
    @Column(nullable = false)
    val chatId: Long,

    @Column(nullable = false)
    val memberId: Long,

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    var content: String,

    id: Long = 0L,
) : BaseEntity(id) {

    fun update(memberId: Long, content: String) {
        check(this.memberId == memberId) { "댓글을 수정할 수 없습니다." }
        this.content = content
    }

    fun validateDelete(memberId: Long) {
        check(this.memberId == memberId) { "댓글을 삭제할 수 없습니다." }
    }
}

