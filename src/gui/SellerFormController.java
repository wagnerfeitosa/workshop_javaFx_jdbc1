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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.servicies.DepartmentService;
import model.servicies.SellerService;

/*Essa classe é Observer é Subject ou seja a classe que Emite o evento */
public class SellerFormController implements Initializable {

	private Seller entity;

	private SellerService service;

	private DepartmentService departmentService;
	// Lista de objetos escritos interessados em receber eventos
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
	private ComboBox<Department> comboboxDepartment;

	private ObservableList<Department> obsList;

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

	// injeção de independencia
	public void setSeller(Seller entity) {
		this.entity = entity;
	}

	public void setSellerService(SellerService service, DepartmentService departmentService) {
		this.service = service;
		this.departmentService = departmentService;
	}

	// Resposavel em escrever listener na lista
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			// metodo de notificação
			notifyDataChangeListeners();

			// Apos salvar os dado fechar a jenela
			Utils.currentsStage(event).close();
			;

		} catch (DbException e) {
			Alerts.showAlerts("Error Saving object", null, e.getMessage(), AlertType.ERROR);
			e.printStackTrace();
		} catch (ValidationException e) {
			setErrorMessages(e.getErros());
		}

	}

	// Responsavel por percorrer a lista e notificando
	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.ondaDataChanged();

		}

	}

	/*
	 * Resposavel por pegar os dados do digitados no TextField e adicionar no objeto
	 * Seller
	 */
	private Seller getFormData() {

		Seller obj = new Seller();
		ValidationException exception = new ValidationException("Validation Error");

		obj.setId(Utils.tryParseToInt(txtId.getText()));
		// trim elimina qualque espaco em branco que esteja no inicio ou na final
		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			// adicionando erro
			exception.addErros("name", "o campo não pode ser vazio");
		}
		obj.setName(txtName.getText());
		// se ouver um erro é lançado a exeption
		if (exception.getErros().size() > 0) {
			throw exception;
		}

		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentsStage(event).close();
		;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializaNode();

	}

	// Metodo de restrições
	public void initializaNode() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 70);
		Constraints.setTextFieldDouble(baseSalary);
		Constraints.setTextFieldMaxLength(txtEmail, 50);
		// formatado da data
		Utils.formatDatePicker(dbBirthDate, "dd/MM/yyyy");
		
		//metodo para inicializar a combobox
		initializeComboBoxDepartment();

	}

	// Metodo Responsavel por pegar uma entity e popular caixa de texto do
	// formulario
	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		// String.valueOf converte para string
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		baseSalary.setText(String.format("%.2f", entity.getBaseSalary()));
		/* Mostrando a data local do fuso Horario do usuario */
		if (entity.getBirthDate() != null) {
			dbBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}
		//getSlectionModel().selectFirst pega o primeiro elemento 
		if(entity.getDepartment() == null) {
			comboboxDepartment.getSelectionModel().selectFirst();
			
		}else {
			//caso ja tenha um department
			comboboxDepartment.setValue(entity.getDepartment());
		}
		

	}

	/* Respossvel por pegar na base de dados os dados e carregar o combobox */
	public void loadAssociatedObjects() {

		if (departmentService == null) {
			throw new IllegalStateException("DepartmentService was null");
		}
		List<Department> list = departmentService.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboboxDepartment.setItems(obsList);

	}
	
	
//Resposavel por inicializar a combobox
	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};

		comboboxDepartment.setCellFactory(factory);
		comboboxDepartment.setButtonCell(factory.call(null));
	}

	// responsavel por pegar os erros da exception e escrever os erros na tela
	private void setErrorMessages(Map<String, String> erros) {

		Set<String> fields = erros.keySet();

		if (fields.contains("name")) {
			labelErroName.setText(erros.get("name"));
		}
	}

}
