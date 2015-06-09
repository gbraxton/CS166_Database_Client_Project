package GUI;

import javax.swing.*;

/**
 * Created by gbraxton on 6/7/14.
 *
 */
public class ChatTabPanel extends JPanel {
    public enum ChatTabStates{CHAT_LIST, MESSAGE_LIST, NEW_MESSAGE, NEW_CHAT}
    private ChatTabStates mState;
    private String mUser;

    ChatTabPanel(String user){
        super();
        mUser = user;
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        add(new ChatsListPanel(mUser, this));
        revalidate();
    }

    public void tabSelected(){
        updateState(ChatTabStates.CHAT_LIST, null, null);
    }

    public void updateState(ChatTabStates state, Object obj1, Object obj2){
        if(mState != state){
            mState = state;
            switch (mState){
                case CHAT_LIST:
                    removeAll();
                    add(new ChatsListPanel(mUser, this));
                    revalidate();
                    break;
                case MESSAGE_LIST:
                    removeAll();
                    add(new MessageListPanel(mUser, (String)obj1, this));
                    revalidate();
                    break;
                case NEW_MESSAGE:
                    removeAll();
                    add(new NewMessagePanel(mUser, (String)obj2, this ));
                    revalidate();
                    break;
                case NEW_CHAT:
                    removeAll();
                    add(new NewChatPanel(mUser, this));
                    revalidate();
                default:
                    break;
            }
        }

    }
}
