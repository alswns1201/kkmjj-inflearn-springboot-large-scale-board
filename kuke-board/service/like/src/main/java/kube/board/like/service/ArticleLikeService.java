package kube.board.like.service;


import jakarta.transaction.Transactional;
import kube.board.like.entity.ArticleLike;
import kube.board.like.repository.ArticleLikeRepository;
import kube.board.like.service.response.ArticleLikeResponse;
import kuke.board.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleLikeService {
    private final Snowflake snowflake = new Snowflake();
    private final ArticleLikeRepository articleLikeRepository;

    public ArticleLikeResponse read(Long articleId, Long userId){
        return articleLikeRepository.findByArticleIdAndUserId(articleId,userId)
                .map(ArticleLikeResponse::from).orElseThrow();
    }

    @Transactional
    public void like(Long articleId,Long userId){
        articleLikeRepository.save(
                ArticleLike.create(snowflake.nextId(),articleId,userId)
        );

    }

    @Transactional
    public void unlike(Long articleId, Long userId){
        articleLikeRepository.findByArticleIdAndUserId(articleId,userId)
                .ifPresent(articleLikeRepository::delete);
    }


}
