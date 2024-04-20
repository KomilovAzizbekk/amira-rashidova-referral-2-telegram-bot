package uz.mediasolutions.referral2.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.*;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.mediasolutions.referral2.entity.MessageCount;
import uz.mediasolutions.referral2.entity.Reaction;
import uz.mediasolutions.referral2.repository.MessageCountRepository;
import uz.mediasolutions.referral2.repository.ReactionRepository;
import uz.mediasolutions.referral2.utills.constants.Message;
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
    private final MessageCountRepository messageCountRepository;
    private final ReactionRepository reactionRepository;

    //    private final String CHANNEL_ID = "-1002125526955";
    private final String CHANNEL_ID = "-1001903287909";

    //    private final String GROUP_ID = "-1002083009671";
    private final String GROUP_ID = "-1002011329029";

    @Override
    public String getBotUsername() {
//        return "AmiraRashidovaMurojaat_bot";
        return "sakaka_bot";
    }

    @Override
    public String getBotToken() {
//        return "6902809898:AAEsgi3liilZ-6Gv9sdo4Gu0vyvzJ-HD0Qc";
        return "6052104473:AAEscLILevwPMcG_00PYqAf-Kpb7eIUCIGg";
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        String chatId = makeService.getChatId(update);
        System.out.println(update);
        if (update.hasMessage() && update.getMessage().getChat().getId().toString().equals(GROUP_ID)) {
            System.out.println("lllllllll");
            if (!update.getMessage().getFrom().getFirstName().equals("Telegram") &&
                    tgUserRepository.existsByChatId(update.getMessage().getFrom().getId().toString())) {
                TgUser user = tgUserRepository.findByChatId(update.getMessage().getFrom().getId().toString());
                System.out.println(user.getChatId());
                user.setComments(user.getComments() + 1);
                user.setPoints(user.getPoints() + 1);
                tgUserRepository.save(user);
            }
        }
        else if (update.hasChatJoinRequest()) {
            ChatJoinRequest request = update.getChatJoinRequest();
            String inviteLink = request.getInviteLink().getInviteLink();
            TgUser user = tgUserRepository.findByInviteLink(inviteLink);
            if (user != null) {
                user.setInvitedPeople(user.getInvitedPeople() + 1);
                TgUser save = tgUserRepository.save(user);
                if (save.getInvitedPeople() == 5) {
                    ApproveChatJoinRequest approveChatJoinRequest = new ApproveChatJoinRequest();
                    approveChatJoinRequest.setChatId(request.getChat().getId().toString());
                    approveChatJoinRequest.setUserId(Long.valueOf(user.getChatId()));
                    execute(approveChatJoinRequest);
                    execute(makeService.whenMenu1(user.getChatId()));
                }
            }

            execute(makeService.whenEnter(update));
            if (videoNoteRepository.existsById(1L))
                execute(makeService.whenEnter2(request.getUser().getId().toString()));
            execute(whenEnter3(request.getUser().getId().toString()));
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            String msg = update.getMessage().getText();
            if (msg.equals("/start") &&
                    tgUserRepository.existsByChatId(chatId) &&
                    tgUserRepository.findByChatId(chatId).getInvitedPeople() >= 5) {
                execute(makeService.whenMenu1(chatId));
            } else if (msg.equals("/start") && (chatId.equals("1302908674") ||
                    chatId.equals("573761807") ||
                    chatId.equals("285710521"))) {
                if (!tgUserRepository.existsByChatId(chatId)) {
                    execute(makeService.whenEnter(update));
                    if (videoNoteRepository.existsById(1L))
                        execute(makeService.whenEnter2(chatId));
                    execute(makeService.whenMenu1(chatId));
                } else {
                    execute(makeService.whenMenu1(chatId));
                }
            } else if (msg.equals("/start") &&
                    !tgUserRepository.existsByChatId(chatId)) {
                if (check(update)) {
                    execute(makeService.whenEnter(update));
                    if (videoNoteRepository.existsById(1L))
                        execute(makeService.whenEnter2(chatId));
                    execute(whenEnter3(chatId));
                } else {
                    execute(makeService.whenNotUser(update));
                }
            } else if (msg.equals("/post")) {
                execute(makeService.whenPost(update));
            } else if (msg.equals("/post_channel")) {
                execute(makeService.whenPostChannel(update));
            } else if (makeService.getUserStep(chatId).equals(StepName.POST) &&
                    msg.equals(makeService.getMessage(Message.BACK))) {
                deleteMessage(update);
                execute(makeService.whenMenu1(chatId));
            } else if (makeService.getUserStep(chatId).equals(StepName.POST) &&
                    !msg.equals(makeService.getMessage(Message.BACK))) {
                whenPostText(update);
                deleteMessage(update);
                execute(makeService.whenMenu1(chatId));
            } else if (makeService.getUserStep(chatId).equals(StepName.POST_CHANNEL) &&
                    msg.equals(makeService.getMessage(Message.BACK))) {
                deleteMessage(update);
                execute(makeService.whenMenu1(chatId));
            } else if (makeService.getUserStep(chatId).equals(StepName.POST_CHANNEL) &&
                    !msg.equals(makeService.getMessage(Message.BACK))) {
                whenPostChannelText(update);
                deleteMessage(update);
                execute(makeService.whenMenu1(chatId));
            }
        } else if (update.hasCallbackQuery()) {
            String data = update.getCallbackQuery().getData();
            if (data.equals("menu")) {
                execute(makeService.whenMenu(update));
            } else if (data.equals("invite")) {
                execute(whenInvite(update));
            } else if (data.equals("heart")) {
                whenClickReaction(update, "heart");
            } else if (data.equals("fire")) {
                whenClickReaction(update, "fire");
            } else if (data.equals("like")) {
                whenClickReaction(update, "like");
            } else if (data.equals("eyes")) {
                whenClickReaction(update, "eyes");
            } else if (data.equals("hundred")) {
                whenClickReaction(update, "hundred");
            }
        } else if (update.hasMessage() && update.getMessage().hasDocument()) {
            if (makeService.getUserStep(chatId).equals(StepName.POST)) {
                whenPostDocument(update);
                deleteMessage(update);
                execute(makeService.whenMenu1(chatId));
            } else if (makeService.getUserStep(chatId).equals(StepName.POST_CHANNEL)) {
                whenPostChannelDocument(update);
                deleteMessage(update);
                execute(makeService.whenMenu1(chatId));
            }
        } else if (update.hasMessage() && update.getMessage().hasPhoto()) {
            if (makeService.getUserStep(chatId).equals(StepName.POST)) {
                whenPostPhoto(update);
                deleteMessage(update);
                execute(makeService.whenMenu1(chatId));
            } else if (makeService.getUserStep(chatId).equals(StepName.POST_CHANNEL)) {
                whenPostChannelPhoto(update);
                deleteMessage(update);
                execute(makeService.whenMenu1(chatId));
            }
        } else if (update.hasMessage() && update.getMessage().hasAudio()) {
            if (makeService.getUserStep(chatId).equals(StepName.POST)) {
                whenPostAudio(update);
                deleteMessage(update);
                execute(makeService.whenMenu1(chatId));
            } else if (makeService.getUserStep(chatId).equals(StepName.POST_CHANNEL)) {
                whenPostChannelAudio(update);
                deleteMessage(update);
                execute(makeService.whenMenu1(chatId));
            }
        } else if (update.hasMessage() && update.getMessage().hasVideo()) {
            if (makeService.getUserStep(chatId).equals(StepName.POST)) {
                whenPostVideo(update);
                deleteMessage(update);
                execute(makeService.whenMenu1(chatId));
            } else if (makeService.getUserStep(chatId).equals(StepName.POST_CHANNEL)) {
                whenPostChannelVideo(update);
                deleteMessage(update);
                execute(makeService.whenMenu1(chatId));
            }
        } else if (update.hasMessage() && update.getMessage().hasVoice()) {
            if (makeService.getUserStep(chatId).equals(StepName.POST)) {
                whenPostVoice(update);
                deleteMessage(update);
                execute(makeService.whenMenu1(chatId));
            } else if (makeService.getUserStep(chatId).equals(StepName.POST_CHANNEL)) {
                whenPostChannelVoice(update);
                deleteMessage(update);
                execute(makeService.whenMenu1(chatId));
            }
        } else if (update.hasMessage() && update.getMessage().hasVideoNote()) {
            if (makeService.getUserStep(chatId).equals(StepName.POST)) {
                whenSaveVideoNote(update);
                deleteMessage(update);
                execute(makeService.whenMenu1(chatId));
            } else if (makeService.getUserStep(chatId).equals(StepName.POST_CHANNEL)) {
                whenPostChannelVideoNote(update);
                deleteMessage(update);
                execute(makeService.whenMenu1(chatId));
            }
        }
    }

    private void whenClickReaction(Update update, String name) throws TelegramApiException {
        String chatId = update.getCallbackQuery().getFrom().getId().toString();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        MessageCount messageCount = messageCountRepository.findByMessageId(messageId);
        List<Reaction> reactionList = messageCount.getReactions();
        if (!tgUserRepository.existsByChatId(chatId)) {

            if (reactionRepository.existsByMessageIdAndChatId(messageId, chatId)) {
                assert reactionList != null;
                for (Reaction reaction : reactionList) {
                    if (Objects.equals(reaction.getChatId(), chatId)) {
                        String nameMinus = reaction.getName();
                        messageCount = minusFromReaction(nameMinus, name, messageId);
                        reaction.setName(name);
                        reactionRepository.save(reaction);
                    }
                }
            } else {

                Reaction save = reactionRepository.save(new Reaction(name, messageId, chatId));
                reactionList.add(save);

                messageCount = plusReaction(name, messageId);
                messageCount.setReactions(reactionList);
                messageCountRepository.save(messageCount);
            }
        } else {
            TgUser user = tgUserRepository.findByChatId(chatId);

            if (reactionRepository.existsByMessageIdAndUserChatId(messageId, chatId)) {
                assert reactionList != null;
                for (Reaction reaction : reactionList) {
                    if (Objects.equals(reaction.getChatId(), user.getChatId())) {
                        String nameMinus = reaction.getName();
                        messageCount = minusFromReaction(nameMinus, name, messageId);
                        reaction.setName(name);
                        reactionRepository.save(reaction);
                    }
                }
            } else {
                user.setReactions(user.getReactions() + 1);
                user.setPoints(user.getPoints() + 3);
                tgUserRepository.save(user);

                Reaction save = reactionRepository.save(new Reaction(name, messageId, user, chatId));
                reactionList.add(save);
                messageCount = plusReaction(name, messageId);
                messageCount.setReactions(reactionList);
                messageCountRepository.save(messageCount);
            }
        }
        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setMessageId(messageId);
        editMessageReplyMarkup.setChatId(CHANNEL_ID);
        editMessageReplyMarkup.setReplyMarkup(forPostChannel1(messageCount));
        execute(editMessageReplyMarkup);
    }

    public MessageCount plusReaction(String namePlus, Integer messageId) {
        MessageCount messageCount = messageCountRepository.findByMessageId(messageId);
        if (Objects.equals(namePlus, "heart"))
            messageCount.setHeartCount(messageCount.getHeartCount() + 1);
        else if (Objects.equals(namePlus, "fire"))
            messageCount.setFireCount(messageCount.getFireCount() + 1);
        else if (Objects.equals(namePlus, "like"))
            messageCount.setLikeCount(messageCount.getLikeCount() + 1);
        else if (Objects.equals(namePlus, "eyes"))
            messageCount.setEyesCount(messageCount.getEyesCount() + 1);
        else if (Objects.equals(namePlus, "hundred"))
            messageCount.setHundredCount(messageCount.getHundredCount() + 1);
        return messageCountRepository.save(messageCount);
    }

    public MessageCount minusFromReaction(String nameMinus, String namePlus, Integer messageId) {
        MessageCount messageCount = messageCountRepository.findByMessageId(messageId);
        if (!messageCount.isDoubleClick()) {
            if (Objects.equals(nameMinus, "heart"))
                messageCount.setHeartCount(messageCount.getHeartCount() - 1);
            else if (Objects.equals(nameMinus, "fire"))
                messageCount.setFireCount(messageCount.getFireCount() - 1);
            else if (Objects.equals(nameMinus, "like"))
                messageCount.setLikeCount(messageCount.getLikeCount() - 1);
            else if (Objects.equals(nameMinus, "eyes"))
                messageCount.setEyesCount(messageCount.getEyesCount() - 1);
            else if (Objects.equals(nameMinus, "hundred"))
                messageCount.setHundredCount(messageCount.getHundredCount() - 1);
        }
        MessageCount save = messageCountRepository.save(messageCount);

        if (!nameMinus.equals(namePlus) || save.isDoubleClick()) {
            if (Objects.equals(namePlus, "heart"))
                save.setHeartCount(save.getHeartCount() + 1);
            else if (Objects.equals(namePlus, "fire"))
                save.setFireCount(save.getFireCount() + 1);
            else if (Objects.equals(namePlus, "like"))
                save.setLikeCount(save.getLikeCount() + 1);
            else if (Objects.equals(namePlus, "eyes"))
                save.setEyesCount(save.getEyesCount() + 1);
            else if (Objects.equals(namePlus, "hundred"))
                save.setHundredCount(save.getHundredCount() + 1);
            save.setDoubleClick(false);
        } else {
            save.setDoubleClick(true);
        }
        return messageCountRepository.save(save);
    }

    private void whenPostChannelVideoNote(Update update) throws TelegramApiException {
        String chatId = makeService.getChatId(update);
        String fileId = update.getMessage().getVideoNote().getFileId();

        SendVideoNote sendVideoNote = new SendVideoNote();
        sendVideoNote.setChatId(CHANNEL_ID);
        sendVideoNote.setVideoNote(new InputFile(fileId));
        sendVideoNote.setReplyMarkup(forPostChannel());
        org.telegram.telegrambots.meta.api.objects.Message message = execute(sendVideoNote);

        messageCountRepository.save(new MessageCount(message.getMessageId(), 0, 0, 0, 0, 0));

        SendMessage sendMessage1 = new SendMessage(chatId, makeService.getMessage(Message.CHANNEL_POST_SENT));
        execute(sendMessage1);
    }

    private void whenPostChannelVoice(Update update) throws TelegramApiException {
        String chatId = makeService.getChatId(update);
        String fileId = update.getMessage().getVoice().getFileId();
        String caption = update.getMessage().getCaption();

        SendVoice sendVoice = new SendVoice();
        sendVoice.setChatId(CHANNEL_ID);
        sendVoice.setVoice(new InputFile(fileId));
        if (caption != null) {
            sendVoice.setCaption(caption);
        }
        sendVoice.setReplyMarkup(forPostChannel());
        org.telegram.telegrambots.meta.api.objects.Message message = execute(sendVoice);

        messageCountRepository.save(new MessageCount(message.getMessageId(), 0, 0, 0, 0, 0));

        SendMessage sendMessage1 = new SendMessage(chatId, makeService.getMessage(Message.CHANNEL_POST_SENT));
        execute(sendMessage1);
    }

    private void whenPostChannelVideo(Update update) throws TelegramApiException {
        String chatId = makeService.getChatId(update);
        String fileId = update.getMessage().getVideo().getFileId();
        String caption = update.getMessage().getCaption();

        SendVideo sendVideo = new SendVideo();
        sendVideo.setChatId(CHANNEL_ID);
        sendVideo.setVideo(new InputFile(fileId));
        if (caption != null) {
            sendVideo.setCaption(caption);
        }
        sendVideo.setReplyMarkup(forPostChannel());
        org.telegram.telegrambots.meta.api.objects.Message message = execute(sendVideo);

        messageCountRepository.save(new MessageCount(message.getMessageId(), 0, 0, 0, 0, 0));

        SendMessage sendMessage1 = new SendMessage(chatId, makeService.getMessage(Message.CHANNEL_POST_SENT));
        execute(sendMessage1);
    }

    private void whenPostChannelAudio(Update update) throws TelegramApiException {
        String chatId = makeService.getChatId(update);
        String fileId = update.getMessage().getAudio().getFileId();
        String caption = update.getMessage().getCaption();

        SendAudio sendAudio = new SendAudio();
        sendAudio.setChatId(CHANNEL_ID);
        sendAudio.setAudio(new InputFile(fileId));
        if (caption != null) {
            sendAudio.setCaption(caption);
        }
        sendAudio.setReplyMarkup(forPostChannel());
        org.telegram.telegrambots.meta.api.objects.Message message = execute(sendAudio);

        messageCountRepository.save(new MessageCount(message.getMessageId(), 0, 0, 0, 0, 0));

        SendMessage sendMessage1 = new SendMessage(chatId, makeService.getMessage(Message.CHANNEL_POST_SENT));
        execute(sendMessage1);
    }

    private void whenPostChannelPhoto(Update update) throws TelegramApiException {
        String chatId = makeService.getChatId(update);
        String fileId = update.getMessage().getPhoto().get(0).getFileId();
        String caption = update.getMessage().getCaption();

        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(CHANNEL_ID);
        sendPhoto.setPhoto(new InputFile(fileId));
        if (caption != null) {
            sendPhoto.setCaption(caption);
        }
        sendPhoto.setReplyMarkup(forPostChannel());
        org.telegram.telegrambots.meta.api.objects.Message message = execute(sendPhoto);

        messageCountRepository.save(new MessageCount(message.getMessageId(), 0, 0, 0, 0, 0));

        SendMessage sendMessage1 = new SendMessage(chatId, makeService.getMessage(Message.CHANNEL_POST_SENT));
        execute(sendMessage1);
    }

    private void whenPostChannelDocument(Update update) throws TelegramApiException {
        String chatId = makeService.getChatId(update);
        String fileId = update.getMessage().getDocument().getFileId();
        String caption = update.getMessage().getCaption();

        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(CHANNEL_ID);
        sendDocument.setDocument(new InputFile(fileId));
        if (caption != null) {
            sendDocument.setCaption(caption);
        }
        sendDocument.setReplyMarkup(forPostChannel());
        org.telegram.telegrambots.meta.api.objects.Message message = execute(sendDocument);

        messageCountRepository.save(new MessageCount(message.getMessageId(), 0, 0, 0, 0, 0));

        SendMessage sendMessage1 = new SendMessage(chatId, makeService.getMessage(Message.CHANNEL_POST_SENT));
        execute(sendMessage1);
    }


    private void whenPostChannelText(Update update) throws TelegramApiException {
        String chatId = makeService.getChatId(update);
        SendMessage sendMessage = new SendMessage(CHANNEL_ID, update.getMessage().getText());
        sendMessage.setReplyMarkup(forPostChannel());
        org.telegram.telegrambots.meta.api.objects.Message message = execute(sendMessage);

        messageCountRepository.save(new MessageCount(message.getMessageId(), 0, 0, 0, 0, 0));

        SendMessage sendMessage1 = new SendMessage(chatId, makeService.getMessage(Message.CHANNEL_POST_SENT));
        execute(sendMessage1);
    }

    public InlineKeyboardMarkup forPostChannel() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        InlineKeyboardButton button3 = new InlineKeyboardButton();
        InlineKeyboardButton button4 = new InlineKeyboardButton();
        InlineKeyboardButton button5 = new InlineKeyboardButton();

        button1.setText("❤\uFE0F");
        button1.setCallbackData("heart");

        button2.setText("\uD83D\uDD25");
        button2.setCallbackData("fire");

        button3.setText("\uD83D\uDC4D");
        button3.setCallbackData("like");

        button4.setText("\uD83D\uDE0D");
        button4.setCallbackData("eyes");

        button5.setText("\uD83D\uDCAF");
        button5.setCallbackData("hundred");

        List<InlineKeyboardButton> row1 = new ArrayList<>();

        row1.add(button1);
        row1.add(button2);
        row1.add(button3);
        row1.add(button4);
        row1.add(button5);

        rowsInline.add(row1);
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    public InlineKeyboardMarkup forPostChannel1(MessageCount messageCount) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        InlineKeyboardButton button3 = new InlineKeyboardButton();
        InlineKeyboardButton button4 = new InlineKeyboardButton();
        InlineKeyboardButton button5 = new InlineKeyboardButton();

        button1.setText(messageCount.getHeartCount() != 0 ? messageCount.getHeartCount() + "❤\uFE0F" : "❤\uFE0F");
        button1.setCallbackData("heart");

        button2.setText(messageCount.getFireCount() != 0 ? messageCount.getFireCount() + "\uD83D\uDD25" : "\uD83D\uDD25");
        button2.setCallbackData("fire");

        button3.setText(messageCount.getLikeCount() != 0 ? messageCount.getLikeCount() + "\uD83D\uDC4D" : "\uD83D\uDC4D");
        button3.setCallbackData("like");

        button4.setText(messageCount.getEyesCount() != 0 ? messageCount.getEyesCount() + "\uD83D\uDE0D" : "\uD83D\uDE0D");
        button4.setCallbackData("eyes");

        button5.setText(messageCount.getHundredCount() != 0 ? messageCount.getHundredCount() + "\uD83D\uDCAF" : "\uD83D\uDCAF");
        button5.setCallbackData("hundred");

        List<InlineKeyboardButton> row1 = new ArrayList<>();

        row1.add(button1);
        row1.add(button2);
        row1.add(button3);
        row1.add(button4);
        row1.add(button5);

        rowsInline.add(row1);
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    @SneakyThrows
    public boolean check(Update update) {
        String chatId = makeService.getChatId(update);

        ChatMember member1 = getChatMember(CHANNEL_ID, update);
        ArrayList<ChatMember> admin1 = getChatAdmin(CHANNEL_ID);
        boolean checkAdmins = CheckAdmins(chatId, admin1);

        return member1.getStatus().equals("member") || checkAdmins;
    }

    private boolean CheckAdmins(String chatId, ArrayList<ChatMember> admins) {
        for (ChatMember admin : admins) {
            if (admin.getUser().getId().toString().equals(chatId)) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<ChatMember> getChatAdmin(String channelId) throws TelegramApiException {
        GetChatAdministrators getChatAdministrators = new GetChatAdministrators(channelId);
        return execute(getChatAdministrators);
    }

    private ChatMember getChatMember(String channelId, Update update) throws TelegramApiException {
        GetChatMember getChatMember = new GetChatMember(channelId,
                update.getMessage().getChatId());
        return execute(getChatMember);
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

    public SendMessage whenEnter3(String chatId) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage(chatId,
                String.format(makeService.getMessage(Message.SENT_THIS_LINK), generateInviteLink(chatId)));
        sendMessage.setReplyMarkup(forEnter(chatId));
        return sendMessage;
    }

    public InlineKeyboardMarkup forEnter(String chatId) throws TelegramApiException {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText(makeService.getMessage(Message.SHARE_LINK));
        button1.setSwitchInlineQuery(String.format(makeService.getMessage(Message.SHARING_LINK_MSG), generateInviteLink(chatId)));
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(button1);
        rowsInline.add(row1);
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    private String generateInviteLink(String chatId) throws TelegramApiException {
        TgUser user = tgUserRepository.findByChatId(chatId);
        if (user.getInviteLink() == null) {
            CreateChatInviteLink link = new CreateChatInviteLink(CHANNEL_ID);
            link.setCreatesJoinRequest(true);
            ChatInviteLink execute = execute(link);
            user.setInviteLink(execute.getInviteLink());
            tgUserRepository.save(user);
            return execute.getInviteLink();
        } else {
            return user.getInviteLink();
        }
    }

    public EditMessageText whenInvite(Update update) throws TelegramApiException {
        String chatId = makeService.getChatId(update);
        String link = generateInviteLink(chatId);

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
