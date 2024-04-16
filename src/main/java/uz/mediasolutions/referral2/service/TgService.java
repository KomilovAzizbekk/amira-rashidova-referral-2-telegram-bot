package uz.mediasolutions.referral2.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.*;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.ChatJoinRequest;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberMember;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.mediasolutions.referral2.constants.Message;
import uz.mediasolutions.referral2.entity.TgUser;
import uz.mediasolutions.referral2.entity.VideoNote;
import uz.mediasolutions.referral2.enums.StepName;
import uz.mediasolutions.referral2.repository.TgUserRepository;
import uz.mediasolutions.referral2.repository.VideoNoteRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TgService extends TelegramLongPollingBot {

    private final MakeService makeService;
    private final TgUserRepository tgUserRepository;
    private final VideoNoteRepository videoNoteRepository;

    private final String CHANNEL_ID = "-1002133049505";

    @Override
    public String getBotUsername() {
        return "AmiraRashidovaMurojaat_bot";
    }

    @Override
    public String getBotToken() {
        return "6902809898:AAEsgi3liilZ-6Gv9sdo4Gu0vyvzJ-HD0Qc";
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        String chatId = makeService.getChatId(update);
        System.out.println(update);
        if (update.hasChatJoinRequest()) {
            ChatJoinRequest request = update.getChatJoinRequest();
            ApproveChatJoinRequest approveChatJoinRequest = new ApproveChatJoinRequest();
            approveChatJoinRequest.setChatId(request.getChat().getId().toString());
            approveChatJoinRequest.setUserId(request.getUser().getId());
            execute(approveChatJoinRequest);

            execute(makeService.whenEnter(request.getUser().getId().toString()));
            execute(makeService.whenEnter2(request.getUser().getId().toString()));
            execute(makeService.whenEnter3(request.getUser().getId().toString()));
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            String msg = update.getMessage().getText();
            if (msg.equals("/start") &&
                    tgUserRepository.existsByChatId(chatId)) {
                execute(makeService.whenMenu1(update));
            } else if (msg.equals("/start") && (chatId.equals("1302908674") ||
                    chatId.equals("573761807") ||
                    chatId.equals("285710521")) &&
                    !tgUserRepository.existsByChatId(chatId)) {
                execute(makeService.whenEnter(chatId));
                execute(makeService.whenEnter2(chatId));
                execute(makeService.whenEnter3(chatId));
            } else if (msg.equals("/start") &&
                    !tgUserRepository.existsByChatId(chatId)) {
                execute(makeService.whenNotUser(update));
            } else if (msg.equals("/post")) {
                execute(makeService.whenPost(update));
            } else if (makeService.getUserStep(chatId).equals(StepName.POST) &&
                    msg.equals(makeService.getMessage(Message.BACK))) {
                deleteMessage(update);
                execute(makeService.whenMenu1(update));
            } else if (makeService.getUserStep(chatId).equals(StepName.POST) &&
                    !msg.equals(makeService.getMessage(Message.BACK))) {
                whenPostText(update);
                deleteMessage(update);
                execute(makeService.whenMenu1(update));
            }
        } else if (update.hasCallbackQuery()) {
            String data = update.getCallbackQuery().getData();
            if (data.equals("menu")) {
                execute(makeService.whenMenu(update));
            } else if (data.equals("invite")) {
                execute(whenInvite(update));
            }
        } else if (update.hasMessage() && update.getMessage().hasDocument()) {
            if (makeService.getUserStep(chatId).equals(StepName.POST)) {
                whenPostDocument(update);
                deleteMessage(update);
                execute(makeService.whenMenu1(update));
            }
        } else if (update.hasMessage() && update.getMessage().hasPhoto()) {
            if (makeService.getUserStep(chatId).equals(StepName.POST)) {
                whenPostPhoto(update);
                deleteMessage(update);
                execute(makeService.whenMenu1(update));
            }
        } else if (update.hasMessage() && update.getMessage().hasAudio()) {
            if (makeService.getUserStep(chatId).equals(StepName.POST)) {
                whenPostAudio(update);
                deleteMessage(update);
                execute(makeService.whenMenu1(update));
            }
        } else if (update.hasMessage() && update.getMessage().hasVideo()) {
            if (makeService.getUserStep(chatId).equals(StepName.POST)) {
                whenPostVideo(update);
                deleteMessage(update);
                execute(makeService.whenMenu1(update));
            }
        } else if (update.hasMessage() && update.getMessage().hasVoice()) {
            if (makeService.getUserStep(chatId).equals(StepName.POST)) {
                whenPostVoice(update);
                deleteMessage(update);
                execute(makeService.whenMenu1(update));
            }
        } else if (update.hasMessage() && update.getMessage().hasVideoNote()) {
            if (makeService.getUserStep(chatId).equals(StepName.POST)) {
                whenSaveVideoNote(update);
                deleteMessage(update);
                execute(makeService.whenMenu1(update));
            }
        }
    }

    public void deleteMessage(Update update) throws TelegramApiException {
        SendMessage sendMessageRemove = new SendMessage();
        sendMessageRemove.setChatId(update.getMessage().getChatId().toString());
        sendMessageRemove.setText(".");
        sendMessageRemove.setReplyMarkup(new ReplyKeyboardRemove(true));
        org.telegram.telegrambots.meta.api.objects.Message message = execute(sendMessageRemove);
        DeleteMessage deleteMessage = new DeleteMessage(update.getMessage().getChatId().toString(), message.getMessageId());
        execute(deleteMessage);
    }

    private String generateInviteLink() throws TelegramApiException {
        ExportChatInviteLink link = new ExportChatInviteLink("-1002133049505");
        System.out.println(link);
        String execute = execute(link);
        System.out.println(execute);
        return execute;
    }

    public EditMessageText whenInvite(Update update) throws TelegramApiException {
        String chatId = makeService.getChatId(update);
        TgUser user = tgUserRepository.findByChatId(chatId);
        String link = generateInviteLink();

        user.setInviteLink(link);
        tgUserRepository.save(user);

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setText(String.format(makeService.getMessage(Message.LINK_MESSAGE), link));
        editMessageText.setReplyMarkup(forInvite());
        editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        return editMessageText;
    }

    private InlineKeyboardMarkup forInvite() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText(makeService.getMessage(Message.MENU));
        button1.setCallbackData("menu");

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(button1);
        rowsInline.add(row1);
        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }

    @SneakyThrows
    public void whenPostText(Update update) {
        String chatId = makeService.getChatId(update);

        List<TgUser> users = tgUserRepository.findAll();
        for (TgUser user : users) {
            if (!Objects.equals(user.getChatId(), chatId))
                execute(new SendMessage(user.getChatId(), update.getMessage().getText()));
        }
        execute(new SendMessage(chatId,
                String.format(makeService.getMessage(Message.POST_SENT), users.size() - 1)));
    }

    @SneakyThrows
    public void whenPostDocument(Update update) {
        String fileId1 = update.getMessage().getDocument().getFileId();
        String caption = update.getMessage().getCaption();
        String chatId = makeService.getChatId(update);

        SendDocument sendDocument = new SendDocument();
        List<TgUser> users = tgUserRepository.findAll();
        for (TgUser user : users) {
            if (!Objects.equals(user.getChatId(), chatId)) {
                sendDocument.setChatId(user.getChatId());
                sendDocument.setDocument(new InputFile(fileId1));
                if (caption != null)
                    sendDocument.setCaption(caption);
                execute(sendDocument);
            }
        }
        execute(new SendMessage(chatId,
                String.format(makeService.getMessage(Message.POST_SENT), users.size() - 1)));
    }

    @SneakyThrows
    public void whenPostPhoto(Update update) {
        String chatId = makeService.getChatId(update);
        String fileId1 = update.getMessage().getPhoto().get(0).getFileId();
        String caption = update.getMessage().getCaption();
        SendPhoto sendPhoto = new SendPhoto();
        List<TgUser> users = tgUserRepository.findAll();
        for (TgUser user : users) {
            if (!Objects.equals(user.getChatId(), chatId)) {
                sendPhoto.setChatId(user.getChatId());
                sendPhoto.setPhoto(new InputFile(fileId1));
                if (caption != null)
                    sendPhoto.setCaption(caption);
                execute(sendPhoto);
            }
        }
        execute(new SendMessage(chatId,
                String.format(makeService.getMessage(Message.POST_SENT), users.size() - 1)));
    }

    @SneakyThrows
    public void whenPostAudio(Update update) {
        String chatId = makeService.getChatId(update);
        String fileId1 = update.getMessage().getAudio().getFileId();
        String caption = update.getMessage().getCaption();
        SendAudio sendAudio = new SendAudio();
        List<TgUser> users = tgUserRepository.findAll();
        for (TgUser user : users) {
            if (!Objects.equals(user.getChatId(), chatId)) {
                sendAudio.setChatId(user.getChatId());
                sendAudio.setAudio(new InputFile(fileId1));
                if (caption != null)
                    sendAudio.setCaption(caption);
                execute(sendAudio);
            }
        }
        execute(new SendMessage(chatId,
                String.format(makeService.getMessage(Message.POST_SENT), users.size() - 1)));
    }

    @SneakyThrows
    public void whenPostVideo(Update update) {
        String chatId = makeService.getChatId(update);
        String fileId1 = update.getMessage().getVideo().getFileId();
        String caption = update.getMessage().getCaption();
        SendVideo sendVideo = new SendVideo();
        List<TgUser> users = tgUserRepository.findAll();
        for (TgUser user : users) {
            if (!Objects.equals(user.getChatId(), chatId)) {
                sendVideo.setChatId(user.getChatId());
                sendVideo.setVideo(new InputFile(fileId1));
                if (caption != null)
                    sendVideo.setCaption(caption);
                execute(sendVideo);
            }
        }
        execute(new SendMessage(chatId,
                String.format(makeService.getMessage(Message.POST_SENT), users.size() - 1)));
    }

    @SneakyThrows
    public void whenPostVoice(Update update) {
        String chatId = makeService.getChatId(update);
        String fileId1 = update.getMessage().getVoice().getFileId();
        String caption = update.getMessage().getCaption();
        SendVoice sendVoice = new SendVoice();
        List<TgUser> users = tgUserRepository.findAll();
        for (TgUser user : users) {
            if (!Objects.equals(user.getChatId(), chatId)) {
                sendVoice.setChatId(user.getChatId());
                sendVoice.setVoice(new InputFile(fileId1));
                if (caption != null)
                    sendVoice.setCaption(caption);
                execute(sendVoice);
            }
        }
        execute(new SendMessage(chatId,
                String.format(makeService.getMessage(Message.POST_SENT), users.size() - 1)));
    }

    @SneakyThrows
    private void whenSaveVideoNote(Update update) {
        String chatId = makeService.getChatId(update);
        String fileId = update.getMessage().getVideoNote().getFileId();
        Optional<VideoNote> optional = videoNoteRepository.findById(1L);
        if (optional.isEmpty()) {
            VideoNote videoNote = VideoNote.builder()
                    .id(1L)
                    .fileId(fileId)
                    .build();
            videoNoteRepository.save(videoNote);
        } else {
            VideoNote videoNote = optional.get();
            videoNote.setFileId(fileId);
            videoNoteRepository.save(videoNote);
        }
        execute(new SendMessage(chatId,
                makeService.getMessage(Message.SAVED)));
    }

}
