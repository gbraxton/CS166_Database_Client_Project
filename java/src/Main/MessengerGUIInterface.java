package Main;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by gbraxton on 6/6/14.
 *
 */
public class MessengerGUIInterface {
    private Messenger mMessenger;

    MessengerGUIInterface(Messenger messenger){
        mMessenger = messenger;
    }

    public boolean login(String login, String password){
        String query = String.format("SELECT * FROM Usr WHERE login = '%s' AND password = '%s'", login, password);
        try {
            return mMessenger.executeQueryAndReturnResult(query).size() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<List<String>> getChatIDsByUser(String user) {
        String chatIDs = "(select chat_id from chat_list where member='" + user + "')";
        try {
            return mMessenger.executeQueryAndReturnResult(chatIDs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<List<String>> getMostRecentUnblockedMessageByChatIDAndUser(String chatID, String user){
        String blockedListID = "(select block_list from usr where login='" + user + "')";
        String blocked = "(select list_member from user_list_contains where list_id in" + blockedListID + ")";
        String message = "select chat_id, sender_login, msg_timestamp, msg_text from message where chat_id="
                            + chatID + " and sender_login not in " + blocked + " order by msg_timestamp";
        try {
            return mMessenger.executeQueryAndReturnResult(message);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<List<String>> getAllUnblockedMessagesByChatIDAndUser(String chatID, String user){
        String blockedListID = "(select block_list from usr where login='" + user + "')";
        String blocked = "(select list_member from user_list_contains where list_id in" + blockedListID + ")";
        String message = "select msg_id, sender_login, msg_timestamp, msg_text from message where chat_id="
                + chatID + " and sender_login not in " + blocked + " order by msg_timestamp desc";
        try {
            return mMessenger.executeQueryAndReturnResult(message);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<List<String>> getMediaAttachmentByMsgID(String id){
        String query = "select media_type, URL from media_attachment where msg_id=" + id + ";";
        try {
            return mMessenger.executeQueryAndReturnResult(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<List<String>> getNotificationsByUser(String user){
        String query = "SELECT a.* FROM MESSAGE a, NOTIFICATION b WHERE b.usr_login='" +
                user + "' AND b.msg_id=a.msg_id AND a.destr_timestamp IS NULL and"
                + " a.sender_login not in (select a.list_member from user_list_contains a, usr b"
                + " where b.login='" + user + "' and a.list_id=b.block_list);";
        try {
            List<List<String>> result =mMessenger.executeQueryAndReturnResult(query);
            query = "delete from notification where usr_login='" + user + "';";
            mMessenger.executeUpdate(query);
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getMembersStringByChatID(String id){
        String query = "select member from chat_list where chat_id=" + id + ";";
        StringBuilder sb = new StringBuilder();
        try {
            for(List<String> s : mMessenger.executeQueryAndReturnResult(query)){
                sb.append(s.get(0).trim());
                sb.append(" ");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public boolean chatOwnerCheck(String user, String chatID){
        String query = "select * from chat where init_sender='" + user + "' and chat_id=" + chatID + ";";
        int result = 0;
        try {
            result = mMessenger.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result > 0;
    }

    public void newMessage(String text, String destruct, String sender, String chat, String mType, String mUrl){
        String destStr = "null";
        if(destruct!=null){
            destStr = "'" + destruct + "'";
        }
        try {
            String query = "insert into message values (DEFAULT, '" + text + "', CURRENT_TIMESTAMP, " + destStr + ", '" + sender + "', " + chat + ");";
            mMessenger.executeUpdate(query);
            query = "select currval('message_msg_id_seq');";
            String mId = mMessenger.executeQueryAndReturnResult(query).get(0).get(0);
            query = "insert into notification(usr_login, msg_id) select member, "
                    + mId + " from chat_list where chat_id=" + chat + " and member!='" + sender + "';";
            mMessenger.executeUpdate(query);
            if(mUrl != null){
                query = "insert into media_attachment values (DEFAULT, '" + mType + "', '" + mUrl + "', " + mId + ");";
                mMessenger.executeUpdate(query);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public List<List<String>> getContactsByUser(String user){
        try {
            String query = "select login, status, phoneNum from usr where login in "
                    + "(select list_member from user_list_contains where list_id in "
                    + "(select contact_list from usr where login='" + user + "'));";
            return mMessenger.executeQueryAndReturnResult(query);
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public List<List<String>> getBlockedByUser(String user){
        try {
            String query = "select list_member from user_list_contains where list_id in "
                    + "(select block_list from usr where login='" + user + "');";
            return mMessenger.executeQueryAndReturnResult(query);
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void removeContactByUserContact(String user, String contact){
        String query = "delete from user_list_contains where list_id in "
                        + "(select contact_list from usr where login='" + user + "') "
                        + "and list_member='" + contact + "';";

        try {
            mMessenger.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeBlockByUserContact(String user, String contact){
        String query = "delete from user_list_contains where list_id in "
                + "(select block_list from usr where login='" + user + "') "
                + "and list_member='" + contact + "';";

        try {
            mMessenger.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkUserExists(String user){
        String query = "select * from usr where login='" + user + "';";
        boolean exists = false;
        try {
            exists = (mMessenger.executeQuery(query) > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exists;
    }

    public boolean checkUserAlreadyContact(String user, String contact){
        String query = "select * from user_list_contains where list_member='" + contact + "' and "
                + "list_id in (select contact_list from usr where login='" + user + "');";
        boolean already = false;
        try {
            already = (mMessenger.executeQuery(query) > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return already;
    }

    public boolean checkUserAlreadyBlocked(String user, String contact){
        String query = "select * from user_list_contains where list_member='" + contact + "' and "
                + "list_id in (select block_list from usr where login='" + user + "');";
        boolean already = false;
        try {
            already = (mMessenger.executeQuery(query) > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return already;
    }

    public void addContact(String user, String contact){
        String query = "insert into user_list_contains(list_id, list_member) values "
                        + "((select contact_list from usr where login='" + user + "'), '" + contact + "');";
        try {
            mMessenger.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addBlocked(String user, String contact){
        String query = "insert into user_list_contains(list_id, list_member) values "
                + "((select block_list from usr where login='" + user + "'), '" + contact + "');";
        try {
            mMessenger.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String createChat(String user, List<String> members){
        String chatType = "private";
        String chatID = null;
        if(members.size()>1){
            chatType = "group";
        }
        String query = "insert into chat values (DEFAULT, '" + chatType + "', '" + user + "');";
        System.out.println("\n Error query: " + query);
        try {
            mMessenger.executeUpdate(query);
            chatID = mMessenger.executeQueryAndReturnResult("select currval('chat_chat_id_seq');").get(0).get(0);
            mMessenger.executeUpdate("insert into chat_list values(" + chatID + ", '" + user + "');");
            for(String s : members){
                query = "insert into chat_list values(" + chatID + ", '" + s + "');";
                mMessenger.executeUpdate(query);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chatID;
    }

    public void deleteChat(String chatID){
        try {
            String query = "delete from media_attachment where msg_id in (select msg_id from message where chat_id=" + chatID + ");";
            mMessenger.executeUpdate(query);
            query = "delete from notification where msg_id in (select msg_id from message where chat_id=" + chatID + ");";
            mMessenger.executeUpdate(query);
            query = "delete from message where chat_id=" + chatID + ";";
            mMessenger.executeUpdate(query);
            query = "delete from chat_list where chat_id=" + chatID + ";";
            mMessenger.executeUpdate(query);
            query = "delete from chat where chat_id=" + chatID + ";";
            mMessenger.executeUpdate(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createUser(String user, String pass, String phone, String status){
        try {
            String query = "insert into user_list values (DEFAULT, 'contact');";
            mMessenger.executeUpdate(query);
            query = "select currval('user_list_list_id_seq');";
            String contactList = mMessenger.executeQueryAndReturnResult(query).get(0).get(0);

            query = "insert into user_list values (DEFAULT, 'block');";
            mMessenger.executeUpdate(query);
            query = "select currval('user_list_list_id_seq');";
            String blockList = mMessenger.executeQueryAndReturnResult(query).get(0).get(0);

            query = "insert into usr values ('" + user + "', '" + phone + "', '" + pass + "', '" + status + "', " + contactList + ", " + blockList + ");";
            mMessenger.executeUpdate(query);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getStatus(String user){
        String query = "select status from usr where login='" + user + "';";
        try {
            return mMessenger.executeQueryAndReturnResult(query).get(0).get(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateStatus(String user, String status){
        String query = "update usr set status='" + status + "' where login='" + user + "';";
        try {
            mMessenger.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean accountEligibleForDelete(String user){
        String query = "select init_sender from chat where init_sender='" + user + "';";
        try {
            return mMessenger.executeQueryAndReturnResult(query).size() == 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void deleteAccount(String user){
        String query = "DELETE FROM USR WHERE login='" + user + "';";
        try {
            mMessenger.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
