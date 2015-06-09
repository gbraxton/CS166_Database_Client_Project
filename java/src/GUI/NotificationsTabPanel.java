package GUI;

import Main.Messenger;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gbraxton on 6/6/14.
 */
public class NotificationsTabPanel extends JPanel{
    private String mUser;
    private MainUserPanel mainUserPanel;
    private List<NotificationObject> mNotifications;

    NotificationsTabPanel(String user, MainUserPanel panel){
        super();
        mUser = user;
        mainUserPanel = panel;
        mNotifications = new ArrayList<NotificationObject>();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        tabSelected();

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.PAGE_AXIS));
        titlePanel.setPreferredSize(new Dimension(500, 60));
        JLabel titleLabel = new JLabel("Notifications");
        titleLabel.setFont(new Font("Verdana", Font.PLAIN, 22));
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        titlePanel.add(titleLabel);
        add(titlePanel);

        JLabel numNotificationsLabel = new JLabel("You have " + mNotifications.size() + " new notifications");
        numNotificationsLabel.setFont(new Font("Verdana", Font.PLAIN, 18));
        add(numNotificationsLabel);

        JList<NotificationObject> notificationJList = new JList<NotificationObject>(mNotifications.toArray(new NotificationObject[mNotifications.size()]));
        notificationJList.setCellRenderer(new NotificationListCellRenderer());
        notificationJList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        JScrollPane scrollPaneForNotificationsList = new JScrollPane(notificationJList);
        scrollPaneForNotificationsList.setPreferredSize(new Dimension(500, 500));
        add(scrollPaneForNotificationsList);
    }

    class NotificationListCellRenderer extends JPanel implements ListCellRenderer<Object> {
        private JLabel chatID;
        private JLabel members;
        private JLabel lastMessageSender;
        private JLabel lastMessageTime;
        private JLabel lastMessageText;

        NotificationListCellRenderer(){
            super();
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            setBackground(Color.white);
            setPreferredSize(new Dimension(500, 90));
            setBorder(BorderFactory.createLineBorder(Color.black));

            chatID = new JLabel("");
            members = new JLabel("");
            lastMessageSender = new JLabel("");
            lastMessageTime = new JLabel("");
            lastMessageText = new JLabel("");
            add(chatID);
            add(members);
            add(lastMessageSender);
            add(lastMessageTime);
            add(lastMessageText);
        }

        public Component getListCellRendererComponent(JList<?> list,
                                                      Object value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus){
            if(cellHasFocus){
                setBackground(Color.lightGray);
            } else {
                setBackground(Color.white);
            }

            if (isSelected) {
                mainUserPanel.goToChatFromNotification(((NotificationObject)value).chatID);
            }

            chatID.setText( "Chat ID: " + ((NotificationObject)value).chatID );
            members.setText( "<html>Members: " + ((NotificationObject)value).members + "</html>");
            lastMessageSender.setText( "Sender: " + ((NotificationObject)value).lastMessageSender );
            lastMessageTime.setText( "Time: " + ((NotificationObject)value).lastMessageTime );
            lastMessageText.setText( "<html>Message: " + ((NotificationObject)value).lastMessageText + "</html>");
            revalidate();
            return this;
        }

    }

    public class NotificationObject {
        public String chatID;
        public String members;
        public String lastMessageSender;
        public String lastMessageTime;
        public String lastMessageText;
        NotificationObject(String id, String mem, String lastSender, String lastTime, String lastText){
            chatID = id;
            members = mem;
            lastMessageSender = lastSender;
            lastMessageTime = lastTime;
            lastMessageText = lastText;
        }
    }

    public void tabSelected(){
        mNotifications.clear();
        List<List<String>> result = Messenger.getmMessengerGUIInterface().getNotificationsByUser(mUser);
        for(List<String> ls : result){
            String members = Messenger.getmMessengerGUIInterface().getMembersStringByChatID(ls.get(5));
            mNotifications.add(new NotificationObject(ls.get(5),members, ls.get(4), ls.get(2), ls.get(1)));
        }
    }
}
