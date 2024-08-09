package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class Utils {
	//Metodo realiza a captura do evento de um botao retornando em um Stage
	public static Stage currentsStage(ActionEvent event) {
		return (Stage) ((Node)event.getSource()).getScene().getWindow();
	}
	
	/*Para que não possamos repetir a implementação foi criado esse metodo
	 * Resposavel de converter uma string em inteiro caso a string não seja um 
	 * numero inteiro valido metodo retonará nulo*/
	public static Integer tryParseToInt(String str) {
		try{
			return Integer.parseInt(str); 
		}catch(NumberFormatException e) {
			return null;
		}
	}

}
