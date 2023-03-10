package website.yevhenii.yevheniiJavaBot.services;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import website.yevhenii.yevheniiJavaBot.enums.ButtonsSet;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

@Service
public class ButtonService {

    private final LinkedList<LinkedHashMap<String, String>> generalButtons = getGeneralButtons();

    private LinkedList<LinkedHashMap<String, String>> getGeneralButtons() {
        LinkedList<LinkedHashMap<String, String>> keyboard = new LinkedList();

        LinkedHashMap row1 = new LinkedHashMap<>();
        LinkedHashMap row2 = new LinkedHashMap<>();

        row1.put("General Info about this bot", "general_info");
        row1.put("Technology stack", "technology_stack");

        row2.put("My business card", "business_card");
        row2.put("Get NBU currency rates", "currency_rate");

        keyboard.add(row1);
        keyboard.add(row2);

        return keyboard;
    }

    private final LinkedList<LinkedHashMap<String, String>> localizationButtons = getLocalizationButtons();

    private LinkedList<LinkedHashMap<String, String>> getLocalizationButtons() {
        LinkedList<LinkedHashMap<String, String>> keyboard = new LinkedList();

        LinkedHashMap row1 = new LinkedHashMap<>();

        row1.put("en", "en");
        row1.put("ua", "ua");

        keyboard.add(row1);

        return keyboard;
    }


    public void attachButtons(SendMessage message) {
        attachButtons(message, ButtonsSet.GENERAL);
    }
    public void attachButtons(SendMessage message, ButtonsSet buttonsSetType) {
        LinkedList<LinkedHashMap<String, String>> buttonsSet = getButtonsSet(buttonsSetType);

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

    private LinkedList<LinkedHashMap<String, String>> getButtonsSet(ButtonsSet buttonsSet) {
        switch (buttonsSet) {
            case LOCALIZATION:
                return (LinkedList<LinkedHashMap<String, String>>) localizationButtons.clone();
            default:
                return (LinkedList<LinkedHashMap<String, String>>) generalButtons.clone();
        }

    }

    private String getCorrectText(String text) {
        return new String(text.getBytes(), StandardCharsets.UTF_8);
    }

}
