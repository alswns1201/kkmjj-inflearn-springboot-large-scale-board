package kube.board.comment.service;

import kube.board.comment.entity.Comment;
import kube.board.comment.repository.CommentRepository;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.lang.model.util.Elements;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @InjectMocks
    CommentService commentService;
    @Mock
    CommentRepository commentRepository;

    @Test
    @DisplayName("삭제할 댓글이 자식이 있으면 삭제 표시만")
    void deleteShouldName(){
        Long articleId =1L;
        Long commentId  =2L;
        Comment comment =  createComment(articleId,commentId);
        given(commentRepository.findById(commentId))
                .willReturn(Optional.of(comment));
        given(commentRepository.countBy(articleId,commentId,2L))
                .willReturn(2L);
        //when
        commentService.delete(commentId);
        //then
        verify(comment).delete();
    }

    @Test
    @DisplayName("하위 댓글만 삭제")
    void deleteShouldDeleteOnlyChildren(){
        Long articleId =1L;
        Long commentId  =2L;
        Long parentCommentId = 1L;
        // 부모가 아닌 커멘트
        Comment comment =  createComment(articleId,commentId,parentCommentId);
        given(comment.isRoot()).willReturn(false);
        // 삭제 되지 않은 부모 커멘트
        Comment parentComment  = mock(Comment.class);
        given(parentComment.getDeleted()).willReturn(false);

        given(commentRepository.findById(commentId))
                .willReturn(Optional.of(comment));
        given(commentRepository.countBy(articleId,commentId,2L))
                .willReturn(1L);
        given(commentRepository.findById(parentCommentId))
                .willReturn(Optional.of(parentComment));
        //when
        commentService.delete(commentId);
        //then
        verify(commentRepository).delete(comment);
        verify(commentRepository,never()).delete(parentComment);
    }

    @Test
    @DisplayName("하위 댓글이 삭제되고, 삭제된 부모면 둘다 삭제한다.")
    void deleteShouldDeleteAll(){
        Long articleId =1L;
        Long commentId  =2L;
        Long parentCommentId = 1L;
        // 부모가 아닌 커멘트
        Comment comment =  createComment(articleId,commentId,parentCommentId);
        given(comment.isRoot()).willReturn(false);
        // 삭제된 커멘트 부모.
        Comment parentComment =  createComment(articleId,parentCommentId);
        given(parentComment.isRoot()).willReturn(true);
        given(parentComment.getDeleted()).willReturn(true);

        given(commentRepository.findById(commentId))
                .willReturn(Optional.of(comment));
        given(commentRepository.countBy(articleId,commentId,2L))
                .willReturn(1L);

        given(commentRepository.findById(parentCommentId))
                .willReturn(Optional.of(parentComment));
        given(commentRepository.countBy(articleId,parentCommentId,2L))
                .willReturn(1L);
        //when
        commentService.delete(commentId);
        //then
        verify(commentRepository).delete(comment);
        verify(commentRepository).delete(parentComment);
    }


    //given 테스트 를 위한  예상 데이터 넣기
    private Comment createComment(Long articleId, Long commentId){
        Comment comment = mock(Comment.class);
        given(comment.getArticleId()).willReturn(articleId);
        given(comment.getCommentId()).willReturn(commentId);
        return comment;
    }

    private Comment createComment(Long articleId, Long commentId,
                                  Long parentCommentId){
        Comment comment =createComment(articleId,commentId);
        given(comment.getParentCommentId()).willReturn(parentCommentId);
        return comment;
    }



}