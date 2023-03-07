package website.yevhenii.yevheniiJavaBot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.slf4j.Logger;
import java.nio.charset.StandardCharsets;

@Component
public class YevheniiSpringAWSBot extends TelegramWebhookBot {

    @Value("${telegrambotUsername}")
    public String username;
    @Value("${telegrambot.path}")
    public String path;

    public YevheniiSpringAWSBot(@Value("${telegrambotToken}")String token) {
        super(token);
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update)  {
        return null;
    }
    public BotApiMethod<?> onWebhookUpdateReceived(Update update, Logger logger) {
        if (update.hasMessage()) {
            String updateMessage = update.getMessage().getText();
            if (updateMessage.equals("/start")) {
                greetingUser(update, logger);
            } else {
                giveCommonAnswer(update, logger);
            }
        }
        return null;
    }

    @Override
    public String getBotPath() {
        return path;
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    public Long getChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getFrom().getId();
        }

        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom().getId();
        }

        return null;
    }


    private SendMessage createMessage(String text, Long chatId) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(new String(text.getBytes(), StandardCharsets.UTF_8));
        sendMessage.setParseMode("markdown");
        return sendMessage;
    }


    private void greetingUser(Update update, Logger logger) {
        SendMessage message = createMessage(getGreetingMessage(update), getChatId(update));

        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Error, while sending message: " + e.getStackTrace().toString());
            throw new RuntimeException(e);
        }
    }

    private String getGreetingMessage(Update update) {
        String firstName = !(update.getMessage().getFrom().getFirstName()).isEmpty() ? update.getMessage().getFrom().getFirstName() : "";
        String lastName = !(update.getMessage().getFrom().getLastName()).isEmpty() ? update.getMessage().getFrom().getLastName() : "";

        String userName = firstName + " " + lastName;
        return String.format("Hello, %s! This is my simple Telegram bot on Java Spring! Please, use menu below to know more about it. ", userName);
    }

    private void giveCommonAnswer(Update update, Logger logger) {
        String updateText = update.getMessage().getText();
        SendMessage message = createMessage(
                "You said: '" + updateText + "', but this bot very simple and do only actions below",
                getChatId(update));
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Error, while sending message: " + e.getStackTrace().toString());
            throw new RuntimeException(e);
        }
    }
}
