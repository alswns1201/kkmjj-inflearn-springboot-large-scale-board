package kube.board.like.api;

import kube.board.like.service.response.ArticleLikeResponse;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ArticleLikeApiTest {
    RestClient restClient = RestClient.create("http://localhost:9000");

    Long articleId = 1L;
    Long userId = 1L;

    @Test
    void like() {
        restClient.post()
                .uri("/v1/articles-likes/articles/{articleId}/users/{userId}", articleId, userId)
                .retrieve()
                .toBodilessEntity();

        System.out.println("like success articleId=%s, userId=%s".formatted(articleId, userId));
    }

    @Test
    void read() {
        ArticleLikeResponse response = restClient.get()
                .uri("/v1/articles-likes/articles/{articleId}/users/{userId}", articleId, userId)
                .retrieve()
                .body(ArticleLikeResponse.class);

        System.out.println("response = " + response);
    }

    @Test
    void unlike() {
        restClient.delete()
                .uri("/v1/articles-likes/articles/{articleId}/users/{userId}", articleId, userId)
                .retrieve()
                .toBodilessEntity();

        System.out.println("unlike success articleId=%s, userId=%s".formatted(articleId, userId));
    }

    @Test
    void likeAndRead() {
        like();
        read();
    }

    @Test
    void likeAndUnlikeAndRead() {
        like();
        unlike();

        try {
            read();
            System.out.println("ERROR: should have thrown after unlike");
        } catch (Exception e) {
            System.out.println("Expected exception after unlike: " + e.getMessage());
        }
    }

    @Test
    void likePerformanceTest() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        likePerformanceTest(executorService,1111L,"pessimistic-lock-1"); // 3천번 호출
        likePerformanceTest(executorService,2222L,"pessimistic-lock-2");// 3천번 호출
        likePerformanceTest(executorService,3333L,"optimistic-lock");// 3천번 호출
    }

    void likePerformanceTest(ExecutorService executorService,Long articleId, String lockType) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(30000);
        System.out.println(lockType + "start");
        like();

        long start = System.nanoTime();
        for(int i=0;i<3000;i++){
            long userId =  i+2;
            executorService.submit(()->{
                like();
                countDownLatch.countDown();
            });
        }
        countDownLatch.wait();

        long end = System.nanoTime();

        System.out.println("lockTpy = "+ lockType +", time = "+(end+start)/ 1000000 +"ms");
        System.out.println(lockType+" end");


    }


}