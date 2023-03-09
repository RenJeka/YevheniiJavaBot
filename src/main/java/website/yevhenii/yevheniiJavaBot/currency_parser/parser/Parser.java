package website.yevhenii.yevheniiJavaBot.currency_parser.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import website.yevhenii.yevheniiJavaBot.currency_parser.entity.CurrencyRate;

import java.io.IOException;
import java.math.RoundingMode;
import java.net.URL;


/**
 * Created by Yevhenii Petrushenko.
 */
public class Parser {

    public static String getFormattedCurrencyRates() throws IOException {
        CurrencyRate[] currencyRates = parseCurrencyRates();
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

    private static CurrencyRate[] parseCurrencyRates() throws IOException {
        final String NBU_CURRENCY_RATE_API_URL = "https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5";

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(new URL(NBU_CURRENCY_RATE_API_URL), CurrencyRate[].class);
    }
}
