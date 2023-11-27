package chat.teco.tecochat.support.querydsl

import com.querydsl.core.types.NullExpression
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
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

fun <C, T> JPQLQuery<C>.orderByNotEmpty(
    param: T?,
    orderSpecifier: (T) -> OrderSpecifier<*>,
): JPQLQuery<C> {
    param.notEmpty {
        this.orderBy(orderSpecifier(it))
    }
    return this.orderBy(OrderByNull())
}

class OrderByNull : OrderSpecifier<Comparable<*>?>(
    Order.ASC,
    NullExpression.DEFAULT as NullExpression<Comparable<*>>,
    NullHandling.Default
)
