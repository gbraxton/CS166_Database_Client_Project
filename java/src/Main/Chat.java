package Main;

import java.io.BufferedReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gbraxton on 6/3/14.
 *
 */
public class Chat {
    public static void chatMenu(Messenger esql, BufferedReader in, String user) throws Exception{
        int choice = 1;
        while(choice != 9) {
            System.out.print("\n\n");
            System.out.println("==================================================");
            System.out.println("=====================Chat Menu====================");
            System.out.println("==================================================");
            System.out.println("1. Browse Current Chats");
            System.out.println("2. Start New Chat");
            System.out.println("9. Return to main menu");
            System.out.print("\nEnter choice: ");
            choice = Integer.parseInt(in.readLine());
            switch (choice) {
                case 1:
                    browseCurrentChats(esql, in, user);
                    break;
                case 2:
                    startNewChat(esql, in, user);
                    break;
                default:
                    break;
            }
        }
    }

    public static void startNewChat(Messenger esql, BufferedReader in, String user) throws Exception{
        List<String> recipients = new ArrayList<String>();
        int choice = 1;
        int chatID;
        while(choice != 9) {
            System.out.print("\n\n");
            System.out.println("==================================================");
            System.out.println("=====================New Chat=====================");
            System.out.println("==================================================");
            if (recipients.size() > 0) {
                System.out.print("Recipients: ");
                for (String s : recipients) {
                    System.out.print(s + ", ");
                }
                System.out.println();
            }
            System.out.println("1. Add recipient to this chat");
            if(recipients.size() > 0) {
                System.out.println("2. Remove recipient to this chat");
                System.out.println("3. Write message and send");
            }
            System.out.println("9. Discard");
            System.out.print("\nEnter choice: ");
            choice = Integer.parseInt(in.readLine());
            String recipient;
            switch(choice) {
                case 1:
                    System.out.println("Enter the login of the recipient to add");
                    recipient = in.readLine();
                    if(esql.executeQuery("select * from usr where login='" + recipient + "';") > 0) {
                        recipients.add(recipient);
                    } else {
                        System.out.println("That user does not exist");
                    }
                    break;
                case 2:
                    System.out.println("Enter the login of the recipient to remove");
                    recipient = in.readLine();
                    if(recipients.remove(recipient)) {
                        System.out.println(recipient + " was removed");
                    } else {
                        System.out.println(recipient + " is not on the recipient list");
                    }
                    break;
                case 3:
                    if(recipients.size() < 1) {
                        System.out.println("You must first add at least one recipient before writing the message text");
                    } else {
                        String query;
                        if(recipients.size() > 1) {
                            query = "insert into chat values (DEFAULT, 'group', '" + user + "');";
                        } else {
                            query = "insert into chat values (DEFAULT, 'private', '" + user + "');";
                        }
                        esql.executeUpdate(query);
                        chatID = Integer.parseInt((esql.executeQueryAndReturnResult("select currval('chat_chat_id_seq')")).get(0).get(0));

                        //now
                        if(writeNewMessage(esql, in, user, chatID)) {
                            int msgID = Integer.parseInt((esql.executeQueryAndReturnResult("select currval('message_msg_id_seq')")).get(0).get(0));
                            for (String s : recipients) {
                                query = "insert into notification values ('" + s + "', " + msgID + ");";
                                esql.executeUpdate(query);
                            }
                            query = "insert into chat_list values (" + chatID + ", '" + user + "');";
                            esql.executeUpdate(query);
                            for (String s : recipients) {
                                query = "insert into chat_list values (" + chatID + ", '" + s + "');";
                                esql.executeUpdate(query);
                            }
                            choice = 9;
                        } else {
                            esql.executeUpdate("delete from chat where chat_id=" + chatID + ";");
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public static void browseCurrentChats(Messenger esql, BufferedReader in, String user) throws Exception {
        int choice = 1;
        while(choice != 0) {
            int chatNum = 1;
            List<Integer> chatIDs = getChatIDs(esql, user);
            System.out.print("\n\n");
            System.out.println("==================================================");
            System.out.println("===================Current Chats==================");
            System.out.println("==================================================");
            System.out.println("You are a member of " + chatIDs.size() + " different chats.");
            System.out.println("--------------------------------------------------");

            for (Integer id : chatIDs) {
                System.out.println(chatNum++ + ". Chat ID: " + id);
                System.out.print("Members: ");
                for (String name : getMembersByChatID(esql, id)) {
                    System.out.print(name.trim() + ", ");
                }
                System.out.println();
                String query = "select init_sender from chat where chat_id=" + id + ";";
                System.out.println("Initial Sender: " + esql.executeQueryAndReturnResult(query).get(0).get(0));
                String[] firstMsg = getFirstMessageByChatID(esql, id);
                System.out.println("Last message at " + firstMsg[1]);
                System.out.println(firstMsg[0].trim());
                System.out.println("--------------------------------------------------");
            }
            System.out.println("Select a chat by number or enter 0 to return to chat menu:");
            choice = Integer.parseInt(in.readLine());
            if ((choice > 0) && (choice <= chatIDs.size())) {
                viewChatMessages(esql, in, user, chatIDs.get(choice - 1));
            } else if(chatNum != 0) {
                System.out.println("Invalid chat number");
            }
        }
    }

    public static void viewChatMessages(Messenger esql, BufferedReader in, String user, int chatID) throws Exception{
        int choice = -1;
        int currentView = 0;
        while(choice != 9) {
            System.out.print("\n\n");
            System.out.println("==================================================");
            System.out.println("=====================Messages=====================");
            System.out.println("==================================================");
            String query = "select sender_login, msg_timestamp, msg_text, msg_id from message where chat_id="
                            + chatID + "and sender_login not in (select a.list_member from user_list_contains a, usr b"
                            + " where b.login='" + user + "' and a.list_id=b.block_list)"
                            + " order by msg_timestamp desc limit 10 offset " + currentView + ";";
            for (List<String> message : esql.executeQueryAndReturnResult(query)) {
                System.out.println("Author: " + message.get(0).trim());
                System.out.println("Time: " + message.get(1).trim());
                System.out.println("Message: " + message.get(2).trim());
                query = "select URL from media_attachment where msg_id=" + message.get(3) + ";";
                List<List<String>> attachments = esql.executeQueryAndReturnResult(query);
                if(attachments.size() > 0){
                    System.out.println("Media Attachment: " + attachments.get(0).get(0));
                }
                System.out.println("----------------------------------------------------");
            }
            System.out.println("1. Write new message");
            System.out.println("2. Previous 10 messages");
            System.out.println("3. Add member to chat (If you are the initial sender)");
            System.out.println("4. Remove member from chat (If you are the initial sender)");
            System.out.println("5. Delete chat (If you are the initial sender)");
            System.out.println("9. Return to chat menu");
            choice = Integer.parseInt(in.readLine());
            switch (choice) {
                case 1: writeNewMessage(esql, in, user, chatID); break;
                case 2: currentView += 10; break;
                case 3: addMemberToChat(esql, in, user, chatID); break;
                case 4: removeMemberFromChat(esql, in, user, chatID); break;
                case 5: deleteChat(esql, in, user, chatID); choice = 9; break;
                default: break;
            }
        }
    }

    public static void addMemberToChat(Messenger esql, BufferedReader in, String user, int chatID) throws Exception{
        String query = "select * from chat where init_sender='" + user + "' and chat_id=" + chatID + ";";
        if(esql.executeQuery(query) > 0) {
            System.out.println("Enter the name of the member to add:");
            String memberToAdd = in.readLine();
            query = "select * from chat_list where chat_id=" + chatID + " and member='" + memberToAdd + "';";
            if(esql.executeQuery(query) == 0) {
                query = "insert into chat_list (chat_id, member) values (" + chatID + ", '" + memberToAdd + "');";
                esql.executeUpdate(query);
                System.out.println("Member, " + memberToAdd + ", was added to this chat");
            } else {
                System.out.println("That user is already a member of this chat");
            }
        } else {
            System.out.println("You must be the initial sender of this chat to add/remove members or delete the chat.");
        }
    }

    public static void removeMemberFromChat(Messenger esql, BufferedReader in, String user, int chatID) throws Exception{
        String query = "select * from chat where init_sender='" + user + "' and chat_id=" + chatID + ";";
        if(esql.executeQuery(query) > 0) {
            System.out.println("Enter the name of the member to remove:");
            String memberToRemove = in.readLine();
            query = "select * from chat_list where chat_id=" + chatID + " and member='" + memberToRemove + "';";
            if(esql.executeQuery(query) > 0) {
                query = "delete from chat_list where chat_id=" + chatID + " and member='" + memberToRemove + "';";
                esql.executeUpdate(query);
                System.out.println("Member, " + memberToRemove + ", was removed from this chat");
            } else {
                System.out.println("That user is not a member of this chat");
            }
        } else {
            System.out.println("You must be the initial sender of this chat to add/remove members or delete the chat.");
        }
    }

    public static void deleteChat(Messenger esql, BufferedReader in, String user, int chatID) throws Exception{
        String query = "select * from chat where init_sender='" + user + "' and chat_id=" + chatID + ";";
        if(esql.executeQuery(query) > 0) {
            System.out.println("Are you sure you want to delete this chat and all associated messages? (yes/no)");
            if(in.readLine().equals("yes")) {
                // this could be done with on delete cascade and one delete from chat"
                query = "delete from media_attachment where msg_id in (select msg_id from message where chat_id=" + chatID + ");";
                esql.executeUpdate(query);
                query = "delete from notification where msg_id in (select msg_id from message where chat_id=" + chatID + ");";
                esql.executeUpdate(query);
                query = "delete from message where chat_id=" + chatID + ";";
                esql.executeUpdate(query);
                query = "delete from chat_list where chat_id=" + chatID + ";";
                esql.executeUpdate(query);
                query = "delete from chat where chat_id=" + chatID + ";";
                esql.executeUpdate(query);
                System.out.println("Chat has been deleted");
            }
        } else {
            System.out.println("You must be the initial sender of this chat to add/remove members or delete the chat.");
        }
    }

    public static boolean writeNewMessage(Messenger esql, BufferedReader in, String user, int chatID) throws Exception {
        int choice = 1;
        boolean sent = false;
        String msgText = null, mediaType = null, mediaURL = null, selfDestructTS = null;
        while(choice != 9) {
            System.out.print("\n\n");
            System.out.println("==================================================");
            System.out.println("====================New Message===================");
            System.out.println("==================================================");
            System.out.println("1. Write Message Text");
            System.out.println("2. Add media attachment");
            System.out.println("3. Set a self-destruction time");
            System.out.println("4. Send message");
            System.out.println("9. Discard this message");
            choice = Integer.parseInt(in.readLine());
            switch (choice) {
                case 1:
                    System.out.println("Enter the message text:");
                    msgText = in.readLine();
                    break;
                case 2:
                    System.out.println("Enter the media type");
                    mediaType = in.readLine();
                    System.out.println("Enter the media attachment URL");
                    mediaURL = in.readLine();
                    break;
                case 3:
                    boolean correctFormat = false;
                    while (!correctFormat) {
                        System.out.println("Enter the self destruction time in format yyyy-mm-dd");
                        String date = in.readLine();
                        System.out.println("Enter the self destruction time in format hh:mm:ss");
                        String time = in.readLine();
                        java.util.Date dateObj = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date + " " + time);
                        if(dateObj == null) {
                            System.out.println("Incorrect date/time format. Please re-enter.");
                        } else {
                            correctFormat = true;
                            selfDestructTS = date + " " + time;
                        }
                    }
                    break;
                case 4:
                    String query = "insert into message values (DEFAULT, '" + msgText + "', CURRENT_TIMESTAMP, ";
                    String msgID = null;
                    if(selfDestructTS != null) {
                        query += "'" + selfDestructTS + "'";
                    } else {
                        query += "null";
                    }
                    query += ", '" + user + "', " + chatID + ");";
                    esql.executeUpdate(query);
                    msgID = esql.executeQueryAndReturnResult("select currval('message_msg_id_seq');").get(0).get(0);
                    if(mediaURL != null) {
                        query = "insert into media_attachment values (DEFAULT, '" + mediaType + "', '" + mediaURL + "', " + msgID + ");";
                        esql.executeUpdate(query);
                    }
                    //update notifications
                    query = "select member from chat_list where chat_id=" + chatID + ";";
                    List<List<String>> result = esql.executeQueryAndReturnResult(query);
                    for(List<String> ls : result) {
                        query = "insert into notification values ('" + ls.get(0) + "', " + msgID + ");";
                        esql.executeUpdate(query);
                    }
                    sent = true;
                    choice = 9;
                    break;
                default:
                    break;
            }
        }
        return sent;
    }

    public static List<Integer> getChatIDs(Messenger esql, String user) throws Exception{
        String query = "select chat_id from chat_list where member='" + user + "';";
        List<Integer> chatIDs = new ArrayList<Integer>();
        for(List<String> chat : esql.executeQueryAndReturnResult(query)){
            chatIDs.add(Integer.parseInt(chat.get(0)));
        }
        return chatIDs;
    }

    public static List<String> getMembersByChatID(Messenger esql, int id) throws Exception {
        String query = "select member from chat_list where chat_id=" + id + ";";
        List<String> members = new ArrayList<String>();
        for(List<String> cm : esql.executeQueryAndReturnResult(query)){
            members.add(cm.get(0));
        }
        return members;
    }

    public static String[] getFirstMessageByChatID(Messenger esql, int id) throws Exception {
        String query = "select msg_text, msg_timestamp from message where chat_id=" + id + " order by msg_timestamp desc;";
        List<List<String>> list = esql.executeQueryAndReturnResult(query);
        return new String[] {list.get(0).get(0), list.get(0).get(1)};
    }
}
