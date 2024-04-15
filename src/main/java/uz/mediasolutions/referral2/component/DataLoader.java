package uz.mediasolutions.referral2.component;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import uz.mediasolutions.referral2.entity.LanguagePs;
import uz.mediasolutions.referral2.entity.LanguageSourcePs;
import uz.mediasolutions.referral2.entity.Step;
import uz.mediasolutions.referral2.enums.StepName;
import uz.mediasolutions.referral2.repository.LanguageRepositoryPs;
import uz.mediasolutions.referral2.repository.LanguageSourceRepositoryPs;
import uz.mediasolutions.referral2.repository.StepRepository;
import uz.mediasolutions.referral2.service.TgService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final ApplicationContext applicationContext;
    private final LanguageSourceRepositoryPs languageSourceRepositoryPs;
    private final LanguageRepositoryPs languageRepositoryPs;
    private final StepRepository stepRepository;

    @Value("${spring.sql.init.mode}")
    private String mode;

    @Override
    public void run(String... args) throws Exception {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            TgService tgService = applicationContext.getBean(TgService.class);
            telegramBotsApi.registerBot(tgService);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }

        if (mode.equals("always")) {
            addSteps();
            addUzLangValues();
        }
    }

    public void addSteps() {
        for (StepName value : StepName.values()) {
            Step step = Step.builder().name(value).build();
            stepRepository.save(step);
        }
    }

    public void addUzLangValues() throws IOException {
        Properties properties = new Properties();
        try (InputStream input = DataLoader.class.getClassLoader()
                .getResourceAsStream("messages_uz.properties")) {
            properties.load(input);
        }
        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            LanguagePs ps = LanguagePs.builder().primaryLang("UZ").key(key).build();
            LanguagePs save = languageRepositoryPs.save(ps);
            LanguageSourcePs sourcePs = LanguageSourcePs.builder()
                    .languagePs(save).language("UZ").translation(value).build();
            languageSourceRepositoryPs.save(sourcePs);
        }
    }
}
