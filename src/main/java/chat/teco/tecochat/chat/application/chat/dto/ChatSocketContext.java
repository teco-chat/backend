package chat.teco.tecochat.chat.application.chat.dto;

import chat.teco.tecochat.domain.member.Member;
import org.springframework.web.socket.WebSocketSession;

public class ChatSocketContext {

    private final WebSocketSession session;
    private final Member member;
    private final StringBuilder stringBuilder;
    private final Long chatId;
    private String question;

    public ChatSocketContext(WebSocketSession session, Member member, Long chatId) {
        this.session = session;
        this.member = member;
        this.chatId = chatId;
        this.stringBuilder = new StringBuilder();
    }

    public String answer() {
        return stringBuilder.toString();
    }

    public Member member() {
        return member;
    }

    public Long chatId() {
        return chatId;
    }

    public String question() {
        return question;
    }

    public WebSocketSession session() {
        return session;
    }

    public StringBuilder stringBuilder() {
        return stringBuilder;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
