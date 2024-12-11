package TareaEvaluativa;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Ejercicio4 {

	 // Conexión
    private static final String CONNECTION_URL = "jdbc:mysql://localhost/dbeventos";
    private static final String USERNAME = "birt";
    private static final String PASSWORD = "birt";

	public static void main(String[] args) {
		
		 // Consultas
	     String select = "select id_evento, nombre_evento from eventos";
	     String existsEvento = "select * from eventos where id_evento = ?";
	     String funcion = "{? = call obtener_numero_asistentes(?)}";
	     
	     // Conexión
	     try (Connection connection = DriverManager.getConnection(CONNECTION_URL, USERNAME, PASSWORD)) {
	            
	    	 try(PreparedStatement stmt = connection.prepareStatement(select)){
	    		 ResultSet result = stmt.executeQuery();
	    		 
	    		 System.out.println("Lista de eventos:");
	    		 
	    		 // Mostrar eventos
	    		 while ( result.next()) {
	    			 int idEvento = result.getInt(1);
	    			 String evento = result.getString(2);
	    			 
	    			 System.out.println(idEvento + ". " + evento);
	    		 }
	    		 
	    		 // Pedir evento del que queremos saber el nº de asistentes
	    		 System.out.println("Introduce el ID del evento para consultar la cantidad de asistentes");
	    		 Scanner scanner = new Scanner(System.in);
	    		 int id = Integer.parseInt(scanner.nextLine());
	    		 
	    		 // Comprobar si existe el evento
	             try (PreparedStatement stmtExists = connection.prepareStatement(existsEvento)){
	             	
	            	stmtExists.setInt(1, id);
	             	ResultSet resultExists = stmtExists.executeQuery();
	             	
	             	// Si no existe el id
	             	if(!resultExists.next()) {
	             		System.out.println("No se encontró ningún evento con ese ID.");
	             		System.exit(0);
	             	// Si existe
	             	} else {
	             		
	             		// Llamada a la función almacenada
	             		try (CallableStatement callable = connection.prepareCall(funcion)) {
	             			
	             			callable.setInt(2, id);
	             			callable.registerOutParameter(1, java.sql.Types.INTEGER);
	             			callable.execute();
	             			int asistentes = callable.getInt(1);
	             			
	             			System.out.println("El número de asistentes para el evento seleccionado es: " + asistentes);
	             			
	             		}
	             		
	             	}
	             	
	             	scanner.close();
	             	
	             }
	    		 
	    	 } catch (Exception e) {
	    		 System.err.println("Error: " + e.getMessage());
	    	 }
	    	 
	    	 	            
	     } catch (SQLException e) {
	    	 System.err.println("Error al conectarse a la base de datos: " + e.getMessage());
	     }
		
	}
	
}
