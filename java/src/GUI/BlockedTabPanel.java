package GUI;

import Main.Messenger;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by gbraxton on 6/8/14.
 */
public class BlockedTabPanel extends JPanel {
    private String mUser;
    private DefaultListModel<String> mBlockedListModel;
    private JButton rmContactButton;

    BlockedTabPanel(String user){
        mUser = user;
        mBlockedListModel = new DefaultListModel<String>();
        getBlockedUsers();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setPreferredSize(new Dimension(500, 500));

        final JList<String> blockedJList = new JList<String>(mBlockedListModel);
        blockedJList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        blockedJList.setPreferredSize(new Dimension(500, 500));
        blockedJList.setCellRenderer(new ContactListCellRender());

        JScrollPane blockListScrollPane = new JScrollPane(blockedJList);
        blockListScrollPane.setPreferredSize(new Dimension(500, 500));

        add(blockListScrollPane);

        rmContactButton = new JButton("Delete Contact");
        rmContactButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int index = blockedJList.getSelectedIndex();
                String rmC = mBlockedListModel.getElementAt(index);
                mBlockedListModel.remove(index);
                Messenger.getmMessengerGUIInterface().removeBlockByUserContact(mUser, rmC);
                rmContactButton.setEnabled(false);
            }
        });
        rmContactButton.setEnabled(false);
        add(rmContactButton);

        JPanel addButtonPanel = new JPanel();
        addButtonPanel.setPreferredSize(new Dimension(500, 40));

        final JTextField addTextField = new JTextField();
        addTextField.setPreferredSize(new Dimension(120, 40));
        addButtonPanel.add(addTextField);

        JButton addButton = new JButton("Add Blocked User");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(!addTextField.getText().trim().equals("")){
                    String contact = addTextField.getText().trim();
                    if(!Messenger.getmMessengerGUIInterface().checkUserExists(contact)){
                        JOptionPane.showMessageDialog(BlockedTabPanel.this, "That user does not exists");
                    } else if(Messenger.getmMessengerGUIInterface().checkUserAlreadyBlocked(mUser, contact)){
                        JOptionPane.showMessageDialog(BlockedTabPanel.this, "That user is already blocked");
                    } else {
                        Messenger.getmMessengerGUIInterface().addBlocked(mUser, contact);
                    }
                    addTextField.setText("");
                    getBlockedUsers();
                }
            }
        });
        addButtonPanel.add(addButton);

        add(addButtonPanel);
    }

    public class ContactListCellRender extends JPanel implements ListCellRenderer<Object>{
        private JLabel nameLabel;

        ContactListCellRender(){
            super();
            setLayout(new GridLayout(2, 3));
            setPreferredSize(new Dimension(500, 80));
            setBorder(BorderFactory.createLineBorder(Color.black));

            nameLabel = new JLabel("");
            add(nameLabel);
        }
        @Override
        public Component getListCellRendererComponent(JList<?> list,
                                                      Object value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus){
            if(isSelected){
                setBackground(Color.lightGray);
                rmContactButton.setEnabled(true);
            } else {
                setBackground(Color.white);
            }

            nameLabel.setText("Name: " + value);
            return this;
        }
    }

    public void getBlockedUsers(){
        mBlockedListModel.removeAllElements();
        java.util.List<java.util.List<String>> result = Messenger.getmMessengerGUIInterface().getBlockedByUser(mUser);
        for(java.util.List<String> ls : result){
            mBlockedListModel.addElement(ls.get(0));
        }
    }

    public void tabSelected(){
        getBlockedUsers();
        revalidate();
    }
}
