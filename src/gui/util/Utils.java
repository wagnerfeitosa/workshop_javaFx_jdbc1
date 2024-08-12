package gui.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
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
	
	/*Metodo para formatar Data para uma formato passado no parametro format*/
	public static <T>void formatTableColumnDate(TableColumn<T,Date> tableColumn,String format) {
		tableColumn.setCellFactory(column ->{
			TableCell<T, Date> cell = new TableCell<T, Date>() {
				private SimpleDateFormat sdf = new SimpleDateFormat(format);
				
				@Override
				protected void updateItem(Date item,boolean empty) {
					super.updateItem(item, empty);
					if(empty) {
						setText(null);
					}else {
						setText(sdf.format(item));
					}
				}
			};
			return cell;
		});
	}
	
	/*Metodo para formatar Double por decimal passado nos parametros*/
	public static <T> void formatTableColumnDouble(TableColumn<T,Double> tableColumn, int decimalPlaces) {
		tableColumn.setCellFactory(column -> {
			TableCell<T,Double> cell = new TableCell<T, Double>(){
				
				@Override
				protected void updateItem(Double item, boolean empty) {
					super.updateItem(item, empty);
					if(empty) {
						setText(null);
					}else {
						Locale.setDefault(Locale.US);
						setText(String.format("%."+decimalPlaces+"f",item ));
					}
				}
			};
			return cell;
		});
	}

}
