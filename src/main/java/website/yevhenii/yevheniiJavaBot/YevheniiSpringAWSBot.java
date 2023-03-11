package website.yevhenii.yevheniiJavaBot;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.slf4j.Logger;
import website.yevhenii.yevheniiJavaBot.enums.Localizations;
import website.yevhenii.yevheniiJavaBot.services.ButtonService;
import website.yevhenii.yevheniiJavaBot.services.LocalizationService;
import website.yevhenii.yevheniiJavaBot.services.MessageService;
import website.yevhenii.yevheniiJavaBot.services.UserService;

@Component
public class YevheniiSpringAWSBot extends TelegramWebhookBot {

    @Value("${telegrambotUsername}")
    public String username;
    @Value("${telegrambot.path}")
    public String path;

    private static final Logger logger = LoggerFactory.getLogger(ButtonService.class);

    private LocalizationService localizationService;
    private MessageService messageService;
    private UserService userService;

    @Autowired
    public void setLocalizationService(LocalizationService localizationService) {
        this.localizationService = localizationService;
    }

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public YevheniiSpringAWSBot(@Value("${telegrambotToken}")String token) {
        super(token);
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().getText() != null) {
            String updateMessage = update.getMessage().getText();
            if (updateMessage.equals("/start")) {
                executeContent(messageService.letUserChooseLocalization(update));
            } else {
                executeContent(messageService.giveCommonAnswer(update));
            }
        }

        if (update.hasCallbackQuery()) {
            switch (update.getCallbackQuery().getData()) {
                case ("locale_en"):
                    localizationService.setUserLocalization(userService.getChatId(update), Localizations.EN);
                    executeContent(messageService.getGreetingMessage(update));
                    break;
                case ("locale_ua"):
                    localizationService.setUserLocalization(userService.getChatId(update), Localizations.UA);
                    executeContent(messageService.getGreetingMessage(update));
                    break;
                case ("general_info"):
                    executeContent(messageService.getGeneralInfoMessage(update));
                    break;
                case ("technology_stack"):
                    executeContent(messageService.getTechnologyStackMessage(update));
                    break;
                case ("business_card"):
                    executeContent(messageService.getPhotoMessage("photo", userService.getChatId(update)));
                    executeContent(messageService.getBusinessCardMessage(update));
                    break;
                case ("currency_rate"):
                    executeContent(messageService.getPhotoMessage("exchange_rate", userService.getChatId(update)));
                    executeContent(messageService.getCurrencyRatesMassage(update));
                    break;

                default:
                    return null;
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

    private void executeContent(SendPhoto photo) {
        try {
            execute(photo);
        } catch (TelegramApiException e) {
            logger.error("Error, while sending photo: ", e);
            throw new RuntimeException(e);
        }
    }
    private void executeContent(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Error, while sending message: ", e);
            throw new RuntimeException(e);
        }
    }
}
