package GUI;

import Main.Messenger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by gbraxton on 6/9/14.
 */
public class CreateUserPanel extends JPanel{
    CreateUserPanel(){
        super();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setPreferredSize(new Dimension(500, 500));

        JLabel userLabel = new JLabel("username");
        add(userLabel);
        final JTextField userField = new JTextField();
        add(userField);
        JLabel passLabel = new JLabel("Password");
        add(passLabel);
        final JPasswordField passField = new JPasswordField();
        add(passField);
        JLabel phoneLabel = new JLabel("Phone number");
        add(phoneLabel);
        final JTextField phoneField = new JTextField();
        add(phoneField);
        JLabel statusLabel = new JLabel("Status (optional)");
        add(statusLabel);
        final JTextField statusField = new JTextField();
        add(statusField);
        final JLabel messageLabel = new JLabel("");
        JButton createButton = new JButton("Create User");
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(userField.getText().trim().equals("")){
                    messageLabel.setText("You must enter a username");
                } else if(passField.getPassword().toString().equals("")){
                    messageLabel.setText("You must enter a password");
                } else if(phoneField.getText().trim().equals("")){
                    messageLabel.setText("You must enter a phone number");
                } else if(Messenger.getmMessengerGUIInterface().checkUserExists(userField.getText().trim())){
                    messageLabel.setText("That username is already take");
                } else {
                    Messenger.getmMessengerGUIInterface().createUser(userField.getText().trim(), passField.getPassword().toString(), phoneField.getText().trim(), statusField.getText().trim());
                    MessengerGUI.setUser(userField.getText().trim());
                    MessengerGUI.updateGuiState(MessengerGUI.GuiStates.MAIN_USER);
                }
            }
        });
        add(createButton);
    }
}
