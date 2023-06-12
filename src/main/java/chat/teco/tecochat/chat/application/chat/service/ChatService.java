package chat.teco.tecochat.chat.application.chat.service;

import chat.teco.tecochat.chat.application.chat.usecase.AskUseCase;
import chat.teco.tecochat.chat.application.chat.usecase.CopyChatUseCase;
import chat.teco.tecochat.chat.application.chat.usecase.CreateChatUseCase;
import chat.teco.tecochat.chat.application.chat.usecase.UpdateChatTitleUseCase;
import chat.teco.tecochat.chat.domain.chat.Chat;
import chat.teco.tecochat.chat.domain.chat.ChatRepository;
import chat.teco.tecochat.chat.domain.chat.GptClient;
import chat.teco.tecochat.chat.domain.chat.GptModel;
import chat.teco.tecochat.chat.domain.chat.Question;
import chat.teco.tecochat.chat.domain.chat.QuestionAndAnswer;
import chat.teco.tecochat.chat.domain.chat.SettingMessage;
import chat.teco.tecochat.chat.domain.chat.event.ChatCopiedEvent;
import chat.teco.tecochat.chat.domain.chat.event.ChatCreatedEvent;
import chat.teco.tecochat.member.domain.Member;
import chat.teco.tecochat.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@RequiredArgsConstructor
@Service
public class ChatService implements
        CreateChatUseCase,
        AskUseCase,
        UpdateChatTitleUseCase,
        CopyChatUseCase {

    private final MemberRepository memberRepository;
    private final ChatRepository chatRepository;
    private final GptClient gptClient;
    private final TransactionTemplate transactionTemplate;
    private final ApplicationEventPublisher publisher;

    @Override
    public CreateChatResult createChat(CreateChatCommand command) {
        Question question = command.question();
        Member member = memberRepository.getById(command.memberId());
        Chat chat = new Chat(GptModel.GPT_3_5_TURBO,
                SettingMessage.byCourse(member.course()),
                question.content(),
                command.memberId()
        );

        QuestionAndAnswer qna = gptClient.ask(chat, question);

        return transactionTemplate.execute(status -> {
            chatRepository.save(chat);
            chat.addQuestionAndAnswer(qna);
            publisher.publishEvent(ChatCreatedEvent.from(chat));
            return new CreateChatResult(chat.id(), qna.answer().content());
        });
    }

    @Override
    public AskResult ask(Long id, AskCommand command) {
        Chat chat = chatRepository.getWithQuestionAndAnswersById(id);
        QuestionAndAnswer qna = gptClient.ask(chat, command.question());
        return transactionTemplate.execute(status -> {
            chatRepository.getWithQuestionAndAnswersById(id).addQuestionAndAnswer(qna);
            return new AskResult(qna.answer().content());
        });
    }

    @Transactional
    @Override
    public void updateTitle(UpdateChatTitleCommand command) {
        Chat chat = chatRepository.getById(command.chatId());
        chat.updateTitle(command.memberId(), command.title());
    }

    @Transactional
    @Override
    public Long copy(CopyCommand command) {
        Chat chat = chatRepository.getById(command.chatId());
        Chat copied = chat.copy(command.memberId());
        Long copiedChatId = chatRepository.save(copied).id();
        publisher.publishEvent(new ChatCopiedEvent(chat.id(), copiedChatId));
        return copiedChatId;
    }
}
