package website.yevhenii.yevheniiJavaBot.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import website.yevhenii.yevheniiJavaBot.entities.Localization;
import website.yevhenii.yevheniiJavaBot.enums.Localizations;

import java.util.HashMap;
import java.util.Map;

@Service
public class LocalizationService {

    @Value("${paths.localization}")
    String localizationFolderPath;

    public Map<Long, Localizations> getUsersLocalizationStorage() {
        return usersLocalizationStorage;
    }

    private final Map<Long, Localizations> usersLocalizationStorage = new HashMap();

    private final Map<Localizations, String> localDictionaryFileNames = Map.of(
            Localizations.EN, "en.json",
            Localizations.UA, "ua.json"
    );

    public Localization getDictionaryForUser(Long chatId) {
        Localizations userLocalization = getUserLocalization(chatId);
        String fileName = localDictionaryFileNames.get(userLocalization);
        return getLocalDictionary(fileName);
    }

    public void setUserLocalization(Long chatId, Localizations localizations) {

        switch (localizations) {
            case UA:
                usersLocalizationStorage.put(chatId, Localizations.UA);
                break;

            default:
                usersLocalizationStorage.put(chatId, Localizations.EN);
                break;
        }
    }

    private Localizations getUserLocalization(Long chatId) {
        if (getUsersLocalizationStorage().containsKey(chatId)) {
            return getUsersLocalizationStorage().get(chatId);
        } else {
            return Localizations.EN;
        }
    }

    private Localization getLocalDictionary(String fileName) {
        return Parser.parseJSON(localizationFolderPath.concat(fileName), Localization.class);
    }
}
