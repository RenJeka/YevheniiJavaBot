package website.yevhenii.yevheniiJavaBot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import website.yevhenii.yevheniiJavaBot.entities.Localization;
import website.yevhenii.yevheniiJavaBot.enums.ButtonsSet;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class MessageService {

    @Value("${paths.image}")
    String imagePath;

    LocalizationService localizationService;
    ButtonService buttonService;
    CurrencyRatesService currencyRatesService;

    @Autowired
    public void setLocalizationService(LocalizationService localizationService) {
        this.localizationService = localizationService;
    }

    @Autowired
    public void setButtonService(ButtonService buttonService) {
        this.buttonService = buttonService;
    }

    @Autowired
    public void setCurrencyRatesService(CurrencyRatesService currencyRatesService) {
        this.currencyRatesService = currencyRatesService;
    }

    public SendMessage greetingUser(Update update) {
        SendMessage message = createMessage(getGreetingMessage(update), getChatId(update));

        buttonService.attachButtons(message);
        return message;
    }

    public SendMessage letUserChooseLocalization(Update update) {
        SendMessage message = createMessage(getChooseLocalizationMessage(), getChatId(update));

        buttonService.attachButtons(message, ButtonsSet.LOCALIZATION);
        return message;
    }

    public SendMessage giveCommonAnswer(Update update) {
        String updateText = update.getMessage().getText();
        SendMessage message = createMessage(String.format(localizationService.getDictionaryForUser(getChatId(update)).commonAnswerForUsersMessage, updateText),
                getChatId(update));
        buttonService.attachButtons(message);
        return message;
    }

    public SendMessage giveGeneralInfo(Update update) {
        SendMessage message = createMessage(
                localizationService.getDictionaryForUser(getChatId(update)).generalInfo,
                getChatId(update));

        buttonService.attachButtons(message);
        return message;
    }

    public SendMessage giveTechnologyStack(Update update) {
        SendMessage message = createMessage(
                localizationService.getDictionaryForUser(getChatId(update)).technologyStack,
                getChatId(update));

        buttonService.attachButtons(message);
        return message;
    }

    public SendMessage giveBusinessCard(Update update) {
        SendMessage message = createMessage(localizationService.getDictionaryForUser(getChatId(update)).businessCard,
                getChatId(update));

        buttonService.attachButtons(message);
        return message;
    }

    public SendMessage giveCurrencyRates(Update update) {
        String formattedCurrencyRates = null;
        try {
            formattedCurrencyRates = currencyRatesService.getFormattedCurrencyRates(getChatId(update));
        } catch (IOException e) {
            e.printStackTrace();
        }
        SendMessage message = createMessage(formattedCurrencyRates, getChatId(update));
        buttonService.attachButtons(message);
        return message;
    }

    public SendPhoto sendImage(String name, Long chatId) {
        SendPhoto photo = new SendPhoto();
        InputFile inputFile = new InputFile();

        inputFile.setMedia(new File(imagePath + name + ".jpg"));

        photo.setPhoto(inputFile);
        photo.setChatId(chatId);
        return photo;
    }



    private Long getChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getFrom().getId();
        }

        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom().getId();
        }

        return null;
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

    private SendMessage createMessage(String text, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(new String(text.getBytes(), StandardCharsets.UTF_8));
        sendMessage.setParseMode("markdown");
        return sendMessage;
    }

    private String getChooseLocalizationMessage() {
        return localizationService.getMessagesForAllLanguages();
    }
}
