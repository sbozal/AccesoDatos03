package TareaEvaluativa;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Ejercicio3 {
	
    // Conexión
    private static final String CONNECTION_URL = "jdbc:mysql://localhost/dbeventos";
    private static final String USERNAME = "birt";
    private static final String PASSWORD = "birt";

	public static void main(String[] args) {
	
		// Consultas
		String listaEventos = 	"Select " + 
									"e.id_evento, " +  
									"e.nombre_evento, " +
									"u.capacidad-s.asistentes_evento as aforo_disponible " +
								"from eventos e " +
								"left join ubicaciones u " +
								"on e.id_ubicacion=u.id_ubicacion " +
								"left join " + 
								"( " +
									"select " + 
										"id_evento, " +
										"count(distinct dni) as asistentes_evento " +
									"from asistentes_eventos " +
									"group by id_evento " +
								") as s " +
								"on e.id_evento=s.id_evento";	
		String existsDNI = "Select * from asistentes where dni = ?";
		String existsEvento = "Select * from eventos where id_evento = ?";
		String insertAsistente = "Insert into asistentes (dni, nombre) values (?,?)";
		String aforoEvento =
							"Select " + 
								"e.id_evento, " +  
								"e.nombre_evento, " +
								"u.capacidad-s.asistentes_evento as aforo_disponible " +
							"from eventos e " +
							"left join ubicaciones u " +
							"on e.id_ubicacion=u.id_ubicacion " +
							"left join " + 
								"( " +
									"select " + 
										"id_evento, " +
										"count(distinct dni) as asistentes_evento " +
										"from asistentes_eventos " +
										"group by id_evento " +
								") as s " +
							"on e.id_evento=s.id_evento " +
							"where e.id_evento = ?";	
		String inscripcionEvento = "insert into asistentes_eventos (dni, id_evento) values (?,?)";
		
		
		// Formato DNI
	    String patronDNI = "^\\d{8}[a-zA-Z]$";
	    Pattern pattern = Pattern.compile(patronDNI);
		
	    // Conexión a la base de datos
	    try (Connection connection = DriverManager.getConnection(CONNECTION_URL, USERNAME, PASSWORD)) {
        	
	    	// Variables dni
	    	String nombre = "";
            String dni = "";
	    	Scanner scanner = new Scanner(System.in);
            boolean dniValido = false;	 
            
            // Pedimos que el usuario introduzca el dni hasta que introduzca uno con el formato correcto
            while (!dniValido) {
                System.out.println("Introduce el DNI del asistente");
                dni = scanner.nextLine();
                if (pattern.matcher(dni).matches()) {
                	dniValido = true;
                }
            } 
            
            
            // Comprobamos si el dni existe en la tabla de asistentes
            try (PreparedStatement stmtDNI = connection.prepareStatement(existsDNI)){
            	
            	stmtDNI.setString(1, dni);
            	ResultSet resultDNI = stmtDNI.executeQuery();
            	
            	// Si no existe lo añadimos
            	if(!resultDNI.next()) {
            		System.out.println("No se encontró un asistente con el DNI proporcionado.");
            		System.out.println("Introduce el nombre del asistente:");
                    String nuevoAsistente = scanner.nextLine();
                    
                    try (PreparedStatement stmtNuevoAsistente = connection.prepareStatement(insertAsistente)){
                    	
                    	stmtNuevoAsistente.setString(1, dni);
                    	stmtNuevoAsistente.setString(2, nuevoAsistente);
                    	stmtNuevoAsistente.executeUpdate();
                    	nombre = nuevoAsistente;
                    }
                 
                // Si ya existe dentro de la base de datos obtenemos el nombre
            	} else {
            		nombre = resultDNI.getString(2);
            	}
            	
            	// Indicamos a nombre de quien se realiza la reserva
            	System.out.println("Estás realizando la reserva para: " + nombre);
            	
            	
            	// Mostrar listado de eventos
            	try (PreparedStatement stmtEventos = connection.prepareStatement(listaEventos)) {
            		
            		ResultSet resultEventos = stmtEventos.executeQuery();
            		
            		// Mostrar los eventos en consola
            		while (resultEventos.next()) {
            			
            			int idEvento = resultEventos.getInt(1);
            			String evento = resultEventos.getString(2);
            			int espaciosDisponibles = resultEventos.getInt(3);
            			
            			System.out.println(idEvento + ". " + evento + " - Espacios disponibles: " + espaciosDisponibles);
            			
            		}
            		
            	}
            	
            	// Elegir evento
            	System.out.println("Elige el número del evento al que quiere asistir");
            	String eventoElegido = scanner.nextLine();
            	int idEventoElegido = Integer.parseInt(eventoElegido);
            	
            	// Comprobamos si existe el evento seleccionado
                try (PreparedStatement stmtIDEvento = connection.prepareStatement(existsEvento)){
                	
                	stmtIDEvento.setInt(1, idEventoElegido);
                	ResultSet resultIDEvento = stmtIDEvento.executeQuery();
                	
                	// Si no existe salimos
                	if(!resultIDEvento.next()) {
                		System.out.println("No existe el evento seleccionado");
                    	System.exit(0);
                	// Si existe controlamos el aforo
                	} else {

                		try (PreparedStatement stmtAforo = connection.prepareStatement(aforoEvento)){
                			
                			stmtAforo.setInt(1, idEventoElegido);
                			ResultSet resultAforo = stmtAforo.executeQuery();
                			int aforo = -1;
                			
                			if (resultAforo.next()) {
                				aforo = resultAforo.getInt(3);
                			}
                			
                			// Si el aforo está completo
                			if (aforo == 0) {
                				System.out.println("Aforo completo. No puede registrarse para el evento seleccionado.");
                				System.exit(0);
                			// Si hay plazas disponibles
                			} else {
                				
                				try (PreparedStatement stmtInscripcion = connection.prepareStatement(inscripcionEvento)) {
                					
                					stmtInscripcion.setString(1, dni);
                					stmtInscripcion.setInt(2, idEventoElegido);
                					
                					stmtInscripcion.executeUpdate();
                					
        	                        System.out.println(nombre + " ha sido registrado para el evento seleccionado.");
                					
                				}
                				
                			}
                			
                		}
                		
                	}
                		
                }
            	
                scanner.close();
            	
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            } 
            
        } catch (SQLException e) {
            System.err.println("Error al conectarse a la base de datos: " + e.getMessage());
        }
	}
	
	
	
}
