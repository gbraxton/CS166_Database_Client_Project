package GUI;

import javax.swing.*;
import java.awt.*;

/**
 * Created by gbraxton on 6/5/14.
 *
 */

public class MessengerGUI implements Runnable{
    private static JFrame mMainFrame;
    private static String mUser;

    public static enum GuiStates {NOT_LOGGED_IN, MAIN_USER, CREATE_USER}
    private static GuiStates mGuiState = GuiStates.NOT_LOGGED_IN;

    public void run() {
        initialFrameSetup();
    }

    private void initialFrameSetup(){
        mMainFrame = new JFrame("Messenger");
        mMainFrame.setDefaultLookAndFeelDecorated(true);
        mMainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mMainFrame.getContentPane().setPreferredSize(new Dimension(600, 600));
        mMainFrame.setVisible(true);
        mMainFrame.getContentPane().setLayout(new BoxLayout(mMainFrame.getContentPane(), BoxLayout.PAGE_AXIS));

        mMainFrame.getContentPane().add(new NotLoggedInPanel());
        mMainFrame.pack();
    }

    public static void updateGuiState(GuiStates state) {
        if(mGuiState != state) {
            mGuiState = state;
            switch (mGuiState) {
                case NOT_LOGGED_IN:
                    mMainFrame.getContentPane().removeAll();
                    mMainFrame.getContentPane().add(new NotLoggedInPanel());
                    mMainFrame.revalidate();
                    mMainFrame.repaint();
                    break;
                case MAIN_USER:
                    mMainFrame.getContentPane().removeAll();
                    mMainFrame.getContentPane().add(new MainUserPanel(mUser));
                    mMainFrame.getContentPane().revalidate();
                    mMainFrame.getContentPane().repaint();
                    break;
                case CREATE_USER:
                    mMainFrame.getContentPane().removeAll();
                    mMainFrame.getContentPane().add(new CreateUserPanel());
                    mMainFrame.getContentPane().revalidate();
                    mMainFrame.getContentPane().repaint();
                default:
                    break;
            }
        }
    }

    public static void setUser(String user){
        mUser = user;
    }
}
