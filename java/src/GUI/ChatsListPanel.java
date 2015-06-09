package GUI;

import Main.Messenger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gbraxton on 6/7/14.
 */
public class ChatsListPanel extends JPanel {
    private ChatTabPanel mChatTabPanel;
    private String mUser;
    private List<ChatObject> mChats;

    ChatsListPanel(String user, ChatTabPanel chatTabPanel){
        super();
        mChatTabPanel = chatTabPanel;
        mUser = user;
        mChats = new ArrayList<ChatObject>();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        updateChats();

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.PAGE_AXIS));
        titlePanel.setPreferredSize(new Dimension(500, 60));
        JLabel titleLabel = new JLabel("Chats");
        titleLabel.setFont(new Font("Verdana", Font.PLAIN, 22));
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        titlePanel.add(titleLabel);
        add(titlePanel);

        JLabel numChatsLabel = new JLabel("You are in  " + mChats.size() + " chats");
        numChatsLabel.setFont(new Font("Verdana", Font.PLAIN, 18));
        add(numChatsLabel);

        JList<ChatObject> chatsList = new JList<ChatObject>(mChats.toArray(new ChatObject[mChats.size()]));
        chatsList.setCellRenderer(new ChatListCellRenderer());
        chatsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        JScrollPane scrollPaneForChatsList = new JScrollPane(chatsList);
        scrollPaneForChatsList.setPreferredSize(new Dimension(500, 500));
        add(scrollPaneForChatsList);

        ChatTabControlPanel chatTabControlPanel = new ChatTabControlPanel();
        add(chatTabControlPanel);

        validate();
    }

    class ChatListCellRenderer extends JPanel implements ListCellRenderer<Object> {
        private JLabel chatID;
        private JLabel members;
        private JLabel lastMessageSender;
        private JLabel lastMessageTime;
        private JLabel lastMessageText;

        ChatListCellRenderer(){
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
            if(isSelected){
                mChatTabPanel.updateState(ChatTabPanel.ChatTabStates.MESSAGE_LIST, ((ChatObject)value).chatID, null);
            }
            if(cellHasFocus){
                setBackground(Color.lightGray);
            } else {
                setBackground(Color.white);
            }

            chatID.setText( "Chat ID: " + ((ChatObject)value).chatID );
            members.setText( "<html>Members: " + ((ChatObject)value).members + "</html>");
            lastMessageSender.setText( "Sender: " + ((ChatObject)value).lastMessageSender );
            lastMessageTime.setText( "Time: " + ((ChatObject)value).lastMessageTime );
            lastMessageText.setText( "<html>Message: " + ((ChatObject)value).lastMessageText + "</html>");
            revalidate();
            return this;
        }

    }

    private class ChatTabControlPanel extends JPanel {
        ChatTabControlPanel(){
            super();
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            setPreferredSize(new Dimension(500, 40));

            JButton newChatButton = new JButton("New Chat");
            newChatButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    mChatTabPanel.updateState(ChatTabPanel.ChatTabStates.NEW_CHAT, null, null);
                }
            });
            add(newChatButton);
        }
    }

    private class ChatObject {
        private String chatID;
        private String members;
        private String lastMessageSender;
        private String lastMessageTime;
        private String lastMessageText;
        ChatObject(String id, String mem, String lastSender, String lastTime, String lastText){
            chatID = id;
            members = mem;
            lastMessageSender = lastSender;
            lastMessageTime = lastTime;
            lastMessageText = lastText;
        }
    }

    public void updateChats(){
        mChats.clear();
        List<List<String>> chatIDs = Messenger.getmMessengerGUIInterface().getChatIDsByUser(mUser);
        for(List<String> ls : chatIDs){
            List<List<String>> message = Messenger.getmMessengerGUIInterface().getMostRecentUnblockedMessageByChatIDAndUser(ls.get(0), mUser);
            if(message.size() > 0) {
                String members = Messenger.getmMessengerGUIInterface().getMembersStringByChatID(ls.get(0));
                mChats.add(new ChatObject(message.get(0).get(0), members, message.get(0).get(1), message.get(0).get(2), message.get(0).get(3)));
            }
        }
    }
}
