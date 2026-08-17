package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class dashboardController {

  @FXML
  private Button clickMe;

  @FXML
  void initialize() {
      //displayExpenses();
      clickMe.setOnAction(evt ->{
        try {
            System.out.println("Got Clicked");
        } catch (Exception e) {
          System.out.println(e);
        }
      });
  }

}
