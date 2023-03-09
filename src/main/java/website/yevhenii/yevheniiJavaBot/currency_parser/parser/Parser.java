package website.yevhenii.yevheniiJavaBot.currency_parser.parser;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;


/**
 * Created by Yevhenii Petrushenko.
 */
public class Parser {

    public static <T> T parseURL(String url, Class<T> valueType) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.readValue(new URL(url), valueType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
