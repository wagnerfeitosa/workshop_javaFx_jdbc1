package db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DB {
	//Classe Connection responsavel para criar a conexao com o banco
	private static Connection conn = null;
	
	//METODO PARA CONEXAO
	public static Connection getConnection() {
		if(conn == null) {
			try {
				//	pegando as propriedades do banco de dados
				Properties props = loadProperties();
				String url = props.getProperty("dburl");
				
				//DriverManager é a classe responsavel de fato pela conexao com banco
				conn = DriverManager.getConnection(url,props);
			}catch(SQLException e) {
				//Tratando a Exceção personalizada
				throw new DbException(e.getMessage());
			}
		
		}
		return conn;
	}
	//Metodo para fechar a conexao
	public static void CloserConnection() {
		if(conn != null) {
			try {
				conn.close();
		}catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
	}
	}
	
	//METODO PARA CARREGAR AS PROPRIEDADES QUE ESTÃO DEFINIDAS NO ARQUIVO db.properties
	private static Properties loadProperties() {
		try(FileInputStream fs = new FileInputStream("db.properties")){
			Properties props = new Properties();
			props.load(fs);
			return props;
			
		}catch(IOException e) {
			throw new DbException(e.getMessage());
		}
	}
	public static void closeStatement(Statement st) {
		if(st != null) {
			try {
				st.close();
			
		}catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
	}
	
	}
	public static void closeResultSet(ResultSet rs) {
		if(rs != null) {
			try {
				rs.close();
				
			}catch(SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}
	public static void main(String[] args) {
		System.out.println(DB.loadProperties());
	}
	
}
