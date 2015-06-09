package GUI;

import Main.Messenger;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gbraxton on 6/8/14.
 *
 */
public class NewChatPanel extends JPanel {
    private DefaultListModel<String> recipientsListModel;
    private String mUser;
    private ChatTabPanel mChatTabPanel;

    NewChatPanel(String user, ChatTabPanel chatTabPanel){
        super();
        mUser = user;
        mChatTabPanel = chatTabPanel;

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setPreferredSize(new Dimension(500, 500));

        AddRecipientPanel addRecipientPanel = new AddRecipientPanel();
        add(addRecipientPanel);

        recipientsListModel = new DefaultListModel<String>();
        final JList<String> recipientsList = new JList<String>(recipientsListModel);
        recipientsList.setPreferredSize(new Dimension(500, 380));
        recipientsList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                if(!listSelectionEvent.getValueIsAdjusting()) {
                    String recipient = recipientsList.getSelectedValue();
                    recipientsListModel.removeElement(recipient);
                }
            }
        });

        JScrollPane recipientsListScrollPane = new JScrollPane(recipientsList);
        recipientsListScrollPane.setPreferredSize(new Dimension(500, 380));
        add(recipientsListScrollPane);

        JButton createButton = new JButton("Create Chat");
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                List<String> recipients = new ArrayList<String>();
                for(int i = 0; i < recipientsListModel.size(); i++){
                    recipients.add(recipientsListModel.get(i));
                }
                if(recipientsListModel.size()>0){
                    String chatID = Messenger.getmMessengerGUIInterface().createChat(mUser, recipients);
                    mChatTabPanel.updateState(ChatTabPanel.ChatTabStates.NEW_MESSAGE, null, chatID);
                }
            }
        });
        add(createButton);

    }

    private class AddRecipientPanel extends JPanel{
        public JLabel warning;
        AddRecipientPanel(){
            super();
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            setPreferredSize(new Dimension(500, 40));

            JLabel addRecipLabel = new JLabel("username");
            addRecipLabel.setPreferredSize(new Dimension(80, 40));
            add(addRecipLabel);

            final JTextField addRecipField = new JTextField();
            addRecipField.setPreferredSize(new Dimension(160, 40));
            add(addRecipField);

            JButton addRecipButton = new JButton("Add Recipient");
            addRecipButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    if(Messenger.getmMessengerGUIInterface().checkUserExists(addRecipField.getText()) && !recipientsListModel.contains(addRecipField.getText())){
                        recipientsListModel.addElement(addRecipField.getText());
                    }
                    addRecipField.setText("");
                }
            });
            add(addRecipButton);

            warning = new JLabel("");
            add(warning);

            validate();
        }


    }
}
