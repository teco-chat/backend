package chat.teco.tecochat.auth.presentation.argumentresolver;

import static chat.teco.tecochat.auth.exception.AuthenticationExceptionType.NO_NAME_HEADER;

import chat.teco.tecochat.auth.Auth;
import chat.teco.tecochat.auth.domain.Authenticator;
import chat.teco.tecochat.auth.exception.AuthenticationException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
@Component
public class AuthArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String AUTH_HEADER_NAME = "name";

    private final Authenticator authenticator;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Auth.class);
    }

    @Override
    public Long resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        return authenticator.authenticateWithBase64(extractName(webRequest))
                .id();
    }

    private String extractName(WebRequest webRequest) {
        String name = webRequest.getHeader(AUTH_HEADER_NAME);
        if (name == null) {
            throw new AuthenticationException(NO_NAME_HEADER);
        }
        return name;
    }
}
