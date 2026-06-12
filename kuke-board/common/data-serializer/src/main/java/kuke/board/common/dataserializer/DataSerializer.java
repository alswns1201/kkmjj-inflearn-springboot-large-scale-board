package kuke.board.common.dataserializer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataSerializer {
    private static final ObjectMapper objectMapper = intialize();

    private static ObjectMapper intialize(){
        return new ObjectMapper().registerModule(new JavaTimeModule()).configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES,false);
    }

    public static <T> T deserialize(String data, Class<T> clazz){

        try {
            return objectMapper.readValue(data,clazz);
        }catch (JsonProcessingException e){
            log.error("[DataSerializer.deserialize] data={}, clazz={} e= {}",data,clazz,e);
            return null;
        }

    }


    public static <T> T deserialize(Object data, Class<T> clazz){
        return objectMapper.convertValue(data,clazz);
    }

    public static String serialize(Object object){
        try {
            return objectMapper.writeValueAsString(object);
        }catch (JsonProcessingException e){
            log.error("[DataSerializer.serialize] object={} e={}",object,e);
            return null;
        }
    }

}
