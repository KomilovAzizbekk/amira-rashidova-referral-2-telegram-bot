package uz.mediasolutions.referral2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.groupadministration.SetChatPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideoNote;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.mediasolutions.referral2.utills.constants.Message;
import uz.mediasolutions.referral2.entity.LanguagePs;
import uz.mediasolutions.referral2.entity.LanguageSourcePs;
import uz.mediasolutions.referral2.entity.TgUser;
import uz.mediasolutions.referral2.entity.VideoNote;
import uz.mediasolutions.referral2.enums.StepName;
import uz.mediasolutions.referral2.repository.LanguageRepositoryPs;
import uz.mediasolutions.referral2.repository.StepRepository;
import uz.mediasolutions.referral2.repository.TgUserRepository;
import uz.mediasolutions.referral2.repository.VideoNoteRepository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MakeService {

    private final TgUserRepository tgUserRepository;
    private final StepRepository stepRepository;
    private final LanguageRepositoryPs languageRepositoryPs;
    private final VideoNoteRepository videoNoteRepository;

    public void setUserStep(String chatId, StepName stepName) {
        TgUser tgUser = tgUserRepository.findByChatId(chatId);
        tgUser.setStep(stepRepository.findByName(stepName));
        tgUserRepository.save(tgUser);
    }

    public StepName getUserStep(String chatId) {
        TgUser tgUser = tgUserRepository.findByChatId(chatId);
        return tgUser.getStep().getName();
    }

    public String getChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChatId().toString();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getChatId().toString();
        }
        return "";
    }

    public String getUsername(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getFrom().getUserName();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom().getUserName();
        }
        return "";
    }

    public String getFirstName(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getFrom().getFirstName();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom().getFirstName();
        }
        return "";
    }

    public String getMessage(String key) {
        List<LanguagePs> allByLanguage = languageRepositoryPs.findAll();
        if (!allByLanguage.isEmpty()) {
            for (LanguagePs languagePs : allByLanguage) {
                for (LanguageSourcePs languageSourceP : languagePs.getLanguageSourcePs()) {
                    if (languageSourceP.getTranslation() != null &&
                            languageSourceP.getLanguage().equals("UZ") &&
                            languagePs.getKey().equals(key)) {
                        return languageSourceP.getTranslation();
                    }
                }
            }
        }
        return null;
    }

    public SendMessage whenPost(Update update) {
        String chatId = getChatId(update);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

//        if (Objects.equals(chatId, "285710521") || Objects.equals(chatId, "6931160281")
//                || Objects.equals(chatId, "1302908674")) {
        if (!tgUserRepository.existsByChatId(chatId)) {
            TgUser user = TgUser.builder()
                    .chatId(chatId)
                    .name(getUsername(update) != null ? getUsername(update) : getFirstName(update))
                    .step(stepRepository.findByName(StepName.POST))
                    .build();
            tgUserRepository.save(user);
        } else {
            setUserStep(chatId, StepName.POST);
        }
        sendMessage.setText(getMessage(Message.POST));
        sendMessage.enableHtml(true);
        sendMessage.setReplyMarkup(forPost());
        return sendMessage;
//        } else {
//            sendMessage.setText(getMessage(Message.YOU_HAVE_NOT_ADMIN_RIGHTS));
//            return sendMessage;
//        }
    }

    private ReplyKeyboardMarkup forPost() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rowList = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();

        KeyboardButton button1 = new KeyboardButton();
        button1.setText(getMessage(Message.BACK));

        row1.add(button1);

        rowList.add(row1);
        markup.setKeyboard(rowList);
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        return markup;
    }

    public SendMessage whenMenu1(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(String.format(getMessage(Message.MENU_MSG)));
        sendMessage.setReplyMarkup(forMenu());
        sendMessage.enableHtml(true);
        setUserStep(chatId, StepName.CHOOSE_FROM_MENU);
        return sendMessage;
    }

    public EditMessageText whenMenu(Update update) {
        String chatId = getChatId(update);
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setText(String.format(getMessage(Message.MENU_MSG)));
        editMessageText.setReplyMarkup(forMenu());
        editMessageText.enableHtml(true);
        editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        setUserStep(chatId, StepName.CHOOSE_FROM_MENU);
        return editMessageText;
    }

    private InlineKeyboardMarkup forMenu() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        InlineKeyboardButton button3 = new InlineKeyboardButton();
        InlineKeyboardButton button4 = new InlineKeyboardButton();

        button1.setText(getMessage(Message.CHALLENGE));
        button2.setText(getMessage(Message.INVITE));
        button3.setText(getMessage(Message.PRIZE_LIST));
        button4.setText(getMessage(Message.REACH_TO_ADMIN));

        button1.setUrl(getMessage(Message.TELEGRAPH_URL_FOR_CHALLENGE));
        button2.setCallbackData("invite");
        button3.setUrl(getMessage(Message.TELEGRAPH_LINK_FOR_PRIZE_LIST));
        button4.setUrl(getMessage(Message.ADMIN_USERNAME));

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        List<InlineKeyboardButton> row3 = new ArrayList<>();

        row1.add(button1);
        row2.add(button2);
        row3.add(button3);
        row3.add(button4);

        rowsInline.add(row1);
        rowsInline.add(row2);
        rowsInline.add(row3);

        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }

    public SendMessage whenEnter(Update update) {
        String chatId = update.getChatJoinRequest().getUser().getId().toString();
        if (!tgUserRepository.existsByChatId(chatId)) {
            TgUser user = TgUser.builder()
                    .chatId(chatId)
                    .name(getUsername(update) != null ? getUsername(update) : getFirstName(update))
                    .build();
            tgUserRepository.save(user);
        }
        return new SendMessage(chatId,
                getMessage(Message.WHEN_JOINED_MESSAGE));
    }

    public SendVideoNote whenEnter2(String chatId) {

        Optional<VideoNote> optional = videoNoteRepository.findById(1L);
        if (optional.isPresent()) {
            setUserStep(chatId, StepName.STEP_1);
            SendVideoNote sendVideoNote = new SendVideoNote();
            sendVideoNote.setChatId(chatId);
            sendVideoNote.setVideoNote(new InputFile(optional.get().getFileId()));

            return sendVideoNote;
        }
        return new SendVideoNote();
    }

    public SendMessage whenNotUser(Update update) {
        String chatId = getChatId(update);
        SendMessage sendMessage = new SendMessage(chatId,
                getMessage(Message.ERROR));
        sendMessage.setReplyMarkup(forNotUser());
        return sendMessage;
    }

    private InlineKeyboardMarkup forNotUser() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText(getMessage(Message.REDIRECT_TO_CHANNEL));
        button1.setUrl(getMessage(Message.CHANNEL_URL_REQUEST));
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(button1);
        rowsInline.add(row1);
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    public SendMessage whenPostChannel(Update update) {
        String chatId = getChatId(update);
        SendMessage sendMessage = new SendMessage(chatId,
                getMessage(Message.POST_TYPE));
        sendMessage.setReplyMarkup(forPost());
        sendMessage.enableHtml(true);
        setUserStep(chatId, StepName.POST_CHANNEL);
        return sendMessage;
    }
}
