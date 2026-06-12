package kuke.board.common.event;

import kuke.board.common.event.payload.ArticleCreatedEventPayload;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.*;

class EventTest {

    @Test
    void serde(){
      ArticleCreatedEventPayload payload=  ArticleCreatedEventPayload.builder()
                .articleId(1L)
                .title("title").content("content").boardId(1L)
                .writerId(1L).createAt(LocalDateTime.now()).modifiedAt(LocalDateTime.now()).boardArticleCount(25L).build();

        Event<EventPayload> event = Event.of(1234L,EventType.ARTICLE_CREATE,payload);
        String json = event.toJson();

        Event<EventPayload> result = Event.fromJson(json);

        assertThat(result.getEventId()).isEqualTo(event.getEventId());

    }

}