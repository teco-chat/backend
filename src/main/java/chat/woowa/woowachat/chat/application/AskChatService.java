package chat.woowa.woowachat.chat.application;

import chat.woowa.woowachat.chat.domain.Chat;
import chat.woowa.woowachat.chat.domain.ChatRepository;
import chat.woowa.woowachat.chat.domain.GptClient;
import chat.woowa.woowachat.chat.domain.GptModel;
import chat.woowa.woowachat.chat.domain.Message;
import chat.woowa.woowachat.chat.domain.SettingMessage;
import chat.woowa.woowachat.chat.dto.AskCommand;
import chat.woowa.woowachat.chat.dto.MessageDto;
import chat.woowa.woowachat.member.domain.Member;
import chat.woowa.woowachat.member.domain.MemberRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class AskChatService {

    private final MemberRepository memberRepository;
    private final ChatRepository chatRepository;
    private final GptClient gptClient;

    public AskChatService(final MemberRepository memberRepository,
                          final ChatRepository chatRepository,
                          final GptClient gptClient) {
        this.memberRepository = memberRepository;
        this.chatRepository = chatRepository;
        this.gptClient = gptClient;
    }

    public MessageDto ask(final AskCommand command) {
        final Message question = Message.user(command.message(), command.token());
        final List<Chat> chats = chatRepository.findAllByMemberId(command.memberId());

        if (isFirstChat(chats)) {
            final Chat chat = saveInitialChat(command, question);
            return answer(chat);
        }
        final Chat chat = chats.get(0);
        chat.addMessage(question);
        return answer(chat);
    }

    private boolean isFirstChat(final List<Chat> chats) {
        return chats.isEmpty();
    }

    private Chat saveInitialChat(final AskCommand command, final Message question) {
        final Member member = findMemberById(command.memberId());
        Chat chat = new Chat(GptModel.GPT_3_5_TURBO,
                SettingMessage.byCourse(member.course()),
                question.content(),
                command.memberId(),
                question
        );
        return chatRepository.save(chat);
    }

    private Member findMemberById(final Long memberId) {
        // TODO 예외처리 변경
        return memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new IllegalArgumentException("일치하는 회원 ID가 없습니다. (TODO: 변경)")
                );
    }

    private MessageDto answer(final Chat save) {
        final Message answer = gptClient.ask(save);
        save.addMessage(answer);
        return new MessageDto(answer.content());
    }
}
