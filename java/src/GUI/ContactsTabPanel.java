package GUI;

import Main.Messenger;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Created by gbraxton on 6/8/14.
 *
 */
public class ContactsTabPanel extends JPanel {
    private String mUser;
    private DefaultListModel<ContactObject> mContactListModel;
    private JList<ContactObject> contactJList;
    private JButton rmContactButton;

    ContactsTabPanel(String user){
        mUser = user;
        mContactListModel = new DefaultListModel<ContactObject>();
        getContacts();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setPreferredSize(new Dimension(500, 500));

        contactJList = new JList<ContactObject>(mContactListModel);
        contactJList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        contactJList.setCellRenderer(new ContactListCellRender());
        contactJList.setPreferredSize(new Dimension(500, 500));

        JScrollPane contactListScrollPane = new JScrollPane(contactJList);
        contactListScrollPane.setPreferredSize(new Dimension(500, 500));

        add(contactListScrollPane);

        rmContactButton = new JButton("Delete Contact");
        rmContactButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int index = contactJList.getSelectedIndex();
                String rmC = mContactListModel.getElementAt(index).user;
                mContactListModel.remove(index);
                Messenger.getmMessengerGUIInterface().removeContactByUserContact(mUser, rmC);
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

        JButton addButton = new JButton("Add Contact");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(!addTextField.getText().trim().equals("")){
                    String contact = addTextField.getText().trim();
                    if(!Messenger.getmMessengerGUIInterface().checkUserExists(contact)){
                        JOptionPane.showMessageDialog(ContactsTabPanel.this, "That user does not exists");
                    } else if(Messenger.getmMessengerGUIInterface().checkUserAlreadyContact(mUser, contact)){
                        JOptionPane.showMessageDialog(ContactsTabPanel.this, "That user is already a contact");
                    } else {
                        Messenger.getmMessengerGUIInterface().addContact(mUser, contact);
                    }
                    addTextField.setText("");
                    getContacts();
                }
            }
        });
        addButtonPanel.add(addButton);

        add(addButtonPanel);
    }

    public void getContacts(){
        mContactListModel.removeAllElements();
        List<List<String>> result = Messenger.getmMessengerGUIInterface().getContactsByUser(mUser);
        for(List<String> ls : result){
            mContactListModel.addElement(new ContactObject(ls.get(0), ls.get(1), ls.get(2)));
        }
    }

    public void tabSelected(){
        getContacts();
        revalidate();
    }

    public class ContactObject{
        String user;
        String status;
        String number;
        ContactObject(String u, String s, String n){
            user = u;
            status = s;
            number = n;
        }
    }

    public class ContactListCellRender extends JPanel implements ListCellRenderer<Object>{
        private JLabel nameLabel;
        private JLabel statusLabel;
        private JLabel numLabel;

        ContactListCellRender(){
            super();
            setLayout(new GridLayout(2, 3));
            setPreferredSize(new Dimension(500, 80));
            setBorder(BorderFactory.createLineBorder(Color.black));

            nameLabel = new JLabel("");
            statusLabel = new JLabel("");
            numLabel = new JLabel("");
            add(nameLabel);
            add(numLabel);
            add(statusLabel);
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

            nameLabel.setText("Name: " + ((ContactObject)value).user);
            statusLabel.setText("<html>Status: " + ((ContactObject)value).status + "</html>");
            numLabel.setText("<html>Number: " + ((ContactObject)value).number + "</html>");
            return this;
        }
    }
}
