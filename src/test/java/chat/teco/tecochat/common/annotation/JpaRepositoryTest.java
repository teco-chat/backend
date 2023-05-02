package chat.teco.tecochat.common.annotation;

import chat.teco.tecochat.common.config.JpaConfig;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({JpaConfig.class})
@DataJpaTest
public @interface JpaRepositoryTest {
}
