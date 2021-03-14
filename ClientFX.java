

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class ClientFX extends Application {

    @Override
    public void start(Stage primaryStage) {
       
        primaryStage.setTitle("GYM Booking SYSTEM");
        
        
        Button listAll,listClient,delete,insert,listPT,ListDate,update,exit;
        
        
        
        listAll = new Button();
        listClient = new Button();
        delete = new Button();
        insert = new Button();
        listPT = new Button();
        ListDate = new Button();
        update = new Button();
        exit = new Button();
       
        
        
        listAll.setText("LISTALL");
        listAll.setMinSize(150,50);
        
              listClient.setText("LISTCLIENT");
        listClient.setMinSize(150, 50);

        delete.setText("DELETE");
        delete.setMinSize(150, 50);

        insert.setText("INSERT");
        insert.setMinSize(150, 50);

        listPT.setText("PISTPT");
        listPT.setMinSize(150, 50);

        ListDate.setText("LISTDATE");
        ListDate.setMinSize(150, 50);

        update.setText("UPDATE");
        update.setMinSize(150, 50);

        exit.setText("Exit");
        exit.setMinSize(150, 50);
        
        
        VBox Layout = new VBox(50);
        Layout.setAlignment(Pos.CENTER);

     
        
        Layout.getChildren().addAll(listAll,listPT,listClient,ListDate,delete,update,insert,exit);
       
       
        
        Scene s1 = new Scene(Layout,400,800);
        primaryStage.setScene(s1);
        primaryStage.show();
        
        
        
        listAll.setOnAction(e -> System.out.println("LISTALL"));
        listPT.setOnAction(e -> System.out.println("LISTPT"));
        listClient.setOnAction(e -> System.out.println("LISTCLIENT"));
        ListDate.setOnAction(e -> System.out.println("LISTDATE"));
        update.setOnAction(e -> System.out.println("UPDATE"));
        delete.setOnAction(e -> System.out.println("DELETE"));
        insert.setOnAction(e -> System.out.println("INSERT"));
        exit.setOnAction(e -> System.out.println("EXIT"));
        
    }

    public static void main(String[] args) {
        launch();
    }

}
