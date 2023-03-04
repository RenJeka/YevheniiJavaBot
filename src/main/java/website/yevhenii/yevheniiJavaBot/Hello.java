package website.yevhenii.yevheniiJavaBot;

import java.util.function.Function;

public class Hello implements Function<String, String> {
    @Override
    public String apply(String s) {
        return "The message was: " + s;
    }

}
