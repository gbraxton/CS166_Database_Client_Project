package GUI;

import Main.Messenger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by gbraxton on 6/7/14.
 */
public class NewMessagePanel extends JPanel {
    private String mUser;
    private String mChatID;
    private ChatTabPanel mChatTabPanel;

    NewMessagePanel(String user, String cID, ChatTabPanel cPanel){
        super();
        mUser = user;
        mChatID = cID;
        mChatTabPanel = cPanel;

        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(500, 500));

        GridBagConstraints constraints = new GridBagConstraints();

        //row 0
        JLabel titleLabel = new JLabel("New Message");
        titleLabel.setFont(new Font("Verdana", Font.PLAIN, 22));
        titleLabel.setPreferredSize(new Dimension(500, 40));
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.gridwidth = 3;
        constraints.gridheight = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        add(titleLabel, constraints);
        //row 1
        JLabel textAreaLabel = new JLabel("Message Text:");
        textAreaLabel.setPreferredSize(new Dimension(150, 20));
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        add(textAreaLabel, constraints);
        final JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(300, 60));
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.gridwidth = 2;
        constraints.gridheight = 2;
        constraints.gridx = 1;
        constraints.gridy = 1;
        add(textField, constraints);
        //row 2
        JLabel mediaTypeLabel = new JLabel("Media Attachment Type: ");
        mediaTypeLabel.setPreferredSize(new Dimension(150, 20));
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.gridx = 0;
        constraints.gridy = 3;
        add(mediaTypeLabel, constraints);
        final JTextField mediaTypeField = new JTextField();
        mediaTypeField.setPreferredSize(new Dimension(300, 20));
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.gridwidth = 1;
        constraints.gridheight = 2;
        constraints.gridx = 1;
        constraints.gridy = 3;
        add(mediaTypeField, constraints);
        //row 3
        JLabel mediaURLLabel = new JLabel("Media Attachment URL: ");
        mediaURLLabel.setPreferredSize(new Dimension(150, 20));
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.gridx = 0;
        constraints.gridy = 4;
        add(mediaURLLabel, constraints);
        final JTextField mediaURLField = new JTextField();
        mediaURLField.setPreferredSize(new Dimension(300, 20));
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.gridwidth = 2;
        constraints.gridheight = 1;
        constraints.gridx = 1;
        constraints.gridy = 4;
        add(mediaURLField, constraints);
        //row 4
        JLabel destructLabel = new JLabel("Self-Destruct Time: ");
        destructLabel.setPreferredSize(new Dimension(150, 20));
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.gridx = 0;
        constraints.gridy = 5;
        add(destructLabel, constraints);
        final JTextField destructField = new JTextField();
        destructField.setPreferredSize(new Dimension(300, 20));
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.gridwidth = 2;
        constraints.gridheight = 1;
        constraints.gridx = 1;
        constraints.gridy = 5;
        add(destructField, constraints);
        final JLabel destructMsg = new JLabel("");
        constraints.gridx = 4;
        add(destructMsg);
        //row 5
        JButton sendButton = new JButton("Send Message");
        sendButton.setPreferredSize(new Dimension(150, 20));
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                boolean prerequisites = true;
                String destructText = destructField.getText();
                if(!destructText.equals("") && !destructText.matches("^\\d{4}[-]?\\d{1,2}[-]?\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$")){
                    destructMsg.setText("Format: yyyy-mm-dd hh:mm:ss");
                    prerequisites = false;
                }
                if(prerequisites){
                    String destructSend = null;
                    String typeSend = null;
                    String urlSend = null;
                    if(!destructText.equals("")){
                        destructSend = destructText;
                    }
                    if(!mediaTypeField.getText().trim().equals("")){
                        typeSend = mediaTypeField.getText();
                    }
                    if(!mediaURLField.getText().trim().equals("")){
                        urlSend = mediaURLField.getText();
                    }
                    Messenger.getmMessengerGUIInterface().newMessage(textField.getText(), destructSend,
                                                                        mUser, mChatID, typeSend, urlSend);
                    mChatTabPanel.updateState(ChatTabPanel.ChatTabStates.MESSAGE_LIST, mChatID, null);
                }
            }
        });
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.gridx = 0;
        constraints.gridy = 6;
        add(sendButton, constraints);
        JButton discardButton = new JButton("Discard Message");
        discardButton.setPreferredSize(new Dimension(150, 20));
        discardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                mChatTabPanel.updateState(ChatTabPanel.ChatTabStates.MESSAGE_LIST, mChatID, null);
            }
        });
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.gridx = 1;
        constraints.gridy = 6;
        add(discardButton, constraints);

        validate();
    }
}
