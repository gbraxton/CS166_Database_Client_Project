package GUI;

import Main.Messenger;

import javax.swing.*;
import java.util.List;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by gbraxton on 6/5/14.
 *
 */
public class NotLoggedInPanel extends JPanel {
    private MessengerGUI mMessengerGui;
    NotLoggedInPanel(){
        super();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBackground(Color.white);
        setPreferredSize(new Dimension(500, 500));

        add(Box.createRigidArea(new Dimension(500, 40)));

        JLabel greetingLabel1 = new JLabel("Welcome to Messenger");
        greetingLabel1.setFont(new Font("Verdana", Font.PLAIN, 25));
        greetingLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(greetingLabel1);

        JLabel greetingLabel3 = new JLabel("by George Braxton, Nicolas Lawler");
        greetingLabel3.setFont(new Font("Verdana", Font.PLAIN, 18));
        greetingLabel3.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(greetingLabel3);

        JLabel greetingLabel2 = new JLabel("Log in to proceed");
        greetingLabel2.setFont(new Font("Verdana", Font.PLAIN, 20));
        greetingLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(greetingLabel2);

        final UsernameFieldPanel usernameFieldPanel = new UsernameFieldPanel();
        add(usernameFieldPanel);

        final PasswordPanel passwordPanel = new PasswordPanel();
        add(passwordPanel);

        final IncorrectLoginMessagePanel incorrectLoginMessagePanel = new IncorrectLoginMessagePanel();
        add(incorrectLoginMessagePanel);

        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(60, 20));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if( Messenger.getmMessengerGUIInterface().login(
                            usernameFieldPanel.mUsernameField.getText(),
                            new String(passwordPanel.mPasswordField.getPassword())) ){

                    incorrectLoginMessagePanel.clearMessage();
                    MessengerGUI.setUser(usernameFieldPanel.mUsernameField.getText());
                    MessengerGUI.updateGuiState(MessengerGUI.GuiStates.MAIN_USER);
                } else {
                    incorrectLoginMessagePanel.showMessage();
                }
            }
        });

        add(loginButton);
        JButton createUserButton = new JButton("Create New User");
        createUserButton.setAlignmentX(CENTER_ALIGNMENT);
        createUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                MessengerGUI.updateGuiState(MessengerGUI.GuiStates.CREATE_USER);
            }
        });
        add(createUserButton);

        add(Box.createRigidArea(new Dimension(500, 350)));
    }

    public class UsernameFieldPanel extends JPanel {
        public JTextField mUsernameField;
        UsernameFieldPanel() {
            super();
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            setPreferredSize(new Dimension(500, 20));

            add(Box.createRigidArea(new Dimension(150, 20)));

            JLabel usernameLabel = new JLabel("username:");
            usernameLabel.setPreferredSize(new Dimension(80, 20));
            add(usernameLabel);

            mUsernameField = new JTextField();
            mUsernameField.setPreferredSize(new Dimension(120, 20));
            add(mUsernameField);

            add(Box.createRigidArea(new Dimension(150, 20)));
        }
    }

    public class PasswordPanel extends JPanel {
        public JPasswordField mPasswordField;
        PasswordPanel(){
            super();
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            setPreferredSize(new Dimension(500, 20));

            add(Box.createRigidArea(new Dimension(150, 20)));

            JLabel passwordLabel = new JLabel("password:");
            passwordLabel.setPreferredSize(new Dimension(80, 20));
            add(passwordLabel);

            mPasswordField = new JPasswordField();
            mPasswordField.setPreferredSize(new Dimension(120, 20));
            add(mPasswordField);

            add(Box.createRigidArea(new Dimension(150, 20)));
        }
    }

    public class IncorrectLoginMessagePanel extends JPanel {
        private JLabel mMessageLabel;
        IncorrectLoginMessagePanel(){
            super();
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            setPreferredSize(new Dimension(500, 20));

            add(Box.createRigidArea(new Dimension(130, 20)));

            mMessageLabel = new JLabel("");
            mMessageLabel.setPreferredSize(new Dimension(240, 20));
            add(mMessageLabel);

            add(Box.createRigidArea(new Dimension(130, 20)));
        }

        public void showMessage(){
            mMessageLabel.setText("Incorrect username and/or password");
        }

        public void clearMessage(){
            mMessageLabel.setText("");
        }
    }
}
