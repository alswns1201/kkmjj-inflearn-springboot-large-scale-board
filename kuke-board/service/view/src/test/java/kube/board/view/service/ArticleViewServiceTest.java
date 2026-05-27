package kube.board.view.service;

import kube.board.view.entity.ArticleViewCount;
import kube.board.view.repository.ArticleViewCountBackupRepository;
import kube.board.view.repository.ArticleViewCountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ArticleViewServiceTest {

    @Autowired
    ArticleViewService articleViewService;

    @Autowired
    ArticleViewCountRepository articleViewCountRepository;

    @Autowired
    ArticleViewCountBackupRepository articleViewCountBackupRepository;

    @Autowired
    StringRedisTemplate redisTemplate;

    static final Long ARTICLE_ID = 9999L;
    static final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        // 테스트 격리: 해당 articleId의 Redis 키 삭제
        redisTemplate.delete("view:article:%s:view_count".formatted(ARTICLE_ID));
    }

    @Test
    @DisplayName("최초 조회 시 count는 0")
    void countReturnZeroWhenNotExists() {
        Long count = articleViewService.count(ARTICLE_ID);
        assertThat(count).isEqualTo(0L);
    }

    @Test
    @DisplayName("increase 호출 시 count 1 증가")
    void increaseOnce() {
        Long result = articleViewService.increase(ARTICLE_ID, USER_ID);
        assertThat(result).isEqualTo(1L);
        assertThat(articleViewService.count(ARTICLE_ID)).isEqualTo(1L);
    }

    @Test
    @DisplayName("increase 반복 호출 시 count 누적")
    void increaseMultipleTimes() {
        int times = 5;
        for (int i = 0; i < times; i++) {
            articleViewService.increase(ARTICLE_ID, (long) i);
        }
        assertThat(articleViewService.count(ARTICLE_ID)).isEqualTo(times);
    }

    @Test
    @DisplayName("Redis increment 동시성 — 100 스레드 각 10회 = 1000")
    void increaseConcurrent() throws InterruptedException {
        int threadCount = 100;
        int callsPerThread = 10;
        int total = threadCount * callsPerThread;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(total);

        for (int i = 0; i < threadCount; i++) {
            long userId = i + 1;
            executor.submit(() -> {
                for (int j = 0; j < callsPerThread; j++) {
                    articleViewService.increase(ARTICLE_ID, userId);
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertThat(articleViewService.count(ARTICLE_ID)).isEqualTo(total);
    }

    @Test
    @DisplayName("ArticleViewCountRepository — Redis read/increase 직접 검증")
    void repositoryReadAndIncrease() {
        assertThat(articleViewCountRepository.read(ARTICLE_ID)).isEqualTo(0L);

        articleViewCountRepository.increase(ARTICLE_ID);
        articleViewCountRepository.increase(ARTICLE_ID);

        assertThat(articleViewCountRepository.read(ARTICLE_ID)).isEqualTo(2L);
    }

    @Test
    @DisplayName("ArticleViewCountBackupRepository — updateViewCount: viewCount 큰 값만 반영")
    void backupRepositoryUpdateViewCount() {
        ArticleViewCount init = ArticleViewCount.init(ARTICLE_ID, 0L);
        articleViewCountBackupRepository.save(init);

        // 처음 update: 100 > 0 → 반영
        int updated = articleViewCountBackupRepository.updateViewCount(ARTICLE_ID, 100L);
        assertThat(updated).isEqualTo(1);

        ArticleViewCount found = articleViewCountBackupRepository.findById(ARTICLE_ID).orElseThrow();
        assertThat(found.getViewCount()).isEqualTo(100L);

        // 작은 값 update: 50 < 100 → 무시
        int notUpdated = articleViewCountBackupRepository.updateViewCount(ARTICLE_ID, 50L);
        assertThat(notUpdated).isEqualTo(0);

        // 정리
        articleViewCountBackupRepository.deleteById(ARTICLE_ID);
    }
}