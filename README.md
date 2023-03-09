# YevheniiJavaBot
This is repsitory for Telegram bot, created by Java Spring boot applacation


## DEBUG (Windows)

1. Uncomment and reinstall dependencies under the `<!-- DEV  -->` in `pom.xml` in `<dependencies>` section
2. Run ngrok console
3. Add ngrok token (if it's not already setup)

    `ngrok config add-authtoken <your ngrok auth-token>`
4. Run ngrok on port **8084**

    `ngrok http 8084`
5. Set-up telegram webhook:     
`https://api.telegram.org/bot<telegram bot token>/setWebhook?url=<ngrok API endpoint>`
6. Add system (environment )variables `telegrambotUsername`, `telegrambotToken` and `telegrambotWebhookURI` with telegram bot name, telegram bot token and ngrok API endpoint URI. (Or You can temporary change those variables in file `application.properties` but don't forget REMOVE values for PROD)
7. Run the project (better after `mvn clean` to refresh profile settings)
8. Try send message from telegram bot


## BUILD PROD (AWS)
1. Comment and reinstall dependencies under the `<!-- DEV  -->` in `pom.xml` in `<dependencies>` section
2. If you had changed `telegrambotUsername`, `telegrambotToken` and `telegrambotWebhookURI` variables in file `application.properties` -- REMOVE values (change with `no-value`)
3. Set-up telegram webhook:     
`https://api.telegram.org/bot<telegram bot token>/setWebhook?url=<AWS Lambda API endpoint>`
4. build the project `mvn clean package -P prod`
5. Take the `yevheniiJavaBot-<your current version>-SNAPSHOT-aws.jar` from `target` folder 
6. import the `.jar` file to your AWS lambda function
7. Set-up AWS environment variables`telegrambotUsername`, `telegrambotToken` and `telegrambotWebhookURI` with telegram bot name, telegram bot token and AWS API endpoint URI  (AWS Lambda → Configuration → Environment variables). 
8. Try send message from telegram bot
