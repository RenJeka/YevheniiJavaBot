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

    public Localizations getDefaultLocalizations() {
        return defaultLocalizations;
    }

    private Localizations defaultLocalizations = Localizations.EN;

    public Map<String, Localizations> getUsersLocalizationStorage() {
        return usersLocalizationStorage;
    }

    private final Map<String, Localizations> usersLocalizationStorage = new HashMap();

    private final Map<Localizations, String> localDictionaryFileNames = Map.of(
            Localizations.EN, "en.json",
            Localizations.UA, "ua.json"
    );

    public Localization getDefaultDictionary() {
        String fileName = localDictionaryFileNames.get(defaultLocalizations);
        return getLocalDictionary(fileName);
    }

    public Localization getDictionaryForUser(Long chatId) {
        return getDictionaryForUser(chatId.toString());
    }
    public Localization getDictionaryForUser(String chatId) {
        Localizations userLocalization = getUserLocalization(chatId);
        String fileName = localDictionaryFileNames.get(userLocalization);
        return getLocalDictionary(fileName);
    }

    public void setUserLocalization(Long chatId, Localizations localizations){
        setUserLocalization(chatId.toString(), localizations);
    }
    public void setUserLocalization(String chatId, Localizations localizations) {

        switch (localizations) {
            case UA:
                usersLocalizationStorage.put(chatId, Localizations.UA);
                break;

            default:
                usersLocalizationStorage.put(chatId, defaultLocalizations);
                break;
        }
    }

    @Override
    public String toString() {
        return "LocalizationService{" +
                "localizationFolderPath='" + localizationFolderPath + '\'' +
                ", defaultLocalizations=" + defaultLocalizations +
                ", usersLocalizationStorage=" + usersLocalizationStorage +
                ", localDictionaryFileNames=" + localDictionaryFileNames +
                '}';
    }

    private Localizations getUserLocalization(String chatId) {
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
