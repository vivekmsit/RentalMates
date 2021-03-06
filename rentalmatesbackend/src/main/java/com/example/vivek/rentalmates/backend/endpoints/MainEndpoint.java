package com.example.vivek.rentalmates.backend.endpoints;

import com.example.vivek.rentalmates.backend.entities.AggregateData;
import com.example.vivek.rentalmates.backend.entities.Chat;
import com.example.vivek.rentalmates.backend.entities.ChatMessage;
import com.example.vivek.rentalmates.backend.entities.Contact;
import com.example.vivek.rentalmates.backend.entities.ExpenseData;
import com.example.vivek.rentalmates.backend.entities.ExpenseGroup;
import com.example.vivek.rentalmates.backend.entities.FlatInfo;
import com.example.vivek.rentalmates.backend.entities.FlatSearchCriteria;
import com.example.vivek.rentalmates.backend.entities.LogStore;
import com.example.vivek.rentalmates.backend.entities.RegistrationRecord;
import com.example.vivek.rentalmates.backend.entities.Request;
import com.example.vivek.rentalmates.backend.entities.RoomMateSearchCriteria;
import com.example.vivek.rentalmates.backend.entities.UserProfile;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.NotFoundException;
import com.googlecode.objectify.ObjectifyService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.inject.Named;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import static com.googlecode.objectify.ObjectifyService.ofy;

@Api(
        name = "mainApi",
        version = "v1",
        resource = "main",
        namespace = @ApiNamespace(
                ownerDomain = "backend.rentalmates.vivek.example.com",
                ownerName = "backend.rentalmates.vivek.example.com",
                packagePath = ""
        )
)

public class MainEndpoint {

    private static final Logger logger = Logger.getLogger(MainEndpoint.class.getName());

    static {
        ObjectifyService.register(FlatInfo.class);
        ObjectifyService.register(ExpenseData.class);
        ObjectifyService.register(ExpenseGroup.class);
        ObjectifyService.register(UserProfile.class);
        ObjectifyService.register(Request.class);
        ObjectifyService.register(Contact.class);
        ObjectifyService.register(FlatSearchCriteria.class);
        ObjectifyService.register(RoomMateSearchCriteria.class);
        ObjectifyService.register(RegistrationRecord.class);
        ObjectifyService.register(LogStore.class);
        ObjectifyService.register(Chat.class);
        ObjectifyService.register(ChatMessage.class);
    }

    /**
     * Returns the {@link AggregateData} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return AggregateData for the corresponding ID
     * @throws NotFoundException if there is no {@code UserProfile} with the provided ID.
     */
    @ApiMethod(
            name = "getAggregateData",
            path = "aggregateData/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public AggregateData getAggregateData(@Named("id") Long id) throws NotFoundException {

        AggregateData data = new AggregateData();

        UserProfile userProfile = ofy().load().type(UserProfile.class).id(id).now();
        if (userProfile == null) {
            throw new NotFoundException("Could not find UserProfile with ID: " + id);
        }

        if (userProfile.getNumberOfFlats() > 0) {
            for (Long flatId : userProfile.getFlatIds()) {
                FlatInfo flatInfo = ofy().load().type(FlatInfo.class).id(flatId).now();
                if (flatInfo.getUpdateCount() > 0) {
                    data.addFlat(flatInfo);
                    flatInfo.resetUpdateCount();
                    ofy().save().entity(flatInfo).now();
                }
            }
        }

        if (userProfile.getNumberOfExpenseGroups() > 0) {
            for (Long expenseGroupId : userProfile.getExpenseGroupIds()) {
                ExpenseGroup expenseGroup = ofy().load().type(ExpenseGroup.class).id(expenseGroupId).now();
                if (expenseGroup.getUpdateCount() > 0) {
                    data.addExpenseGroup(expenseGroup);
                    expenseGroup.resetUpdateCount();
                    ofy().save().entity(expenseGroup).now();
                }
            }
        }

        if (userProfile.getNumberOfRequests() > 0) {
            for (Long requestId : userProfile.getRequestIds()) {
                Request request = ofy().load().type(Request.class).id(requestId).now();
                if (request.getUpdateCount() > 0) {
                    data.addRequest(request);
                    request.resetUpdateCount();
                    ofy().save().entity(request).now();
                }
            }
        }

        return data;
    }


    /**
     * Request to join a given entity.
     */
    @ApiMethod(
            name = "requestJoinExistingEntity",
            path = "requestJoinExistingEntity",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Request requestJoinExistingEntity(@Named("entityType") String entityName, @Named("entityName") String entityType, @Named("ownerEmailId") String ownerEmailId, @Named("userProfileId") Long userProfileId) throws IOException, NotFoundException {
        Request request = new Request();
        UserProfile requesterUserProfile = ofy().load().type(UserProfile.class).id(userProfileId).now();
        if (requesterUserProfile == null) {
            request.setStatus("INVALID_USER");
            return request;
        }
        switch (entityType) {
            case "FlatInfo":
                FlatInfo flatInfo = ofy().load().type(FlatInfo.class)
                        .filter("flatName", entityName)
                        .filter("ownerEmailId", ownerEmailId)
                        .first().now();
                if (flatInfo == null) {
                    request.setStatus("ENTITY_NOT_AVAILABLE");
                    return request;
                }
                if (requesterUserProfile.getFlatIds().contains(flatInfo.getFlatId())) {
                    request.setStatus("ALREADY_MEMBER");
                    return request;
                }
                request.setRequestedEntity(flatInfo.getFlatId());
                request.setRequestProviderId(flatInfo.getOwnerId());
                break;
            case "ExpenseGroup":
                ExpenseGroup expenseGroup = ofy().load().type(ExpenseGroup.class)
                        .filter("name", entityName)
                        .filter("ownerEmailId", ownerEmailId)
                        .first().now();
                if (expenseGroup == null) {
                    request.setStatus("ENTITY_NOT_AVAILABLE");
                    return request;
                }
                if (requesterUserProfile.getExpenseGroupIds().contains(expenseGroup.getId())) {
                    request.setStatus("ALREADY_MEMBER");
                    return request;
                }
                request.setRequestedEntity(expenseGroup.getId());
                request.setRequestProviderId(expenseGroup.getOwnerId());
                break;
            default:
                request.setStatus("UNKNOWN_ENTITY");
                return request;
        }
        request.setRequesterId(userProfileId);
        request.setStatus("PENDING");
        request.setEntityType(entityType);
        request.setRequestedEntityName(entityName);
        request.setRequesterName(requesterUserProfile.getUserName());
        ofy().save().entity(request).now();

        //Add request Id to requestProvider's requestIds list
        UserProfile userProfile = ofy().load().type(UserProfile.class).id(request.getRequestProviderId()).now();
        userProfile.addRequestId(request.getId());
        ofy().save().entity(userProfile).now();

        //send notification to requestProvider
        List<Long> userIds = new ArrayList<>();
        userIds.add(request.getRequestProviderId());
        String message = requesterUserProfile.getUserName() + " has requested to join " + entityType + ": " + entityName;
        ExpenseGroupEndpoint.sendMessage(userIds, "message", message);
        return ofy().load().entity(request).now();
    }

    /**
     * Reject request to join a given entity.
     */
    @ApiMethod(
            name = "rejectRequest",
            path = "rejectRequest",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Request rejectRequest(@Named("requestId") Long requestId) throws IOException {
        Request request = ofy().load().type(Request.class).id(requestId).now();

        //update request status
        request.setStatus("REJECTED");
        ofy().save().entity(request).now();
        request = ofy().load().entity(request).now();

        //Remove request Id from requestProvider's requestIds list
        UserProfile userProfile = ofy().load().type(UserProfile.class).id(request.getRequestProviderId()).now();
        userProfile.removeRequestId(request.getId());
        ofy().save().entity(userProfile).now();

        //send notification to requester
        List<Long> userIds = new ArrayList<>();
        userIds.add(request.getRequesterId());
        String message = "Your request to join " + request.getEntityType() + " " + request.getRequestedEntityName() + " has been rejected";
        ExpenseGroupEndpoint.sendMessage(userIds, "message", message);
        return request;
    }

    /**
     * Accept request to join a given entity.
     */
    @ApiMethod(
            name = "acceptRequest",
            path = "acceptRequest",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Request acceptRequest(@Named("requestId") Long requestId) throws IOException {
        Request request = ofy().load().type(Request.class).id(requestId).now();
        switch (request.getEntityType()) {
            case "FlatInfo":
                acceptRequestRegisterWithOtherFlat(request.getRequestedEntity(), request.getRequesterId());
                break;
            case "ExpenseGroup":
                acceptRequestJoinExistingExpenseGroup(request.getRequestedEntity(), request.getRequesterId());
                break;
            default:
                break;
        }
        //update request status
        request.setStatus("APPROVED");
        ofy().save().entity(request).now();
        request = ofy().load().entity(request).now();

        //Remove request Id from requestProvider's requestIds list
        UserProfile requestProviderUserProfile = ofy().load().type(UserProfile.class).id(request.getRequestProviderId()).now();
        requestProviderUserProfile.removeRequestId(request.getId());
        ofy().save().entity(requestProviderUserProfile).now();

        //send notification to requester
        List<Long> userIds = new ArrayList<>();
        userIds.add(request.getRequesterId());
        String message = "Your request to join " + request.getEntityType() + " " + request.getRequestedEntityName() + " has been approved";
        ExpenseGroupEndpoint.sendMessage(userIds, "message", message);
        return request;
    }

    /**
     * Accept request to join a given entity.
     */
    @ApiMethod(
            name = "acceptRequestRegisterWithOtherFlat",
            path = "acceptRequestRegisterWithOtherFlat",
            httpMethod = ApiMethod.HttpMethod.POST)
    public void acceptRequestRegisterWithOtherFlat(@Named("requestedEntityId") Long requestedEntityId, @Named("requesterId") Long requesterId) throws IOException {
        FlatInfo finalFlatInfo = ofy().load().type(FlatInfo.class).id(requestedEntityId).now();
        UserProfile userProfile;
        if (finalFlatInfo == null) {
            logger.info("Created FlatInfo.");
        } else {
            //Add flatId of flat to UserProfile flatIds List
            userProfile = ofy().load().type(UserProfile.class).id(requesterId).now();
            ExpenseGroup flatExpenseGroup = ofy().load().type(ExpenseGroup.class).id(finalFlatInfo.getExpenseGroupId()).now();
            if (!userProfile.getFlatIds().contains(finalFlatInfo.getFlatId())) {
                userProfile.addFlatId(finalFlatInfo.getFlatId());
            }
            userProfile.setPrimaryFlatId(finalFlatInfo.getFlatId());
            if (!userProfile.getExpenseGroupIds().contains(flatExpenseGroup.getId())) {
                userProfile.addExpenseGroupId(flatExpenseGroup.getId());
            }
            userProfile.setFlatExpenseGroupId(flatExpenseGroup.getId());
            ofy().save().entity(userProfile).now();

            //Add userProfileId to FlatInfo userIds List
            if (!finalFlatInfo.getMemberIds().contains(requesterId)) {
                finalFlatInfo.addMemberId(requesterId);
                ofy().save().entity(finalFlatInfo).now();
            }
            Long l = (long) 0;//need to be changed later
            if (!flatExpenseGroup.getMembersData().keySet().contains(requesterId)) {
                flatExpenseGroup.addMemberData(requesterId, l);
                ofy().save().entity(flatExpenseGroup).now();
            }
            finalFlatInfo.setCreateFlatResult("OLD_FLAT_INFO");
        }
    }

    /**
     * Accept request to join existing {@code ExpenseGroup}.
     */
    @ApiMethod(
            name = "acceptRequestJoinExistingExpenseGroup",
            path = "acceptRequestJoinExistingExpenseGroup",
            httpMethod = ApiMethod.HttpMethod.POST)
    public void acceptRequestJoinExistingExpenseGroup(@Named("requestedEntityId") Long requestedEntityId, @Named("requesterId") Long requesterId) throws IOException {
        ExpenseGroup finalExpenseGroup = ofy().load().type(ExpenseGroup.class).id(requestedEntityId).now();
        UserProfile userProfile;
        if (finalExpenseGroup == null) {
            logger.info("Created ExpenseGroup.");
        } else {
            //Add expenseGroupId of expense group to UserProfile expenseGroupIds List
            userProfile = ofy().load().type(UserProfile.class).id(requesterId).now();
            if (!userProfile.getExpenseGroupIds().contains(requestedEntityId)) {
                userProfile.addExpenseGroupId(requestedEntityId);
            }
            ofy().save().entity(userProfile).now();

            //Add userProfileId to ExpenseGroup userIds List
            Long l = (long) 0;//need to be changed later
            if (!finalExpenseGroup.getMembersData().keySet().contains(requesterId)) {
                finalExpenseGroup.addMemberData(requesterId, l);
                ofy().save().entity(finalExpenseGroup).now();
            }
        }
    }

    /**
     * Add a new contact to a given flat.
     */
    @ApiMethod(
            name = "addContact",
            path = "addContact",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Contact addContact(Contact contact, @Named("requesterId") Long requesterId) throws IOException {
        ofy().save().entity(contact).now();
        contact = ofy().load().entity(contact).now();
        FlatInfo flatInfo = ofy().load().type(FlatInfo.class).id(contact.getFlatId()).now();
        flatInfo.addContactId(contact.getId());
        ofy().save().entity(flatInfo).now();
        contact.setRequestResult("SUCCESSFUL");
        return contact;
    }

    /**
     * Returns List of {@code Contact} for a given {@code FlatInfo}.
     */
    @ApiMethod(
            name = "getContactList",
            path = "getContactList",
            httpMethod = ApiMethod.HttpMethod.POST)
    public List<Contact> getContactList(@Named("id") Long flatId, @Named("flatId") Long userProfileId) throws NotFoundException {
        UserProfile userProfile = ofy().load().type(UserProfile.class).id(userProfileId).now();
        if (!userProfile.getFlatIds().contains(flatId)) {
            return null;
        }
        FlatInfo flatInfo = ofy().load().type(FlatInfo.class).id(flatId).now();
        List<Contact> contacts = new ArrayList<>();
        for (Long contactId : flatInfo.getContactIds()) {
            Contact contact = ofy().load().type(Contact.class).id(contactId).now();
            contacts.add(contact);
        }
        return contacts;
    }

    private void sendExpenseDataAsMail(Long userProfileId) {
        UserProfile userProfile = ofy().load().type(UserProfile.class).id(userProfileId).now();
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        String msgBody = "All Expenses";
        WritableWorkbook workbook;
        byte[] attachmentData;

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            workbook = Workbook.createWorkbook(byteArrayOutputStream);
            for (Long expenseGroupId : userProfile.getExpenseGroupIds()) {
                ExpenseGroup expenseGroup = ofy().load().type(ExpenseGroup.class).id(expenseGroupId).now();
                WritableSheet sheet = workbook.createSheet(expenseGroup.getName(), 0);
                WritableFont headingFont = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, false);
                WritableFont normalFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false);
                WritableCellFormat headingFormat = new WritableCellFormat(headingFont);
                headingFormat.setBackground(Colour.GREEN);
                headingFormat.setAlignment(Alignment.CENTRE);
                WritableCellFormat normalFormat = new WritableCellFormat(normalFont);
                normalFormat.setBackground(Colour.VERY_LIGHT_YELLOW);
                normalFormat.setAlignment(Alignment.CENTRE);
                sheet.addCell(new Label(0, 0, "ExpenseGroup: ", headingFormat));
                sheet.addCell(new Label(1, 0, expenseGroup.getName(), normalFormat));
                sheet.addCell(new Label(0, 1, "Sl. No.", headingFormat));
                sheet.addCell(new Label(1, 1, "Description", headingFormat));
                sheet.addCell(new Label(2, 1, "Uploaded By", headingFormat));
                sheet.addCell(new Label(3, 1, "Date", headingFormat));
                int rowNumber = 4;
                int numberOfMembers = expenseGroup.getNumberOfMembers();
                List<Long> memberIds = new ArrayList<>();
                int count = 0;
                UserProfile tempUserProfile;
                for (Long memberId : expenseGroup.getMembersData().keySet()) {
                    tempUserProfile = ofy().load().type(UserProfile.class).id(memberId).now();
                    memberIds.add(count, memberId);
                    sheet.addCell(new Label(rowNumber + count, 1, tempUserProfile.getUserName(), headingFormat));
                    count++;
                }
                int currentRow = 2;
                for (Long expenseDataId : expenseGroup.getExpenseDataIds()) {
                    ExpenseData expenseData = ofy().load().type(ExpenseData.class).id(expenseDataId).now();
                    sheet.addCell(new Label(0, currentRow, String.valueOf(currentRow - 1), normalFormat));
                    sheet.addCell(new Label(1, currentRow, expenseData.getDescription(), normalFormat));
                    sheet.addCell(new Label(2, currentRow, expenseData.getUserName(), normalFormat));
                    sheet.addCell(new Label(3, currentRow, expenseData.getDate().toString(), normalFormat));
                    for (int j = 0; j < numberOfMembers; j++) {
                        Long userId = memberIds.get(j);
                        Long amount = expenseData.getExpenseValues().get(userId);
                        sheet.addCell(new Label(4 + j, currentRow, amount.toString(), normalFormat));
                    }
                    currentRow++;
                }
            }
            workbook.write();
            workbook.close();

            attachmentData = byteArrayOutputStream.toByteArray();
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("vivekmsit@gmail.com", "RentalMates"));
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(userProfile.getEmailId(), userProfile.getUserName()));
            message.setSubject("RentalMates: Testing Mail feature");
            message.setText(msgBody);

            Multipart mp = new MimeMultipart();
            MimeBodyPart attachment = new MimeBodyPart();
            InputStream attachmentDataStream = new ByteArrayInputStream(attachmentData);
            attachment.setFileName("Expenses.xls");
            attachment.setContent(attachmentDataStream, "application/xls");
            mp.addBodyPart(attachment);

            message.setContent(mp);
            Transport.send(message);

        } catch (MessagingException | WriteException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeLog(String log) {
        LogStore logStore = ofy().load().type(LogStore.class).filter("ownerEmailId", "vivekmsit@gmail.com").first().now();
        if (logStore == null) {
            logStore = new LogStore();
            logStore.setOwnerEmailId("vivekmsit@gmail.com");
        }
        logStore.writeLog(log);
        ofy().save().entity(logStore).now();
    }

    /**
     * Sends message to a given receiver.
     */
    @ApiMethod(
            name = "sendChatMessage",
            path = "sendChatMessage",
            httpMethod = ApiMethod.HttpMethod.POST)
    public ChatMessage sendChatMessage(@Named("chatId") Long chatId, @Named("senderId") Long senderId, @Named("receiverId") Long receiverId, @Named("messageContent") String messageContent) throws NotFoundException {
        UserProfile senderUserProfile = ofy().load().type(UserProfile.class).id(senderId).now();
        UserProfile receiverUserProfile = ofy().load().type(UserProfile.class).id(receiverId).now();
        Chat chat;
        if (chatId == 1) {
            //New Chat
            chat = new Chat();
            chat.setFirstMemberId(senderId);
            chat.setSecondMemberId(receiverId);
        } else {
            //Old Chat
            chat = ofy().load().type(Chat.class).id(chatId).now();
        }

        //Create ChatMessage entity
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(messageContent);
        chatMessage.setSenderId(senderId);
        chatMessage.setReceiverId(receiverId);
        chatMessage.setChatId(chatId);
        chatMessage.setSent(true);
        ofy().save().entity(chatMessage).now();
        chatMessage = ofy().load().entity(chatMessage).now();

        //Add message to Chat message list
        chat.addMessage(chatMessage.getId());
        ofy().save().entity(chat).now();
        chat = ofy().load().entity(chat).now();

        if (chatId == 1) {
            senderUserProfile.addChat(receiverId, chat.getId());
            receiverUserProfile.addChat(senderId, chat.getId());
            senderUserProfile.addChatReceiverId(chat.getId());
            receiverUserProfile.addChatReceiverId(chat.getId());
            ofy().save().entity(senderUserProfile).now();
            ofy().save().entity(receiverUserProfile).now();
            chatMessage.setChatId(chat.getId());
            ofy().save().entity(chatMessage).now();
            chatMessage = ofy().load().entity(chatMessage).now();
        }

        //send notification to receiver
        List<Long> userIds = new ArrayList<>();
        userIds.add(receiverId);
        String message = "New message received from " + senderUserProfile.getUserName();
        try {
            ExpenseGroupEndpoint.sendMessage(userIds, "ChatMessage", message);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return chatMessage;
    }


    /**
     * Returns List of {@code Chat} for a given {@code UserProfile}.
     */
    @ApiMethod(
            name = "getChatList",
            path = "getChatList",
            httpMethod = ApiMethod.HttpMethod.POST)
    public List<Chat> getChatList(@Named("id") Long userProfileId) throws NotFoundException {
        checkUserProfileExists(userProfileId);
        UserProfile userProfile = ofy().load().type(UserProfile.class).id(userProfileId).now();
        List<Chat> chats = new ArrayList<>();
        for (String chatId : userProfile.getChats().values()) {
            Chat chat = ofy().load().type(Chat.class).id(Integer.getInteger(chatId)).now();
            chats.add(chat);
        }
        return chats;
    }

    /**
     * Returns List of {@code ChatMessage} for a given {@code Chat}.
     */
    @ApiMethod(
            name = "getChatMessageList",
            path = "getChatMessageList",
            httpMethod = ApiMethod.HttpMethod.POST)
    public List<ChatMessage> getChatMessageList(@Named("id") Long chatId) throws NotFoundException {
        checkChatExists(chatId);
        Chat chat = ofy().load().type(Chat.class).id(chatId).now();
        List<ChatMessage> chatMessages = new ArrayList<>();
        for (Long chatMessageId : chat.getMessages()) {
            ChatMessage chatMessage = ofy().load().type(ChatMessage.class).id(chatMessageId).now();
            chatMessages.add(chatMessage);
        }
        return chatMessages;
    }

    /**
     * Returns previous chatId for a given receiverId.
     */
    @ApiMethod(
            name = "getPreviousChatId",
            path = "getPreviousChatId",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Chat getPreviousChatId(@Named("id") Long userProfileId, @Named("receiverId") Long receiverId) throws NotFoundException {
        checkUserProfileExists(userProfileId);
        UserProfile userProfile = ofy().load().type(UserProfile.class).id(userProfileId).now();
        Chat chat = null;
        if (userProfile.getChatReceiverIds().contains(receiverId)) {
            chat = ofy().load().type(Chat.class).id(Long.valueOf(userProfile.getChats().get(receiverId))).now();
        } else {
            chat = new Chat();
        }
        return chat;
    }

    private void checkUserProfileExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(UserProfile.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find UserProfile with ID: " + id);
        }
    }

    private void checkChatExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(Chat.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Chat with ID: " + id);
        }
    }
}