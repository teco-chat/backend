package chat.teco.tecochat.member.domain;

import static chat.teco.tecochat.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;

import chat.teco.tecochat.member.exception.MemberException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByName(String name);

    Optional<Member> findByName(String name);

    default Member getByName(String name) {
        return findByName(name)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
    }
}
