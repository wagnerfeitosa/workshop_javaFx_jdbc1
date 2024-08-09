package model.exceptions;

import java.util.HashMap;
import java.util.Map;

/*Um formulario pode conter varios campos para validar*/
public class ValidationException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	//para guardar cada erro especificos
	private Map<String,String> erros = new HashMap<>();
	
	public ValidationException(String msg) {
		super(msg);
	}
	
	public Map<String, String> getErros(){
		return erros;
	}
	//adicionar errros
	public void addErros(String fieldName,String errorMessage) {
		erros.put(fieldName, errorMessage);
	}
	

}
