package model.servicies;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentService {
	
	private DepartmentDao dao = DaoFactory.createDepartmentDao();
	
	/*Responsavél por pegar todos os dados*/
	public List<Department> findAll(){

		return dao.findAll();
	}
	//Responsavel por inserir os dados ou alterar
	public void saveOrUpdate(Department obj) {
		//verificar se trata de um novo dado ou se é uma alteração
		if(obj.getId() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
		
	}
	public void remove(Department obj) {
		dao.deleteById(obj.getId());
		
	}

}
