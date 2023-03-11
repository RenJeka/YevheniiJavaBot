package website.yevhenii.yevheniiJavaBot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.slf4j.Logger;
import website.yevhenii.yevheniiJavaBot.entities.Localization;
import website.yevhenii.yevheniiJavaBot.enums.ButtonsSet;
import website.yevhenii.yevheniiJavaBot.enums.Localizations;
import website.yevhenii.yevheniiJavaBot.services.ButtonService;
import website.yevhenii.yevheniiJavaBot.services.CurrencyRatesService;
import website.yevhenii.yevheniiJavaBot.services.LocalizationService;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class YevheniiSpringAWSBot extends TelegramWebhookBot {

    @Value("${telegrambotUsername}")
    public String username;
    @Value("${telegrambot.path}")
    public String path;
    @Value("${paths.image}")
    String imagePath;

    private LocalizationService localizationService;

    @Autowired
    public void setLocalizationService(LocalizationService localizationService) {
        this.localizationService = localizationService;
    }

    private ButtonService buttonService;

    @Autowired
    public void setButtonService(ButtonService buttonService) {
        this.buttonService = buttonService;
    }

    private CurrencyRatesService currencyRatesService;

    @Autowired
    public void setCurrencyRatesService(CurrencyRatesService currencyRatesService) {
        this.currencyRatesService = currencyRatesService;
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
                letUserChooseLocalization(update, logger);
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
            } else if (update.getCallbackQuery().getData().equals("en")) {
                localizationService.setUserLocalization(getChatId(update), Localizations.EN);
                greetingUser(update, logger);
            } else if (update.getCallbackQuery().getData().equals("ua")) {
                localizationService.setUserLocalization(getChatId(update), Localizations.UA);
                greetingUser(update, logger);
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

    private String getChooseLocalizationMessage(Update update) {
        return String.format("Please Choose language: ");
    }

    private String getGreetingMessage(Update update) {
        Localization userDictionary = localizationService.getDictionaryForUser(getChatId(update));
        return String.format(userDictionary.greetingUser, getUserName(update));
    }

    private String getUserName(Update update) {
        String userFirstName = "";
        String userLastName = "";
        String resultFirstName = "";
        String resultLastName = "";

        if (update.hasMessage()) {
            userFirstName = update.getMessage().getFrom().getFirstName();
            userLastName = update.getMessage().getFrom().getLastName();
        }

        if (update.hasCallbackQuery()) {
            userFirstName = update.getCallbackQuery().getFrom().getFirstName();
            userLastName = update.getCallbackQuery().getFrom().getLastName();
        }
        resultFirstName = (userFirstName != null && userFirstName != "") ? userFirstName : "";
        resultLastName = (userLastName != null && userLastName != "") ? userLastName : "";

        return resultFirstName + " " + resultLastName;
    }


    private void greetingUser(Update update, Logger logger) {
        SendMessage message = createMessage(getGreetingMessage(update), getChatId(update));

        buttonService.attachButtons(message);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Error, while sending message: " + e.getStackTrace().toString());
            throw new RuntimeException(e);
        }
    }

    private void letUserChooseLocalization(Update update, Logger logger) {
        SendMessage message = createMessage(getChooseLocalizationMessage(update), getChatId(update));

        buttonService.attachButtons(message, ButtonsSet.LOCALIZATION);
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
        buttonService.attachButtons(message);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Error, while sending message: " + e.getStackTrace().toString());
            throw new RuntimeException(e);
        }
    }

    private void giveGeneralInfo(Update update, Logger logger) {
        SendMessage message = createMessage(
                localizationService.getDictionaryForUser(getChatId(update)).generalInfo,
                getChatId(update));

        buttonService.attachButtons(message);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Error, while sending message: " + e.getStackTrace().toString());
            throw new RuntimeException(e);
        }
    }

    private void giveTechnologyStack(Update update, Logger logger) {
        SendMessage message = createMessage(
                localizationService.getDictionaryForUser(getChatId(update)).technologyStack,
                getChatId(update));

        buttonService.attachButtons(message);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Error, while sending message: " + e.getStackTrace().toString());
            throw new RuntimeException(e);
        }
    }

    private void giveBusinessCard(Update update, Logger logger) {
        sendImage("photo", getChatId(update), logger);
        SendMessage message = createMessage(localizationService.getDictionaryForUser(getChatId(update)).businessCard,
                getChatId(update));

        buttonService.attachButtons(message);
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
            String formattedCurrencyRates = currencyRatesService.getFormattedCurrencyRates();
            SendMessage message = createMessage(formattedCurrencyRates, getChatId(update));
            buttonService.attachButtons(message);
            execute(message);
        } catch (TelegramApiException | IOException e) {
            logger.error("Error, while sending message: " + e.getStackTrace().toString());
            throw new RuntimeException(e);
        }
    }

    private void sendImage(String name, Long chatId, Logger logger) {
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
