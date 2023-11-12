package chat.teco.tecochat.comment.application;

import chat.teco.tecochat.chat.domain.chat.Chat;
import chat.teco.tecochat.chat.domain.chat.ChatRepository;
import chat.teco.tecochat.comment.application.dto.DeleteCommentCommand;
import chat.teco.tecochat.comment.application.dto.UpdateCommentCommand;
import chat.teco.tecochat.comment.application.dto.WriteCommentCommand;
import chat.teco.tecochat.comment.domain.Comment;
import chat.teco.tecochat.comment.domain.CommentRepository;
import chat.teco.tecochat.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class CommentService {

    private final MemberRepository memberRepository;
    private final ChatRepository chatRepository;
    private final CommentRepository commentRepository;

    public Long write(WriteCommentCommand command) {
        memberRepository.getById(command.memberId());
        Comment comment = command.toDomain();
        Chat chat = chatRepository.getById(command.chatId());
        chat.increaseComment();
        return commentRepository.save(comment).id();
    }

    public void update(UpdateCommentCommand command) {
        Comment comment = commentRepository.getById(command.commentId());
        comment.update(command.memberId(), command.content());
    }

    public void delete(DeleteCommentCommand command) {
        Comment comment = commentRepository.getById(command.commentId());
        comment.validateDelete(command.memberId());
        Chat chat = chatRepository.getById(comment.chatId());
        chat.decreaseComment();
        commentRepository.delete(comment);
    }
}
