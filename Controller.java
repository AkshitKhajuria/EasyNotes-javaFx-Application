package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Controller {

    //String user;

    @FXML
    private PasswordField login_pass;

    @FXML
    private Button button_login_ok;

    @FXML
    private TextField text_login;

    @FXML
    private Button button_new_user;

    public void okbuttonClicked(ActionEvent event)
    {
        String pass = login_pass.getText();
        String user = text_login.getText();
        String query ;
        Connection con = null;
        if (pass.equals("") || user.equals("")) //null fields
        {
            Alert empty_field = new Alert(AlertType.ERROR);
            empty_field.setContentText("Empty fields not allowed!");
            empty_field.showAndWait();
        }

        else  //otherwise try to login
            {
                try {
                    con = ConConfig.getConnection();  //called from ConConfig class
                    Statement stmt = null;

                    if (con!=null){
                        System.out.println("Connection Established!");
                        stmt = con.createStatement();
                    }

                    query = "select user from users where user = \""+user+"\" and pass = \""+pass+"\";";
                    ResultSet rs = stmt.executeQuery(query);
                    //rs.next();

                    if (rs.next()) //will execute if result set is not empty
                    {
                        if (rs.getString(1).equals(user))  //check if its a registered user
                        {
                            //welcome message
                            /*Todo_List_Controller obj = new Todo_List_Controller();
                            obj.setUser(user);*/
                            Alert hello_user = new Alert(AlertType.INFORMATION);
                            hello_user.setContentText("Welcome "+user);
                            hello_user.setHeaderText(null);
                            hello_user.showAndWait();

                            //open up the editor ui
                            Todo_List_Controller t = new Todo_List_Controller();
                            t.setUser(user);
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("todo_list_window.fxml"));
                            Parent root1 = fxmlLoader.load();
                            Stage stage = new Stage();
                            stage.setScene(new Scene(root1));
                            stage.setTitle("My Easy Notes");
                            stage.setOnCloseRequest((WindowEvent event1) -> {
                                login_pass.setText("");
                                text_login.setText("");
                                button_login_ok.setDisable(false);
                                button_new_user.setDisable(false);
                            });
                            stage.show();
                            button_login_ok.setDisable(true);
                            button_new_user.setDisable(true);
                        }
                    }

                    else  //username or password not valid
                    {
                        Alert inv_input = new Alert(AlertType.ERROR);
                        inv_input.setContentText("Invalid username or password.");
                        inv_input.showAndWait();
                        login_pass.setText(null);
                    }
                }
                catch (Exception e){
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

    public void newUserButtonClicked(ActionEvent event)
    {
        String pass = new String(login_pass.getText());
        String user = text_login.getText();
        String query ;
        boolean flag = true;
        Connection con = null;
        if (pass.equals("") || user.equals("")) //null fields
        {
            Alert empty_field = new Alert(AlertType.ERROR);
            empty_field.setContentText("Empty fields not allowed!");
            empty_field.showAndWait();
        }

        else  //otherwise try to create user
        {
            try {
                con = ConConfig.getConnection();  //called from ConConfig class
                Statement stmt = null;

                if (con!=null){
                    System.out.println("Connection Established!");
                    stmt = con.createStatement();
                }

                query = "select user from users where user = \""+user+"\";";
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next())  //check if user already exists
                {
                    if (rs.getString(1).equals(user))
                    {
                        flag = false;
                        break;
                    }

                }

                if (flag)  // register new user
                {
                    query = "insert into users values(\""+user+"\",\""+pass+"\");";
                    stmt.executeUpdate(query);
                    query = "create table "+user+"(created_on date,title varchar(20) primary key,content varchar(120))";
                    stmt.executeUpdate(query);

                    Alert user_created = new Alert(AlertType.INFORMATION);
                    user_created.setHeaderText("New User Created!");
                    user_created.setContentText("User "+user+" created! PLease Login.");
                    user_created.showAndWait();
                }

                else //cant create new user
                {
                    Alert user_exists = new Alert(AlertType.ERROR);
                    user_exists.setContentText("This user name is already taken!");
                    user_exists.showAndWait();
                    text_login.setText("");
                }
            }
            catch (Exception e){
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

}
