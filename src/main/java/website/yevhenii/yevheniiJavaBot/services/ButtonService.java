package website.yevhenii.yevheniiJavaBot.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import website.yevhenii.yevheniiJavaBot.entities.Localization;
import website.yevhenii.yevheniiJavaBot.enums.ButtonsSet;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

@Service
public class ButtonService {

//    private static final Logger logger = LoggerFactory.getLogger(ButtonService.class);

    private LocalizationService localizationService;

    @Autowired
    public void setLocalizationService(LocalizationService localizationService) {
        this.localizationService = localizationService;
    }

    private LinkedList<LinkedHashMap<String, String>> localizationButtons = getLocalizationButtons();

    private LinkedList<LinkedHashMap<String, String>> getLocalizationButtons() {
        LinkedList<LinkedHashMap<String, String>> keyboard = new LinkedList();

        LinkedHashMap row1 = new LinkedHashMap<>();

        row1.put("English", "en");
        row1.put("Українська", "ua");

        keyboard.add(row1);

        return keyboard;
    }


    public void attachButtons(SendMessage message) {
        attachButtons(message, ButtonsSet.GENERAL);
    }
    public void attachButtons(SendMessage message, ButtonsSet buttonsSetType) {

        LinkedList<LinkedHashMap<String, String>> buttonsSet = getButtonsSet(buttonsSetType, message.getChatId());

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

//        button rows loop
        for (LinkedHashMap<String, String> buttonsRow : buttonsSet) {

            List<InlineKeyboardButton> keyboardRow = new ArrayList<>();
//        button columns loop
            for (String buttonName : buttonsRow.keySet()) {
                String buttonValue = buttonsRow.get(buttonName);

                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(getCorrectText(buttonName));
                button.setCallbackData(buttonValue);

                keyboardRow.add(button);
            }
            keyboard.add(keyboardRow);
        }

        markup.setKeyboard(keyboard);
        message.setReplyMarkup(markup);

    }

    private LinkedList<LinkedHashMap<String, String>> getButtonsSet(ButtonsSet buttonsSetType, String chatId) {
        Localization userLocalization = localizationService.getDictionaryForUser(chatId);

        switch (buttonsSetType) {
            case LOCALIZATION:
                return (LinkedList<LinkedHashMap<String, String>>) localizationButtons.clone();
            default:
                return (LinkedList<LinkedHashMap<String, String>>) getGeneralButtons(userLocalization).clone();
        }
    }

    private String getCorrectText(String text) {
        return new String(text.getBytes(), StandardCharsets.UTF_8);
    }

    private LinkedList<LinkedHashMap<String, String>> getGeneralButtons() {
        return getGeneralButtons(localizationService.getDefaultDictionary());
    }
    private LinkedList<LinkedHashMap<String, String>> getGeneralButtons(Localization localeDictionary) {
        LinkedList<LinkedHashMap<String, String>> keyboard = new LinkedList();

        LinkedHashMap row1 = new LinkedHashMap<>();
        LinkedHashMap row2 = new LinkedHashMap<>();

        row1.put(localeDictionary.generalInfoBtn, "general_info");
        row1.put(localeDictionary.technologyStackBtn, "technology_stack");

        row2.put(localeDictionary.businessCardBtn, "business_card");
        row2.put(localeDictionary.currencyRateBtn, "currency_rate");

        keyboard.add(row1);
        keyboard.add(row2);

        return keyboard;
    }
}
