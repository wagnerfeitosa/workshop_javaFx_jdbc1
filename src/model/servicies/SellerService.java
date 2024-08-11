package model.servicies;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Seller;

public class SellerService {
	
	private SellerDao dao = DaoFactory.createSellerDao();
	
	/*Responsavél por pegar todos os dados*/
	public List<Seller> findAll(){

		return dao.findAll();
	}
	//Responsavel por inserir os dados ou alterar
	public void saveOrUpdate(Seller obj) {
		//verificar se trata de um novo dado ou se é uma alteração
		if(obj.getId() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
		
	}
	public void remove(Seller obj) {
		dao.deleteById(obj.getId());
		
	}

}
