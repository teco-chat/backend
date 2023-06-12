package chat.teco.tecochat.common.presentation;

import java.util.List;
import org.springframework.data.domain.Page;

public record PageResponse<T>(
        List<T> content,
        boolean last,
        boolean first,
        boolean empty,
        int totalPages,
        int currentPages,
        long totalElements,
        int numberOfElements
) {

    public static <T> PageResponse<T> from(Page<T> response) {
        return new PageResponse<T>(
                response.getContent(),
                response.isLast(),
                response.isFirst(),
                response.isEmpty(),
                response.getTotalPages(),
                response.getNumber(),
                response.getTotalElements(),
                response.getNumberOfElements());
    }
}
