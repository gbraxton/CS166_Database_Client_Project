package GUI;

import Main.Messenger;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by gbraxton on 6/6/14.
 *
 */
public class MainUserPanel extends JPanel{
    private String mUser;
    private String mStatus;
    private NotificationsTabPanel notificationsTabPanel;
    private ChatTabPanel chatTabPanel;
    private ContactsTabPanel contactsTabPanel;
    private BlockedTabPanel blockedTabPanel;
    private JTabbedPane tabbedPane;

    MainUserPanel(String user){
        super();
        mUser = user;
        mStatus = Messenger.getmMessengerGUIInterface().getStatus(mUser);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        JPanel logoutPanel = new JPanel();
        logoutPanel.setLayout(new BoxLayout(logoutPanel, BoxLayout.LINE_AXIS));
        logoutPanel.setPreferredSize(new Dimension(500, 40));
        final JTextField statusField = new JTextField();
        statusField.setPreferredSize(new Dimension(275, 40));
        statusField.setText(mStatus);
        logoutPanel.add(statusField);
        JButton updateStatusButton = new JButton("update status");
        updateStatusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Messenger.getmMessengerGUIInterface().updateStatus(mUser, statusField.getText().trim());
            }
        });
        logoutPanel.add(updateStatusButton);
        JButton logoutButton = new JButton("Logout");
        logoutButton.setAlignmentX(RIGHT_ALIGNMENT);
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                MessengerGUI.setUser(null);
                MessengerGUI.updateGuiState(MessengerGUI.GuiStates.NOT_LOGGED_IN);
            }
        });
        logoutPanel.add(logoutButton);
        JButton deleteAccountButton = new JButton("Delete Account");
        deleteAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(!Messenger.getmMessengerGUIInterface().accountEligibleForDelete(mUser)){
                    JOptionPane.showMessageDialog(tabbedPane, "You must first delete the chats \nto which you are the initial sender");
                } else {
                    int confimation = JOptionPane.showConfirmDialog(tabbedPane, "Are you sure you want to delete your account\nThis canoot be undone", "Delete Account", JOptionPane.OK_CANCEL_OPTION);
                    if(confimation == JOptionPane.OK_OPTION){
                        Messenger.getmMessengerGUIInterface().deleteAccount(mUser);
                        MessengerGUI.updateGuiState(MessengerGUI.GuiStates.NOT_LOGGED_IN);
                    }
                }
            }
        });
        logoutPanel.add(deleteAccountButton);

        add(logoutPanel);


        tabbedPane = new JTabbedPane();
        chatTabPanel = new ChatTabPanel(mUser);
        contactsTabPanel = new ContactsTabPanel(mUser);
        notificationsTabPanel = new NotificationsTabPanel(mUser, this);
        tabbedPane.addTab("Notifications", null, notificationsTabPanel, "New messages not yet seen are displayed here");

        tabbedPane.addTab("Chats        ", null, chatTabPanel, "List of chats you are a member of");

        tabbedPane.addTab("Contacts", null, contactsTabPanel, "Your saved contacts");
        blockedTabPanel = new BlockedTabPanel(mUser);
        tabbedPane.addTab("Blocked", null, blockedTabPanel, "The users on your block list");

        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                int tabSelected = ((JTabbedPane)changeEvent.getSource()).getSelectedIndex();
                switch (tabSelected){
                    case 1:
                        notificationsTabPanel.tabSelected();
                        revalidate();
                        break;
                    case 2:
                        chatTabPanel.tabSelected();
                        revalidate();
                    case 3:
                        contactsTabPanel.tabSelected();
                        revalidate();
                        break;
                    case 4:
                        blockedTabPanel.tabSelected();
                        revalidate();
                    default:
                        break;
                }
            }
        });
        setPreferredSize(new Dimension(500, 500));
        add(tabbedPane);
        revalidate();
        //Tabs: notifications, browse chats, chat, contacts, blocked
    }

    public void goToChatFromNotification(String chatID){
        chatTabPanel.updateState(ChatTabPanel.ChatTabStates.MESSAGE_LIST, chatID, null);
        tabbedPane.setSelectedComponent(chatTabPanel);
        revalidate();
    }
}
