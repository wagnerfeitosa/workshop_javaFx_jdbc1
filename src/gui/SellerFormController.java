package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.servicies.SellerService;
/*Essa classe é Observer é Subject ou seja a classe que Emite o evento */
public class SellerFormController implements Initializable{
	
	private Seller entity;
	
	private SellerService service;
	//Lista de objetos escritos interessados em receber eventos 
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private TextField txtEmail;
	
	@FXML
	private DatePicker dbBirthDate;
	
	@FXML
	private TextField baseSalary;
	
	@FXML
	private Label labelErroName;
	
	@FXML
	private Label labelErroEmail;
	
	@FXML
	private Label labelErroBirthDate;
	
	@FXML
	private Label labelErroBaseSalary;
	
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	//injeção de independencia
	public void setSeller(Seller entity) {
		this.entity = entity;
	}
	public void setSellerService(SellerService service) {
		this.service = service;
	}
	//Resposavel em escrever listener na lista
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if(entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if(service == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			//metodo de notificação
			notifyDataChangeListeners();
			
			//Apos salvar os dado fechar a jenela
			Utils.currentsStage(event).close();;
			
		}catch(DbException e) {
			Alerts.showAlerts("Error Saving object", null, e.getMessage(), AlertType.ERROR);
			e.printStackTrace();
		}
		catch(ValidationException e) {
			setErrorMessages(e.getErros());
		}
		
	}
	//Responsavel por percorrer a lista e notificando 
	private void notifyDataChangeListeners() {
		for(DataChangeListener listener : dataChangeListeners) {
			listener.ondaDataChanged();
			
		}
		
	}
	/*Resposavel por pegar os dados do digitados no TextField
	 * e adicionar no objeto Seller */
	private Seller getFormData() {
		
		Seller obj = new Seller();
		ValidationException exception = new ValidationException("Validation Error");
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		  //trim elimina qualque espaco em branco que esteja no inicio ou na final
		if(txtName.getText() == null || txtName.getText().trim().equals("")) {
			//adicionando erro
			exception.addErros("name", "o campo não pode ser vazio");
		}
		obj.setName(txtName.getText());
		//se ouver um erro é lançado a exeption
		if(exception.getErros().size() > 0) {
			throw exception;
		}
		
		return obj;
	}
	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentsStage(event).close();;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializaNode();
		
	}
	//Metodo de restrições
	public void initializaNode() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 70);
		Constraints.setTextFieldDouble(baseSalary);
		Constraints.setTextFieldMaxLength(txtEmail, 50);
		//formatado da data
		Utils.formatDatePicker(dbBirthDate, "dd/MM/yyyy");
		
	}
	//Metodo Responsavel por pegar uma entity e popular caixa de texto do formulario
	public void updateFormData() {
		if(entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		//String.valueOf converte para string
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		baseSalary.setText(String.format("%.2f", entity.getBaseSalary()));
		/*Mostrando a data local do fuso Horario do usuario*/
		if(entity.getBirthDate() != null) {
			dbBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}
			
		
	}
	//responsavel por pegar os erros da exception e escrever os erros na tela
	private void setErrorMessages(Map<String,String> erros) {
		
		Set<String> fields = erros.keySet();
		
		if(fields.contains("name")) {
			labelErroName.setText(erros.get("name"));
		}
	}

}
