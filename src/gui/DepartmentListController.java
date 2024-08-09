package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.servicies.DepartmentService;
/*Classe Observer que fica escutando a outra classe*/
public class DepartmentListController implements Initializable,DataChangeListener{
	
	private DepartmentService service;
	
	private ObservableList<Department> obsList;
	
	@FXML
	private TableView<Department> tableViewDepartment;
	
	@FXML
	private TableColumn<Department, Integer> tableColumnId;
	
	@FXML
	private TableColumn<Department,String> tableColumnName;
	
	@FXML
	private TableColumn<Department, Department> tableColumnEdit;
	
	@FXML
	private TableColumn<Department, Department> tableColumnRemove;
	
	@FXML
	private Button btNew;
	
	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentsStage(event);
		Department obj = new Department();
		createDialogForm(obj,"/gui/DepartmentForm.fxml", parentStage);
	}
	/*injetando a injeção de dependencia*/
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
		
	}

	private void initializeNodes() {
		/*inicializando o comportamento das calunas padrao javaFx
		 * id e name são capturados do get das Classes */
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		/*Macete para ajustar tela department*/
		Stage stage = (Stage)Main.getMainScene().getWindow();
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
		
		
	}
	/*Responsavél por Acessar o serviço, carregar os Departemento
	 * e jogar os departamentos no obsList*/
	public void updateTableView() {
		//testando no caso de não entrar com um service
		if(service == null) {
			throw new IllegalStateException("Service é nulo");
			
		}
		//atribuindo a list dados do service.findAll
		List<Department> list = service.findAll();
		/*Carregando list dentro do obsList para que possa ser atribuido
		para um atributo da View*/ 
		obsList = FXCollections.observableArrayList(list);
		//setando ao atributo tableViewDepartmet o obsList
		tableViewDepartment.setItems(obsList);
		//criará um botao em cada linha para atualizar 
		initEditButton();
		/*Criará um botao para remover*/
		initRemoveButton();
		
	}
	/*Metodo para criar uma nova janela para o evento New do Formulario Department
	 * Recebe como argumento um fxml e um Stage no caso o evento do botao*/
	private void createDialogForm(Department obj,String absoluteName, Stage parentStage) {
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			
			//pegando o controlador da tela criada
			DepartmentFormController controller = loader.getController();
			controller.setDepartment(obj);
			controller.setDepartmentService(new DepartmentService());
			//Se escrevendo para receber um evento
			controller.subscribeDataChangeListener(this);
			
			controller.updateFormData();
			
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Department data");
			dialogStage.setScene(new Scene(pane));
			//Metodo como false diz que Redirecionamento tela negada
			dialogStage.setResizable(false);
			//metodo recebe quem será pai da janela
			dialogStage.initOwner(parentStage);
			//metodo de comportamento da janela recebe Modality.WINDOW_MODAL
			//que travará a tela 
			dialogStage.initModality(Modality.WINDOW_MODAL);
			//garrega o formulario 
			dialogStage.showAndWait();
			
			
		}
		catch(IOException e) {
			Alerts.showAlerts("IO Exception", "Error loading View ", e.getMessage(), AlertType.ERROR);
		}
	}
	//Resposavel por executar a notificação quando os dados forem alterados
	@Override
	public void ondaDataChanged() {
		updateTableView();
		
		
	}
	/*Responsavel em criar um botao Edit em cada linha da tabela
	 * Para atualizar os dados */
	public void initEditButton() {
		tableColumnEdit.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEdit.setCellFactory(param -> new TableCell<Department,Department>(){
			private final Button button = new Button("edit");
			
			@Override
			protected void updateItem(Department obj,boolean empty) {
				super.updateItem(obj, empty);
				
				if(obj == null) {
					setGraphic(null);
					return ;
				}
				setGraphic(button);
				button.setOnAction(event -> createDialogForm(obj,"/gui/DepartmentForm.fxml", Utils.currentsStage(event)));
			}
			
		});
	}
	/*responsavel por criar um botao para remover itens*/
	public void initRemoveButton() {
		tableColumnRemove.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnRemove.setCellFactory(param -> new TableCell<Department,Department>(){
			private final Button button = new Button("remove");
			
			@Override
			protected void updateItem(Department obj,boolean empty) {
				super.updateItem(obj, empty);
				
				if(obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}
	/*responsavel por remover dados*/
	private void  removeEntity(Department obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Tem certeza que deseja deletar");
		
		if(result.get() == ButtonType.OK) {
			if(service == null) {
				throw new IllegalStateException("service was null");
			}
			try {
				service.remove(obj);
				updateTableView();
				
			}catch(DbException e) {
				Alerts.showAlerts("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}
						
		}
	}

}
