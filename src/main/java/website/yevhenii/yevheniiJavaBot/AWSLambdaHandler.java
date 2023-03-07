package website.yevhenii.yevheniiJavaBot;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.function.Function;

@Component
public class AWSLambdaHandler implements Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Value("${telegrambotWebhookURI}")
    String telegrambotWebhookURI;

    private static final Logger logger = LoggerFactory.getLogger(AWSLambdaHandler.class);

    private YevheniiSpringAWSBot yevheniiSpringAWSBot;
    ObjectMapper objectMapper = new ObjectMapper();

    public AWSLambdaHandler(YevheniiSpringAWSBot yevheniiSpringAWSBot) {
        this.yevheniiSpringAWSBot = yevheniiSpringAWSBot;
    }

    @Override
    public APIGatewayProxyResponseEvent apply(APIGatewayProxyRequestEvent input) {
        try {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(yevheniiSpringAWSBot, setWebhook());
            Update update = objectMapper.readValue(input.getBody(), Update.class);
            yevheniiSpringAWSBot.onWebhookUpdateReceived(update, logger);
            APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
            response.setStatusCode(200);
            response.setBody("OK");
            return response;
        } catch (Exception e) {
            throw new RuntimeException("handle Request failed: ", e);
        }
    }

    private SetWebhook setWebhook() {
        return SetWebhook.builder()
                .url(telegrambotWebhookURI)
                .build();
    }

}
