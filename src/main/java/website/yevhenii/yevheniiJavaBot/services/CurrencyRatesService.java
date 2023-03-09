package website.yevhenii.yevheniiJavaBot.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import website.yevhenii.yevheniiJavaBot.currency_parser.entity.CurrencyRate;
import website.yevhenii.yevheniiJavaBot.currency_parser.parser.Parser;

import java.io.IOException;
import java.math.RoundingMode;
import java.net.URL;

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
