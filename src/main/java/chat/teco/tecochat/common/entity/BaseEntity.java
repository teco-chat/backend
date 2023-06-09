package chat.teco.tecochat.common.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;

    @CreatedDate
    private LocalDateTime createdAt;

    protected BaseEntity() {
        id = null;
    }

    public BaseEntity(Long id) {
        this.id = id;
    }

    public Long id() {
        return id;
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }
}
