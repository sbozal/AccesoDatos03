package TareaEvaluativa;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Ejercicio2 {
	    
	    // Conexión
	    private static final String CONNECTION_URL = "jdbc:mysql://localhost/dbeventos";
	    private static final String USERNAME = "birt";
	    private static final String PASSWORD = "birt";
	    
	    public static void main(String[] args) {
	    	
	        try (Connection connection = DriverManager.getConnection(CONNECTION_URL, USERNAME, PASSWORD)) {
	        	
	            datos(connection);	
	            
	        } catch (SQLException e) {
	            System.err.println("Error al conectarse a la base de datos: " + e.getMessage());
	        }
	        
	    }
	    
	    // Método mostrarDatos
	    private static void datos(Connection connection) {
	    	
	    	// Consulta a la base de datos
			String query = "select * from ubicaciones where nombre = ?";
			String update = "update ubicaciones set capacidad = ? where nombre = ?";
			
			try (PreparedStatement stmt = connection.prepareStatement(query)) {

	    		// Pedir ubicación
    			Scanner scanner = new Scanner(System.in);
    			System.out.println("Introduce el nombre de la ubicación:");
    			String ubicacionIntroducida = scanner.nextLine();
    			stmt.setString(1, ubicacionIntroducida);
    			
    			ResultSet result = stmt.executeQuery();
	    	  			
    			// Si la ubicación introducida no existe dentro de la base de datos
    			if (!result.next()) {
    				System.out.println("No existe esa ubicación.");
    			
    			// Si existe
    			} else {
    				System.out.println("La capacidad actual de la ubicación " + ubicacionIntroducida + " es: " + result.getInt(4));
    			
	    			// Actualizar información
	                try(PreparedStatement stmtActualizar = connection.prepareStatement(update)) {
	                    System.out.println("Introduce la nueva capacidad máxima: ");
	                    String capacidadIntroducida = scanner.nextLine();
	                    
	                    // Si no se introduce el nombre no se actualiza
	                    if (capacidadIntroducida.isEmpty()) {
	                    	System.exit(0);
	                    } else {
	                    	stmtActualizar.setString(1, capacidadIntroducida);
	                        stmtActualizar.setString(2, ubicacionIntroducida);
	                        
	                        stmtActualizar.executeUpdate();
	
	                        System.out.println("Capacidad actualizada correctamente.");
	                    }
	                    
	                 scanner.close();
	                 
	                } catch (Exception e) {
	    	    		System.err.println("Error: " + e.getMessage());
	    	    	}
    			}
				
				
			} catch (Exception e) {
	    		System.err.println("Error: " + e.getMessage());
	    	}
					
	    }
	    
	    
	
}
