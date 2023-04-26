package chat.woowa.woowachat.member.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByName(final String name);

    Optional<Member> findByName(final String name);
}
