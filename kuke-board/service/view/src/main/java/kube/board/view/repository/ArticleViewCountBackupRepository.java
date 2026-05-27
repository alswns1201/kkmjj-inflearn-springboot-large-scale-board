package kube.board.view.repository;

import kube.board.view.entity.ArticleViewCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleViewCountBackupRepository extends JpaRepository<ArticleViewCount,Long> {
    @Modifying
    @Query(value = "update article_view_count set view_count = :viewCount" +
            " where article_id = :articleId and view_count < :viewCount")
    int updateViewCount(@Param("articleId") Long articleId, @Param("viewCount") Long viewCount);

}
