package website.yevhenii.yevheniiJavaBot.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import website.yevhenii.yevheniiJavaBot.entities.Localization;

@Service
public class LocalizationService {

    @Value("${paths.localization}")
    String localizationFolderPath;

    public Localization getLocalization(String fileName) {
        String fullPath = localizationFolderPath.concat(fileName);
        System.out.println("fullPath: " + fullPath);
        return Parser.parseJSON(localizationFolderPath.concat(fileName), Localization.class);
    }
}
