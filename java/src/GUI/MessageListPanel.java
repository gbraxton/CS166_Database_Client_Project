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
 *
 */
public class MessageListPanel extends JPanel {
    private ChatTabPanel mChatTabPanel;
    private String mUser;
    private String mChatID;
    private List<MessageObject> mMessages;

    MessageListPanel(String user, String chatID, ChatTabPanel ctPanel){
        super();
        mChatTabPanel = ctPanel;
        mUser = user;
        mChatID = chatID;
        mMessages = new ArrayList<MessageObject>();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        updateMessages(mChatID);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.PAGE_AXIS));
        titlePanel.setPreferredSize(new Dimension(500, 60));
        JLabel titleLabel = new JLabel("Messages");
        titleLabel.setFont(new Font("Verdana", Font.PLAIN, 22));
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        titlePanel.add(titleLabel);
        add(titlePanel);

        JLabel numMessagesLabel = new JLabel("There are " + mMessages.size() + " messages in this chat");
        numMessagesLabel.setFont(new Font("Verdana", Font.PLAIN, 18));
        add(numMessagesLabel);

        JList<MessageObject> messageList = new JList<MessageObject>(mMessages.toArray(new MessageObject[mMessages.size()]));
        messageList.setCellRenderer(new MessageListCellRenderer());
        messageList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        JScrollPane scrollPaneForMessageList = new JScrollPane(messageList);
        scrollPaneForMessageList.setPreferredSize(new Dimension(500, 400));
        add(scrollPaneForMessageList);

        add(new MessagesControlPanel(mUser, mChatTabPanel, mChatID));

        revalidate();
    }

    class MessagesControlPanel extends JPanel {
        MessagesControlPanel(final String user, final ChatTabPanel chatTabPanel, final String chatID){
            super();
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            setPreferredSize(new Dimension(500, 100));

            final JButton newMessageButton = new JButton("New Message");
            newMessageButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    chatTabPanel.updateState(ChatTabPanel.ChatTabStates.NEW_MESSAGE, user, chatID);
                }
            });
            add(newMessageButton);

            final JButton backButton = new JButton("Back");
            backButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    chatTabPanel.updateState(ChatTabPanel.ChatTabStates.CHAT_LIST, null, null);
                }
            });
            add(backButton);

            final JButton deleteChatButton = new JButton("Delete Chat");
            deleteChatButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    if(Messenger.getmMessengerGUIInterface().chatOwnerCheck(mUser, mChatID)){
                        int confirmation = JOptionPane.showConfirmDialog(MessageListPanel.this,
                                "Are you sure you want to delete this chat \n and all the messages in it?",
                                "Confirm Delete", JOptionPane.OK_CANCEL_OPTION );
                        if(confirmation == JOptionPane.OK_OPTION){
                            Messenger.getmMessengerGUIInterface().deleteChat(mChatID);
                            mChatTabPanel.updateState(ChatTabPanel.ChatTabStates.CHAT_LIST, null, null);
                        }
                    } else {
                        JOptionPane.showMessageDialog(MessageListPanel.this, "You must be the initial sender \n of this chat to delete it", "Cannot Delet", JOptionPane.OK_OPTION );
                    }


                }
            });
            add(deleteChatButton);
        }
    }

    class MessageListCellRenderer extends JPanel implements ListCellRenderer<Object> {
        private JLabel messageSender;
        private JLabel messageTime;
        private JLabel messageText;
        private JLabel mediaType;
        private JLabel mediaURL;

        MessageListCellRenderer(){
            super();
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            setBackground(Color.white);
            setPreferredSize(new Dimension(500, 90));
            setBorder(BorderFactory.createLineBorder(Color.black));

            messageSender = new JLabel("");
            messageTime = new JLabel("");
            messageText = new JLabel("");
            mediaType = new JLabel("");
            mediaURL = new JLabel("");
            add(messageSender);
            add(messageTime);
            add(messageText);
            add(mediaType);
            add(mediaURL);
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

            messageSender.setText( "Sender: " + ((MessageObject)value).messageSender );
            messageTime.setText( "Time: " + ((MessageObject)value).messageTime );
            messageText.setText( "<html>Message: " + ((MessageObject)value).messageText + "</html>");
            if( ((MessageObject)value).mediaURL != null) {
                mediaType.setText("<html>Attachment type: " + ((MessageObject) value).mediaType + "</html>");
                mediaURL.setText("<html>Attachment url: " + ((MessageObject)value).mediaURL + "</html>");
            } else {
                mediaType.setText("");
                mediaURL.setText("");
            }
            revalidate();
            return this;
        }

    }

    private class MessageObject {
        private String messageSender;
        private String messageTime;
        private String messageText;
        private String mediaType;
        private String mediaURL;

        MessageObject(String sender, String time, String text, String type, String url){
            messageSender = sender;
            messageTime = time;
            messageText = text;
            mediaType = type;
            mediaURL = url;
        }
    }

    public void updateMessages(String chatID){
        mMessages.clear();
        List<List<String>> result = Messenger.getmMessengerGUIInterface().getAllUnblockedMessagesByChatIDAndUser(chatID, mUser);
        for(List<String> ls : result){
            List<List<String>> attachment = Messenger.getmMessengerGUIInterface().getMediaAttachmentByMsgID(ls.get(0));
            if(attachment.size() > 0) {
                mMessages.add(new MessageObject(ls.get(1), ls.get(2), ls.get(3), attachment.get(0).get(0), attachment.get(0).get(1)));
            } else {
                mMessages.add(new MessageObject(ls.get(1), ls.get(2), ls.get(3), null, null));
            }
        }
    }
}
