package website.yevhenii.yevheniiJavaBot;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
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
public class AWSLambdaHandler implements Function<String, APIGatewayProxyResponseEvent> {

    ObjectMapper objectMapper = new ObjectMapper();

    @Value("${telegrambotWebhookURI}")
    String telegrambotWebhookURI;

    private static final Logger logger = LoggerFactory.getLogger(AWSLambdaHandler.class);

    private YevheniiSpringAWSBot yevheniiSpringAWSBot;


    public AWSLambdaHandler(YevheniiSpringAWSBot yevheniiSpringAWSBot) {
        this.yevheniiSpringAWSBot = yevheniiSpringAWSBot;
    }

    @Override
    public APIGatewayProxyResponseEvent apply(String input) {
        try {
            Update update = objectMapper.readValue(input, Update.class);
            logger.info(getUpdateMessage(update));
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(yevheniiSpringAWSBot, setWebhook());
            yevheniiSpringAWSBot.onWebhookUpdateReceived(update);
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

    private String getUpdateMessage(Update update) throws JsonProcessingException {
        if (update.hasMessage()) {
            return "message: " + update.getMessage().getText();
        } else if (update.hasCallbackQuery()) {
            return "callbackQuery: " + update.getCallbackQuery().getData();
        }
        return objectMapper.writeValueAsString(update);
    }

}
