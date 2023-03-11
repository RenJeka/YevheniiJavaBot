package website.yevhenii.yevheniiJavaBot.services;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class UserService {

    public Long getChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getFrom().getId();
        }

        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom().getId();
        }

        return null;
    }

    public String getUserName(Update update) {
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
}
