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

    private LocalizationService localizationService;
    private ButtonService buttonService;
    private CurrencyRatesService currencyRatesService;
    private UserService userService;

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

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public SendMessage getGreetingMessage(Update update) {
        Localization userDictionary = localizationService.getDictionaryForUser(userService.getChatId(update));
        String formattingMessage = String.format(userDictionary.greetingUser, userService.getUserName(update));
        SendMessage message = createMessage(formattingMessage, userService.getChatId(update));

        buttonService.attachButtons(message);
        return message;
    }

    public SendMessage letUserChooseLocalization(Update update) {
        SendMessage message = createMessage(getChooseLocalizationMessage(), userService.getChatId(update));

        buttonService.attachButtons(message, ButtonsSet.LOCALIZATION);
        return message;
    }

    public SendMessage giveCommonAnswer(Update update) {
        String updateText = update.getMessage().getText();
        SendMessage message = createMessage(String.format(localizationService.getDictionaryForUser(userService.getChatId(update)).commonAnswerForUsersMessage, updateText),
                userService.getChatId(update));
        buttonService.attachButtons(message);
        return message;
    }

    public SendMessage getGeneralInfoMessage(Update update) {
        SendMessage message = createMessage(
                localizationService.getDictionaryForUser(userService.getChatId(update)).generalInfo,
                userService.getChatId(update));

        buttonService.attachButtons(message);
        return message;
    }

    public SendMessage getTechnologyStackMessage(Update update) {
        SendMessage message = createMessage(
                localizationService.getDictionaryForUser(userService.getChatId(update)).technologyStack,
                userService.getChatId(update));

        buttonService.attachButtons(message);
        return message;
    }

    public SendMessage getBusinessCardMessage(Update update) {
        SendMessage message = createMessage(localizationService.getDictionaryForUser(userService.getChatId(update)).businessCard,
                userService.getChatId(update));

        buttonService.attachButtons(message);
        return message;
    }

    public SendMessage getCurrencyRatesMassage(Update update) {
        String formattedCurrencyRates = null;
        try {
            formattedCurrencyRates = currencyRatesService.getFormattedCurrencyRates(userService.getChatId(update));
        } catch (IOException e) {
            e.printStackTrace();
        }
        SendMessage message = createMessage(formattedCurrencyRates, userService.getChatId(update));
        buttonService.attachButtons(message);
        return message;
    }

    public SendPhoto getPhotoMessage(String name, Long chatId) {
        SendPhoto photo = new SendPhoto();
        InputFile inputFile = new InputFile();

        inputFile.setMedia(new File(imagePath + name + ".jpg"));

        photo.setPhoto(inputFile);
        photo.setChatId(chatId);
        return photo;
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
