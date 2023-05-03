package chat.teco.tecochat.member.presentation.argumentresolver;

import static chat.teco.tecochat.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;

import chat.teco.tecochat.member.domain.Member;
import chat.teco.tecochat.member.domain.MemberRepository;
import chat.teco.tecochat.member.exception.MemberException;
import java.util.Base64;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class AuthArgumentResolver implements HandlerMethodArgumentResolver {

    private final MemberRepository memberRepository;

    public AuthArgumentResolver(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Auth.class);
    }

    @Override
    public Long resolveArgument(final MethodParameter parameter, final ModelAndViewContainer mavContainer,
                                final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory) {
        final String name = decodeName(webRequest);
        final Member member = memberRepository.findByName(name)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
        return member.id();
    }

    private String decodeName(final NativeWebRequest webRequest) {
        final byte[] names = Base64.getDecoder()
                .decode(webRequest.getHeader("name"));
        return new String(names).intern();
    }
}
