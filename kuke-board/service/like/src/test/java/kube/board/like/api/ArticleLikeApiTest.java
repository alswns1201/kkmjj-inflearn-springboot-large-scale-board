package kube.board.like.api;

import kube.board.like.service.response.ArticleLikeResponse;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

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
}