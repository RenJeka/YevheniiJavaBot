package website.yevhenii.yevheniiJavaBot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import website.yevhenii.yevheniiJavaBot.entities.CurrencyRate;

import java.io.IOException;
import java.math.RoundingMode;

@Service
public class CurrencyRatesService {

    private LocalizationService localizationService;

    @Autowired
    public void setLocalizationService(LocalizationService localizationService) {
        this.localizationService = localizationService;
    }

    public String getFormattedCurrencyRates(Long chatId) throws IOException {
        return getFormattedCurrencyRates(chatId.toString());
    }
    public String getFormattedCurrencyRates(String chatId) throws IOException {
        CurrencyRate[] currencyRates = getCurrencyRates();

        String formattedCurrencyRates = localizationService.getDictionaryForUser(chatId).dueToNBU;

        for (CurrencyRate currencyRate : currencyRates) {
            formattedCurrencyRates = formattedCurrencyRates.concat(String.format(
                    localizationService.getDictionaryForUser(chatId).currencyRateTemplate,
                    currencyRate.getFrom(),
                    currencyRate.getSale().setScale(2, RoundingMode.HALF_EVEN),
                    currencyRate.getTo(),
                    currencyRate.getFrom(),
                    currencyRate.getBuy().setScale(2, RoundingMode.HALF_EVEN),
                    currencyRate.getTo()
            ));
        }
        return formattedCurrencyRates;
    }

    public CurrencyRate[] getCurrencyRates() throws IOException {

        return Parser.parseURL("https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5", CurrencyRate[].class);
    }
}
