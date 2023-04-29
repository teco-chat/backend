package chat.woowa.woowachat.chat.application;

import chat.woowa.woowachat.chat.domain.Chat;
import chat.woowa.woowachat.chat.domain.ChatRepository;
import chat.woowa.woowachat.chat.domain.GptClient;
import chat.woowa.woowachat.chat.domain.GptModel;
import chat.woowa.woowachat.chat.domain.Question;
import chat.woowa.woowachat.chat.domain.QuestionAndAnswer;
import chat.woowa.woowachat.chat.domain.SettingMessage;
import chat.woowa.woowachat.chat.dto.AskCommand;
import chat.woowa.woowachat.chat.dto.MessageDto;
import chat.woowa.woowachat.member.domain.Member;
import chat.woowa.woowachat.member.domain.MemberRepository;
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

    public MessageDto createAsk(final AskCommand command) {
        final Question question = command.question();
        final Chat chat = saveInitialChat(command, question);
        return answer(chat, question);
    }

    private Chat saveInitialChat(final AskCommand command, final Question question) {
        final Member member = findMemberById(command.memberId());
        Chat chat = new Chat(GptModel.GPT_3_5_TURBO,
                SettingMessage.byCourse(member.course()),
                question.content(),
                command.memberId()
        );
        return chatRepository.save(chat);
    }

    public MessageDto ask(final Long id, final AskCommand command) {
        final Chat chat = chatRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("아이디가 %d인 채팅이 없습니다."));
        return answer(chat, command.question());
    }

    private Member findMemberById(final Long memberId) {
        // TODO 예외처리 변경
        return memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new IllegalArgumentException("일치하는 회원 ID가 없습니다. (TODO: 변경)")
                );
    }

    private MessageDto answer(final Chat chat, final Question question) {
        final QuestionAndAnswer ask = gptClient.ask(chat, question);
        chat.addQuestionAndAnswer(ask);
        return new MessageDto(chat.id(), ask.answer().content());
    }
}
