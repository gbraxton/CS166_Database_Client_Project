package Main;

import java.io.BufferedReader;
import java.util.List;

/**
 * Created by gbraxton on 6/3/14.
 */
public class Notifications {
    public static void notifications(Messenger esql, BufferedReader in, String user) throws Exception {
        System.out.print("\n\n");
        System.out.println("==================================================");
        System.out.println("===================Notifications==================");
        System.out.println("==================================================");
        browseCurrentNotifications(esql, in, user);
    }

    public static void browseCurrentNotifications(Messenger esql, BufferedReader in, String user) throws Exception {
        @SuppressWarnings("SpellCheckingInspection")
        String query = "SELECT a.* FROM MESSAGE a, NOTIFICATION b WHERE b.usr_login='" +
                        user + "' AND b.msg_id=a.msg_id AND a.destr_timestamp IS NULL and"
                        + " a.sender_login not in (select a.list_member from user_list_contains a, usr b"
                        + " where b.login='" + user + "' and a.list_id=b.block_list);";
        List<List<String>> notifications = esql.executeQueryAndReturnResult(query);
        System.out.println("There are " + notifications.size() + " new notifications");
        for(List<String> list : notifications) {
            System.out.print(list.get(4) + "\n" + list.get(2) + "\n" + list.get(1));
        }
        query = "delete from notification where usr_login='" + user + "';";
        esql.executeUpdate(query);

    }
}
