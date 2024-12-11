package TareaEvaluativa;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Ejercicio1 {

	// Consulta a la base de datos
	private static final String QUERY = "select \r\n"
											+ "	nombre_evento,\r\n"
											+ " count(dni) as numasistentes,\r\n"
											+ " nombre,\r\n"
											+ " direccion\r\n"
										+ "from eventos eventos\r\n"
										+ "left join \r\n"
											+ "	ubicaciones ubicaciones\r\n"
											+ "on eventos.id_ubicacion = ubicaciones.id_ubicacion\r\n"
										+ "left join\r\n"
											+ "	asistentes_eventos asistentes\r\n"
											+ "on eventos.id_evento = asistentes.id_evento\r\n"
										+ "group by nombre_evento,nombre,direccion";
    
    // Conexión
    private static final String CONNECTION_URL = "jdbc:mysql://localhost/dbeventos";
    private static final String USERNAME = "birt";
    private static final String PASSWORD = "birt";
    
    public static void main(String[] args) {
    	
        try (Connection connection = DriverManager.getConnection(CONNECTION_URL, USERNAME, PASSWORD)) {
            mostrarDatos(connection);
            
        } catch (SQLException e) {
            System.err.println("Error al conectarse a la base de datos: " + e.getMessage());
        }
        
    }
    
    // Método mostrarDatos
    private static void mostrarDatos(Connection connection) {
    	
    	try (PreparedStatement stmt = connection.prepareStatement(QUERY);
    			ResultSet eventosRs = stmt.executeQuery()) {

    			// Mostrar el resultado por consola
    			// Cebecera
	    		String encabezado = String.format("%1$30s %2$15s %3$40s %4$40s", "Evento", "Asistentes", "Ubicación", "Dirección");
	            System.out.println(encabezado);
	            System.out.println("----------------------------------------------------------------------------------------------------------------------------------------");
    			
	            // Recorrer la consulta
	            while (eventosRs.next()) {
                    String evento = eventosRs.getString(1);
                    int asistentes = eventosRs.getInt(2);
                    String ubicacion = eventosRs.getString(3);
                    String direccion = eventosRs.getString(4);

                    String result = String.format("%1$30s %2$15s %3$40s %4$40s", evento, asistentes, ubicacion, direccion);
                    System.out.println(result);
                }
               	
    	} catch (SQLException e) {
    		System.err.println("Error al obtener datos de los eventos: " + e.getMessage());
    	}
    	
    }
	
}
