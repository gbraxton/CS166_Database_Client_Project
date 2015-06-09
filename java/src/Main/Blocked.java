package Main;

import java.io.BufferedReader;
import java.util.List;

/**
 * Created by gbraxton on 6/3/14.
 */
public class Blocked {
    public static void blockedMenu(Messenger esql, BufferedReader in, String user) throws Exception {
        int choice = 1;
        while(choice != 9) {
            System.out.print("\n\n");
            System.out.println("==================================================");
            System.out.println("===================Blocked Menu===================");
            System.out.println("==================================================");
            System.out.println("1. Browse blocked list");
            System.out.println("2. Add user to blocked list");
            System.out.println("3. Remove user from blocked list");
            System.out.println("9. Return to main menu");
            System.out.print("\nEnter choice: ");
            choice = Integer.parseInt(in.readLine());
            switch (choice) {
                case 1:
                    ListBlocked(esql, in, user);
                    break;
                case 2:
                    AddToBlocked(esql, in, user);
                    break;
                case 3:
                    removeFromBlocked(esql, in, user);
                    break;
                default:
                    break;
            }
        }
    }
    public static void AddToBlocked(Messenger esql, BufferedReader in, String user) throws Exception{
        //Get current user contact list ID
        String query = String.format("SELECT block_list FROM USR WHERE login='" + user + "';");
        List<List<String>> result = esql.executeQueryAndReturnResult(query);
        int blockListID = Integer.parseInt(result.get(0).get(0));

        //User inputs the contact to add
        System.out.println("Enter username to block");
        String userToAdd = in.readLine();

        //Verify user exists. If so add
        query = String.format("SELECT * FROM USR WHERE login='" + userToAdd + "';");
        if(esql.executeQuery(query) > 0){
            query = String.format("INSERT INTO USER_LIST_CONTAINS VALUES(" + blockListID + ", '" + userToAdd + "');");
            esql.executeUpdate(query);
        }
    }//end



    public static void ListBlocked(Messenger esql, BufferedReader in, String user) throws Exception{
        //Get current user contact list ID
        String query = String.format("SELECT block_list FROM USR WHERE login='" + user + "';");
        List<List<String>> result = esql.executeQueryAndReturnResult(query);
        int blockListID = Integer.parseInt(result.get(0).get(0));

        query = "SELECT aUser.list_member, tUser.status FROM USER_LIST_CONTAINS aUser, USR tUser WHERE aUser.list_id=" + blockListID + " AND tUser.login=aUser.list_member;";
        List<List<String>> contactList = esql.executeQueryAndReturnResult(query);
        System.out.print("\n\nBlocked Users\n-----------------------------------------------------------------\n");
        int contactCount = 1;
        for(List<String> contact : contactList){
            System.out.println(contactCount++ + ". " + contact.get(0));
            System.out.println("  Status: " + contact.get(1));
            System.out.println("-----------------------------------------------------------------");
        }
        System.out.print("\n\n");
    }//end



    public static void removeFromBlocked(Messenger esql, BufferedReader in, String user) throws Exception{
        //Get current user contact list ID
        String query = String.format("SELECT block_list FROM USR WHERE login='" + user + "';");
        List<List<String>> result = esql.executeQueryAndReturnResult(query);
        int blockListID = Integer.parseInt(result.get(0).get(0));

        System.out.println("Enter the login of the contact you want to remove");
        String removeBlock = in.readLine();

        //verify exists and in list of
        query = "SELECT * FROM USER_LIST_CONTAINS WHERE list_member='" + removeBlock + "' AND list_id=" + blockListID + ";";
        if(esql.executeQuery(query) > 0){
            query = "DELETE FROM USER_LIST_CONTAINS WHERE list_member='" + removeBlock + "' AND list_id=" + blockListID + ";";
            esql.executeUpdate(query);
            System.out.println("User " + removeBlock + " deleted from your block list");
        } else {
            System.out.println("The entered user is not on your block list");
        }
    }

}
