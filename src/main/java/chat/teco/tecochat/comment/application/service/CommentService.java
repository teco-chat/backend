package chat.teco.tecochat.comment.application.service;

import static chat.teco.tecochat.chat.exception.chat.ChatExceptionType.NOT_FOUND_CHAT;
import static chat.teco.tecochat.comment.execption.CommentExceptionType.NOT_FOUND_COMMENT;
import static chat.teco.tecochat.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;

import chat.teco.tecochat.chat.domain.chat.ChatRepository;
import chat.teco.tecochat.chat.exception.chat.ChatException;
import chat.teco.tecochat.comment.application.usecase.DeleteCommentUseCase;
import chat.teco.tecochat.comment.application.usecase.UpdateCommentUseCase;
import chat.teco.tecochat.comment.application.usecase.WriteCommentUseCase;
import chat.teco.tecochat.comment.domain.Comment;
import chat.teco.tecochat.comment.domain.CommentRepository;
import chat.teco.tecochat.comment.execption.CommentException;
import chat.teco.tecochat.member.domain.MemberRepository;
import chat.teco.tecochat.member.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class CommentService implements WriteCommentUseCase, UpdateCommentUseCase, DeleteCommentUseCase {

    private final MemberRepository memberRepository;
    private final ChatRepository chatRepository;
    private final CommentRepository commentRepository;

    @Override
    public Long write(WriteCommentCommand command) {
        validateMemberExist(command.memberId());
        validateChatExist(command.chatId());
        Comment comment = command.toDomain();
        return commentRepository.save(comment).id();
    }

    private void validateMemberExist(Long memberId) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
    }

    private void validateChatExist(Long chatId) {
        chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatException(NOT_FOUND_CHAT));
    }

    @Override
    public void update(UpdateCommentCommand command) {
        Comment comment = findCommentById(command.commentId());
        comment.update(command.memberId(), command.content());
    }

    private Comment findCommentById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new CommentException(NOT_FOUND_COMMENT));
    }

    @Override
    public void delete(DeleteCommentCommand command) {
        Comment comment = findCommentById(command.commentId());
        comment.validateDelete(command.memberId());
        commentRepository.delete(comment);
    }
}
