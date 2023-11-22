package chat.teco.tecochat.common.annotation;

import chat.teco.tecochat.config.JpaConfig;
import chat.teco.tecochat.config.QueryDslConfig;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({JpaConfig.class, QueryDslConfig.class})
@DataJpaTest(includeFilters = {
        @Filter(type = FilterType.ANNOTATION, classes = {Repository.class})
})
public @interface JpaRepositoryTest {
}
