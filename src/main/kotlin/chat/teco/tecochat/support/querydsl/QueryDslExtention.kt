package chat.teco.tecochat.support.querydsl

import com.querydsl.core.types.Predicate
import com.querydsl.jpa.JPQLQuery

fun <C, T> JPQLQuery<C>.whereNotEmpty(
    param: T?,
    predicate: (T) -> Predicate?,
): JPQLQuery<C> {
    param.notEmpty {
        this.where(predicate(it))
    }
    return this
}

inline fun <T> T?.notEmpty(
    block: (T) -> Unit,
) {
    if (this == null || (this is String && this.trim() === "")) return
    block(this)
}
