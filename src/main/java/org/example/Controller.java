package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class Controller {

    private String operator = "";
    private int x = 0;
    private Model model = new Model();

    @FXML
    private Text result;

    @FXML
    private void operand(ActionEvent event){
        result.setText(result.getText() + ((Button) event.getSource()).getText());
    }

    @FXML
    private void operator(ActionEvent event){
        if(((Button) event.getSource()).getText().equals("=")){
            result.setText(model.calculate(operator, x, Integer.parseInt(result.getText()))+"");
        } else{
            operator = ((Button) event.getSource()).getText();
            x = Integer.parseInt(result.getText());
            result.setText("");
        }
    }
}
