package kube.board.view.service;


import jakarta.transaction.Transactional;
import kube.board.view.entity.ArticleViewCount;
import kube.board.view.repository.ArticleViewCountBackupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleViewCountBackupProcessor {
    private final ArticleViewCountBackupRepository articleViewCountBackupRepository;

    @Transactional
    public void backup(Long articleId,Long viewCount){
        int result = articleViewCountBackupRepository.updateViewCount(articleId, viewCount);
        if(result == 0){
            articleViewCountBackupRepository.findById(articleId)
                    .ifPresentOrElse(ignored ->{},
                        ()-> articleViewCountBackupRepository.save(ArticleViewCount.init(articleId,viewCount))
                    );
        }
    }
}
