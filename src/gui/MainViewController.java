package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.servicies.DepartmentService;
import model.servicies.SellerService;

public class MainViewController implements Initializable{
	
	@FXML
	private MenuItem menuItemSeller;
	
	@FXML
	private MenuItem menuItemDepartment;
	
	@FXML
	private MenuItem menuItemAbout;
	
	@FXML
	public void onMenuItemSellerAction() {
		loadView("/gui/SellerList.fxml",(SellerListController controller) ->{
			controller.setSellerService(new SellerService());
			controller.updateTableView();
			
		});
	}
	
	@FXML
	public void onMenuItemDepartmentAction() {
		//Agora a função recebe dois argumentos um fxml e Expressão lambda
		loadView("/gui/DepartmentList.fxml",(DepartmentListController controller) ->{
			controller.setDepartmentService(new DepartmentService());
			controller.updateTableView();
			
		});
	}
	
	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/Aboult.fxml",x -> {});
		System.out.println("onMenuItemAbout");
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
	}
	/*symchronized garante que o metodo não será interrompido pela multthreds
	 * Esse metodo laodView carrenga uma View; Para que não tenha que
	 * criar um metodo loadView para cada ação Action atribuimos mais um paramento na função
	 * que recebe um tipo Consumer tipo generico<T>*/
	private synchronized <T> void loadView(String absoluteName,Consumer<T> initializingAction){
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVbox = loader.load();
			//pegando a view de referencia do Main
			Scene mainScene = Main.getMainScene();
			/*metodo getRoot pega o primeiro elemento da view que no caso é ScrollPane*/
			VBox mainVBox = (VBox)((ScrollPane) mainScene.getRoot()).getContent();
			//pegando o primeiro elemento da view mainVBox
			Node mainMenu = mainVBox.getChildren().get(0);
			//Limpando todos os filho mainVBox
			mainVBox.getChildren().clear();
			//Adicionando o mainMenu
			mainVBox.getChildren().add(mainMenu);
			//assAll adiciona uma coleção no caso filhos do newVbox
			mainVBox.getChildren().addAll(newVbox.getChildren());
			
			//executando a função passada como parametro
			T controller = loader.getController();
			initializingAction.accept(controller);
			
		}catch(IOException e) {
			Alerts.showAlerts("IOException", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}
	/*Foi clonado esse metodo apenas para testar DepartmentService*/
	private synchronized void loadView2(String absoluteName) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVbox = loader.load();
			//pegando a view de referencia do Main
			Scene mainScene = Main.getMainScene();
			/*metodo getRoot pega o primeiro elemento da view que no caso é ScrollPane*/
			VBox mainVBox = (VBox)((ScrollPane) mainScene.getRoot()).getContent();
			//pegando o primeiro elemento da view mainVBox
			Node mainMenu = mainVBox.getChildren().get(0);
			//Limpando todos os filho mainVBox
			mainVBox.getChildren().clear();
			//Adicionando o mainMenu
			mainVBox.getChildren().add(mainMenu);
			//assAll adiciona uma coleção no caso filhos do newVbox
			mainVBox.getChildren().addAll(newVbox.getChildren());
			
			/*Acessando o controller da view loader*/
			DepartmentListController controller = loader.getController();
			//injetando a dependencia 
			controller.setDepartmentService(new DepartmentService());	
			//Atualizando os dados na tela tableView
			controller.updateTableView();
			
		}catch(IOException e) {
			Alerts.showAlerts("IOException", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}
	

}
