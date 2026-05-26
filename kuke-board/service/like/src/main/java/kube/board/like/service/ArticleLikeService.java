package kube.board.like.service;


import jakarta.transaction.Transactional;
import kube.board.like.entity.ArticleLike;
import kube.board.like.entity.ArticleLikeCount;
import kube.board.like.repository.ArticleLikeCountRepository;
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

    private final ArticleLikeCountRepository articleLikeCountRepository;


    public ArticleLikeResponse read(Long articleId, Long userId){
        return articleLikeRepository.findByArticleIdAndUserId(articleId,userId)
                .map(ArticleLikeResponse::from).orElseThrow();
    }

    /**
     *  update 구문
     * @param articleId
     * @param userId
     */
    @Transactional
    public void likePessimisticLock1(Long articleId,Long userId){
        articleLikeRepository.save(
                ArticleLike.create(snowflake.nextId(),articleId,userId)
        );

        int result = articleLikeCountRepository.increase(articleId);
        if(result == 0 ){
            // 최초 요청시에는 UPDATE 되는 레코드가 없어서 1로
            // 게시글 등록시 0으로 초기화 하는 방법도 있다.
            articleLikeCountRepository.save(
                    ArticleLikeCount.init(articleId,1L)
            );
        }

    }

    @Transactional
    public void unlikePessimisticLock1(Long articleId, Long userId){
        articleLikeRepository.findByArticleIdAndUserId(articleId,userId)
                .ifPresent(articleLike ->{
                    articleLikeRepository.delete(articleLike);
                    articleLikeCountRepository.decrease(articleId);
                });
    }

    /**
     *  select ... fro update + update
     * @param articleId
     * @param userId
     */
    @Transactional
    public void likePessimisticLock2(Long articleId,Long userId){
        articleLikeRepository.save(
                ArticleLike.create(snowflake.nextId(),articleId,userId)
        );

       ArticleLikeCount articleLikeCount = articleLikeCountRepository.findLockedByArticleId(articleId)
                .orElseGet(()->ArticleLikeCount.init(articleId,0L));

       articleLikeCount.increase();
       articleLikeCountRepository.save(articleLikeCount);

    }

    @Transactional
    public void unlikePessimisticLock2(Long articleId, Long userId){
        articleLikeRepository.findByArticleIdAndUserId(articleId,userId)
                .ifPresent(articleLike ->{
                    articleLikeRepository.delete(articleLike);
                   ArticleLikeCount articleLikeCount =
                           articleLikeCountRepository.findLockedByArticleId(articleId).orElseThrow();
                   articleLikeCount.decrease();


                });
    }

    /**
     *  OptimisticLock 낙관적 락
     * @param articleId
     * @param userId
     */
    @Transactional
    public void likeOptimisticLock(Long articleId,Long userId){
        articleLikeRepository.save(
                ArticleLike.create(snowflake.nextId(),articleId,userId)
        );

        ArticleLikeCount articleLikeCount = articleLikeCountRepository.findById(articleId).orElseGet(()->ArticleLikeCount.init(articleId,0L));
        articleLikeCount.increase();
        articleLikeCountRepository.save(articleLikeCount);
    }

    @Transactional
    public void unlikeOptimisticLock(Long articleId, Long userId){
        articleLikeRepository.findByArticleIdAndUserId(articleId,userId).ifPresent(
                articleLike -> {
                    articleLikeRepository.delete(articleLike);
                   ArticleLikeCount articleLikeCount =  articleLikeCountRepository.findById(articleId).orElseThrow();
                   articleLikeCount.decrease();
                });
    }

    public Long count(Long articleId){
        return articleLikeCountRepository.findById(articleId)
                .map(ArticleLikeCount::getLikeCount)
                .orElse(0L);

    }


}
