package chat.teco.tecochat.chat.application;

import static chat.teco.tecochat.chat.exception.ChatExceptionType.NOT_FOUND_CHAT;
import static chat.teco.tecochat.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;

import chat.teco.tecochat.chat.domain.Chat;
import chat.teco.tecochat.chat.domain.ChatRepository;
import chat.teco.tecochat.chat.domain.GptClient;
import chat.teco.tecochat.chat.domain.GptModel;
import chat.teco.tecochat.chat.domain.Question;
import chat.teco.tecochat.chat.domain.QuestionAndAnswer;
import chat.teco.tecochat.chat.domain.SettingMessage;
import chat.teco.tecochat.chat.domain.event.ChatCreatedEvent;
import chat.teco.tecochat.chat.dto.AskCommand;
import chat.teco.tecochat.chat.dto.MessageDto;
import chat.teco.tecochat.chat.exception.ChatException;
import chat.teco.tecochat.member.domain.Member;
import chat.teco.tecochat.member.domain.MemberRepository;
import chat.teco.tecochat.member.exception.MemberException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class AskChatService {

    private final MemberRepository memberRepository;
    private final ChatRepository chatRepository;
    private final GptClient gptClient;
    private final TransactionTemplate transactionTemplate;
    private final ApplicationEventPublisher publisher;

    public AskChatService(final MemberRepository memberRepository,
                          final ChatRepository chatRepository,
                          final GptClient gptClient,
                          final TransactionTemplate transactionTemplate,
                          final ApplicationEventPublisher publisher) {
        this.memberRepository = memberRepository;
        this.chatRepository = chatRepository;
        this.gptClient = gptClient;
        this.transactionTemplate = transactionTemplate;
        this.publisher = publisher;
    }

    public MessageDto createAsk(final AskCommand command) {
        final Question question = command.question();
        final Member member = findMemberById(command.memberId());
        final Chat chat = new Chat(GptModel.GPT_3_5_TURBO,
                SettingMessage.byCourse(member.course()),
                question.content(),
                command.memberId()
        );

        final QuestionAndAnswer qna = gptClient.ask(chat, question);

        return transactionTemplate.execute(status -> {
            chatRepository.save(chat);
            chat.addQuestionAndAnswer(qna);
            publisher.publishEvent(ChatCreatedEvent.from(chat));
            return new MessageDto(chat.id(), qna.answer().content());
        });
    }

    public MessageDto ask(final Long id, final AskCommand command) {
        // TODO 자신의 채팅인경우, 아닌 경우 나눠서 처리해야 함
        final Chat chat = findChatWithQuestionAndAnswersById(id);

        final QuestionAndAnswer qna = gptClient.ask(chat, command.question());

        return transactionTemplate.execute(status -> {
            findChatWithQuestionAndAnswersById(id).addQuestionAndAnswer(qna);
            return new MessageDto(chat.id(), qna.answer().content());
        });
    }

    private Member findMemberById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new MemberException(NOT_FOUND_MEMBER));
    }

    private Chat findChatWithQuestionAndAnswersById(final Long id) {
        return chatRepository.findWithQuestionAndAnswersById(id)
                .orElseThrow(() -> new ChatException(NOT_FOUND_CHAT));
    }
}
