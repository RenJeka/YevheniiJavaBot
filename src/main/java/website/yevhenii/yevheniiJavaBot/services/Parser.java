package website.yevhenii.yevheniiJavaBot.services;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.URL;


/**
 * Created by Yevhenii Petrushenko.
 */
public class Parser {
    static ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T parseURL(String url, Class<T> valueType) {

        try {
            return objectMapper.readValue(new URL(url), valueType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T parseJSON(String path, Class<T> valueType) {

        try {
            return objectMapper.readValue(new File(path), valueType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
