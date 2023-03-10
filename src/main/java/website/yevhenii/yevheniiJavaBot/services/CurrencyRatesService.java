package website.yevhenii.yevheniiJavaBot.services;

import org.springframework.stereotype.Service;
import website.yevhenii.yevheniiJavaBot.entities.CurrencyRate;

import java.io.IOException;
import java.math.RoundingMode;

@Service
public class CurrencyRatesService {

    public String getFormattedCurrencyRates() throws IOException {
        CurrencyRate[] currencyRates = getCurrencyRates();
        String formattedCurrencyRates = "Due to National Bank of Ukraine: \n\n";

        for (CurrencyRate currencyRate : currencyRates) {
            formattedCurrencyRates = formattedCurrencyRates.concat(String.format(
                    "You can *buy* 1 _%s_ for *%s %s* and *sale* 1 _%s_ for *%s %s*;\n",
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
