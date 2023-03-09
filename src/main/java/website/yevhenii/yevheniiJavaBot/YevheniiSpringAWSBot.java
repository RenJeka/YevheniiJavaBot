package website.yevhenii.yevheniiJavaBot;

import org.slf4j.Marker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.slf4j.Logger;
import website.yevhenii.yevheniiJavaBot.currency_parser.parser.Parser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

@Component
public class YevheniiSpringAWSBot extends TelegramWebhookBot {

    @Value("${telegrambotUsername}")
    public String username;
    @Value("${telegrambot.path}")
    public String path;
    @Value("${paths.image}")
    String imagePath;

    private final LinkedHashMap<String, String> keyboardButtons = getKeyboardButtons();

    private LinkedHashMap<String, String> getKeyboardButtons() {
        LinkedHashMap buttons = new LinkedHashMap<>();

        buttons.put("General Info about this bot", "general_info");
        buttons.put("Technology stack", "technology_stack");
        buttons.put("My business card", "business_card");
        buttons.put("Get NBU currency rates", "currency_rate");
        return buttons;
    }

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

        if (update.hasCallbackQuery()) {
            if (update.getCallbackQuery().getData().equals("general_info")) {
                giveGeneralInfo(update, logger);
            } else if (update.getCallbackQuery().getData().equals("technology_stack")) {
                giveTechnologyStack(update, logger);
            } else if (update.getCallbackQuery().getData().equals("business_card")) {
                giveBusinessCard(update, logger);
            } else if (update.getCallbackQuery().getData().equals("currency_rate")) {
                giveCurrencyRates(update, logger);
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


    private void attachButtons(SendMessage message) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (String buttonName : keyboardButtons.keySet()) {
            String buttonValue = keyboardButtons.get(buttonName);

            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(getCorrectText(buttonName));
            button.setCallbackData(buttonValue);

            keyboard.add(Arrays.asList(button));
        }

        markup.setKeyboard(keyboard);
        message.setReplyMarkup(markup);

    }

    /**
     * Helper method to get text in correct encoding
     * @param text
     * @return
     */
    private String getCorrectText(String text) {
        return new String(text.getBytes(), StandardCharsets.UTF_8);
    }

    private String getGreetingMessage(Update update) {
        String firstName = !(update.getMessage().getFrom().getFirstName()).isEmpty() ? update.getMessage().getFrom().getFirstName() : "";
        String lastName = !(update.getMessage().getFrom().getLastName()).isEmpty() ? update.getMessage().getFrom().getLastName() : "";

        String userName = firstName + " " + lastName;
        return String.format("Hello, %s! This is my simple Telegram bot on Java Spring! Please, use menu below to know more about it. ", userName);
    }

    private void greetingUser(Update update, Logger logger) {
        SendMessage message = createMessage(getGreetingMessage(update), getChatId(update));

        attachButtons(message);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Error, while sending message: " + e.getStackTrace().toString());
            throw new RuntimeException(e);
        }
    }

    private void giveCommonAnswer(Update update, Logger logger) {
        String updateText = update.getMessage().getText();
        SendMessage message = createMessage(
                "You said: '" + updateText + "', but this bot very simple and do only actions below",
                getChatId(update));
        attachButtons(message);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Error, while sending message: " + e.getStackTrace().toString());
            throw new RuntimeException(e);
        }
    }

    private void giveGeneralInfo(Update update, Logger logger) {
        SendMessage message = createMessage(
                "This is simple project to show my experience in Java and related technologies (see \"*technology stack*\"); \n" +
                        "\n" +
                        "The bot can output text, images and give information by pressing the buttons.\n" +
                        "\n" +
                        "Think of it like a business card bot." +
                        "\n" +
                        "See the project via this [GitHub link](https://github.com/RenJeka/Study/tree/master/Java_projects/hello-lambda-core)",
                getChatId(update));

        attachButtons(message);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Error, while sending message: " + e.getStackTrace().toString());
            throw new RuntimeException(e);
        }
    }

    private void giveTechnologyStack(Update update, Logger logger) {
        SendMessage message = createMessage(
                "This project was built by using: \n" +
                        " ✔️ Java POJO;\n" +
                        " ✔️ Java Corretto 11 SDK;\n" +
                        " ✔️ Software project management — 'Maven';\n" +
                        " ✔️ 'telegrambots' Java library;\n" +
                        " ✔️ Telegram webhooks (to get info prom telegram);\n" +
                        " ✔️ AWS Lambda\n",
                getChatId(update));

        attachButtons(message);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Error, while sending message: " + e.getStackTrace().toString());
            throw new RuntimeException(e);
        }
    }

    private void giveBusinessCard(Update update, Logger logger) {
        sendImage("photo", getChatId(update), logger);
        SendMessage message = createMessage(
                "My name is Yevhenii Petrushenko.\n" +
                        "\n" +
                        "I am 29 y.o. Front-end developer who specializes in websites and single-page application (SPA) development.\n" +
                        "\n" +
                        "My expertise is Angular 2+, JavaScript, TypeScript, HTML / CSS (+ any preprocessors), RxJS, Unit and e2e Tests, GIT, Jira etc...\n" +
                        "\n" +
                        "I have 3+ years commercial experience and work with various technologies in different commands.\n" +
                        "\n" +
                        "I am friendly, sociable, fond of sports, psychology, technology, business and much more \uD83D\uDE0A\n" +
                        "\n" +
                        "You can visit my portfolio by clicking on [this portfolio link](http://yevhenii.website/).\n" +
                        "\n" +
                        "You can also visit my github by clicking on [this github link](https://github.com/RenJeka).\n" +
                        "\n" +
                        "Of course, you can contact me by clicking on [this telegram link](https://t.me/RenJeka).\n",
                getChatId(update));

        attachButtons(message);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Error, while sending message: " + e.getStackTrace().toString());
            throw new RuntimeException(e);
        }
    }

    private void giveCurrencyRates(Update update, Logger logger) {

        sendImage("exchange_rate", getChatId(update), logger);

        try {
            String formattedCurrencyRates = Parser.getFormattedCurrencyRates();
            SendMessage message = createMessage(formattedCurrencyRates, getChatId(update));
            attachButtons(message);
            execute(message);
        } catch (TelegramApiException | IOException e) {
            logger.error("Error, while sending message: " + e.getStackTrace().toString());
            throw new RuntimeException(e);
        }
    }

    public void sendImage(String name, Long chatId, Logger logger) {
        SendPhoto photo = new SendPhoto();
        InputFile inputFile = new InputFile();

        inputFile.setMedia(new File(imagePath + name + ".jpg"));

        photo.setPhoto(inputFile);
        photo.setChatId(chatId);

        try {
            execute(photo);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }

    }
}
