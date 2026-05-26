package kube.board.like.service.response;


import kube.board.like.entity.ArticleLike;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class ArticleLikeResponse {
    private Long articleLikedId;
    private Long articleId;
    private Long userId;
    private LocalDateTime localDateTime;

    public static ArticleLikeResponse from(ArticleLike articleLike){
       ArticleLikeResponse articleLikeResponse =  new ArticleLikeResponse();
       articleLikeResponse.articleId = articleLike.getArticleId();
       articleLikeResponse.articleLikedId = articleLike.getArticleLikedId();
       articleLikeResponse.userId =articleLike.getUserId();
       return articleLikeResponse;
    }
}
