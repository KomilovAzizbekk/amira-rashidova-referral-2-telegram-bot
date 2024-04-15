package uz.mediasolutions.referral2.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class TgService extends TelegramLongPollingBot {

    @Override
    public String getBotUsername() {
        return "AmiraRashidovaMurojaat_bot";
    }

    @Override
    public String getBotToken() {
        return "6902809898:AAEsgi3liilZ-6Gv9sdo4Gu0vyvzJ-HD0Qc";
    }

    @Override
    public void onUpdateReceived(Update update) {

    }
}
