package sample;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

public class Todo_List_Controller implements Initializable
{
    private static String username;
    @FXML
    private ListView<String> list_view;

    @FXML
    private ListView<Date> date_list;

    @FXML
    private Button button_new;

    @FXML
    private Button button_edit;

    @FXML
    private Button button_del;

    @FXML
    private TextArea text_area;


    public static void setUser() {
        username = "";
    }

    public static void setUser(String val)
    {
        username = val;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        //username = "nmit";
        list_view.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        populateList();
    }

    private void populateList()
    {
        Connection con = null;
        String query = "select title,created_on from "+username+";";
        list_view.getItems().clear();
        date_list.getItems().clear();
        text_area.setText("");

        try
        {
            con = ConConfig.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next())
            {
                do {
                    list_view.getItems().add(rs.getString(1));
                    date_list.getItems().add(rs.getDate(2));
                }while (rs.next());
            }
            else {
                Alert no_todo = new Alert(AlertType.INFORMATION);
                no_todo.setHeaderText(null);
                no_todo.setContentText("You currently don't have any notes.");
                no_todo.showAndWait();
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            if (con!=null){
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void getTodo()
    {
        String title = list_view.getSelectionModel().getSelectedItem();
        Connection con = null;

        if (list_view.getItems().isEmpty())
        {
            Alert add_new_todo = new Alert(AlertType.INFORMATION);
            add_new_todo.setHeaderText(null);
            add_new_todo.setContentText("Click add to add new TODO.");
            add_new_todo.showAndWait();
        }

        else
        {
            try
            {
                con = ConConfig.getConnection();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("select content from "+username+" where title = \""+title+"\";");
                if (rs.next())
                {
                    text_area.setText(rs.getString(1));
                }
                else {
                    text_area.setText("Your TODO here.");
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally {
                if (con!=null){
                    try {
                        con.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    public void addNewButtonClicked()
    {
        String title;
        boolean flag = true;
        Connection con = null;
        TextInputDialog td = new TextInputDialog();
        td.setHeaderText("Enter your TODO title.");
        td.showAndWait();
        title = td.getEditor().getText();
        ObservableList<String> titles;
        titles = list_view.getItems();

        if (title.equals(""))
        {
            Alert empty_field = new Alert(AlertType.ERROR);
            empty_field.setContentText("No Title entered!");
            empty_field.showAndWait();
        }

        else if(list_view.getItems().isEmpty()) //list is empty
        {
            //add a new title to db
            try
            {
                con = ConConfig.getConnection();
                Statement stmt = con.createStatement();
                stmt.executeUpdate("insert into "+username+" values(current_date(),\""+title+"\",\"\");");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally {
                if (con!=null){
                    try {
                        con.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

            populateList();
        }

        else //list has some items
        {
            for (String s:titles)  //check for a duplicate title
            {
                if (s.equals(title))  //a duplicate was found
                {
                    flag = false;
                    break;
                }

                else
                {flag = true;}
            }

            if (flag)  //no duplicate found
            {
                //add a new title to db
                try
                {
                    con = ConConfig.getConnection();
                    Statement stmt = con.createStatement();
                    stmt.executeUpdate("insert into "+username+" values(current_date(),\""+title+"\",\"\");");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally {
                    if (con!=null){
                        try {
                            con.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }

                populateList();
            }

            else //a duplicate was found
            {
                Alert add_new_todo = new Alert(AlertType.ERROR);
                add_new_todo.setHeaderText(null);
                add_new_todo.setContentText("This TODO already exists!");
                add_new_todo.showAndWait();
            }
        }

    }

    public void saveButtonClicked()
    {
        String title;
        String content;
        Connection con = null;
        if (!list_view.getSelectionModel().getSelectedItems().isEmpty())  //if something is selected then
        {
            title = list_view.getSelectionModel().getSelectedItem();
            content = text_area.getText();

            try
            {
                con = ConConfig.getConnection();
                Statement stmt = con.createStatement();
                stmt.executeUpdate("update "+username+" set content = \""+content+"\" where title = \""+title+"\";");
                Alert todo_saved = new Alert(AlertType.INFORMATION);
                todo_saved.setHeaderText(null);
                todo_saved.setContentText("TODO Saved.");
                todo_saved.showAndWait();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally {
                if (con!=null){
                    try {
                        con.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        else //username didn't select any title
        {
            Alert add_new_todo = new Alert(AlertType.INFORMATION);
            add_new_todo.setHeaderText(null);
            add_new_todo.setContentText("Please select a TODO to save.");
            add_new_todo.showAndWait();
        }
    }

    public void deleteButtonClicked()
    {
        String title;
        Connection con = null;
        if (!list_view.getSelectionModel().getSelectedItems().isEmpty())  //is something is selected then
        {
            title = list_view.getSelectionModel().getSelectedItem();

            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Delete TODO");
            alert.setHeaderText("Selected TODO will be deleted.");
            alert.setContentText("Are you sure?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK)
            {
                // ... username chose OK
                try
                {
                    con = ConConfig.getConnection();
                    Statement stmt = con.createStatement();
                    stmt.executeUpdate("delete from "+username+" where title =\""+title+"\";");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally {
                    if (con!=null){
                        try {
                            con.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
                populateList();
            }

            else
                {
                // ... username chose CANCEL or closed the dialog
            }
        }

        else //username didnt select any title
        {
            Alert add_new_todo = new Alert(AlertType.INFORMATION);
            add_new_todo.setHeaderText(null);
            add_new_todo.setContentText("Please select a TODO first.");
            add_new_todo.showAndWait();
        }
    }
}
