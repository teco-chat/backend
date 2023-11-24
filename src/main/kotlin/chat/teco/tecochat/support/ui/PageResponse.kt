package chat.teco.tecochat.support.ui

import org.springframework.data.domain.Page

data class PageResponse<T>(
    val content: List<T>,
    val last: Boolean,
    val first: Boolean,
    val empty: Boolean,
    val totalPages: Int,
    val currentPages: Int,
    val totalElements: Long,
    val numberOfElements: Int,
) {

    companion object {
        @JvmStatic
        fun <T> from(response: Page<T>): PageResponse<T> {
            return PageResponse(
                response.content,
                response.isLast,
                response.isFirst,
                response.isEmpty,
                response.totalPages,
                response.number,
                response.totalElements,
                response.numberOfElements
            )
        }
    }
}

