package kube.board.view.service;


import kube.board.view.repository.ArticleViewCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleViewService {
    private final ArticleViewCountRepository articleViewCountRepository;
    private final ArticleViewCountBackupProcessor articleViewCountBackupProcessor;
    private static final int BACK_UP_BACH_SIZE = 100;

    public Long increase(Long articleId,Long userId){
        Long count =  articleViewCountRepository.increase(articleId);
        if(count == BACK_UP_BACH_SIZE){
            articleViewCountBackupProcessor.backup(articleId,count);
        }
        return count;
    }

    public Long count(Long articleId){
        return articleViewCountRepository.read(articleId);
    }
}
