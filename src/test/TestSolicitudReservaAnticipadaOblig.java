package test;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;

import modelo.gestoresplazas.GestorLocalidad;
import modelo.gestoresplazas.GestorZona;
import modelo.gestoresplazas.huecos.Hueco;
import modelo.reservas.solicitudesreservas.SolicitudReserva;
import modelo.reservas.solicitudesreservas.SolicitudReservaAnticipada;
import modelo.reservas.solicitudesreservas.TEnumPrioridad;
import modelo.vehiculos.Vehiculo;

public class TestSolicitudReservaAnticipadaOblig {
	
	@Rule //Se establece un time out general para todos los tests. Se debe comentar esta línea y la de abajo para depurar
    public TestRule  globalTimeout = new DisableOnDebug(Timeout.millis(100)); // 100 milisegundos máximos por test	
	

	private GestorLocalidad gestorLocalidad;
	private int[][] plazas = {{1, 1},
							  {3, 4}};
	private double[][] precios = {{1.0, 1.0}, {1.0, 2.0}};


	@Before
	public void setUp() {
		gestorLocalidad = new GestorLocalidad(plazas, precios);
	}
	
	/**
	 * Comprueba la definición de los atributos heredados de la clase SolicitudReserva
	 */
	@Test	
	public void testAtributosSolicitudReserva() {
		Class<?> className = SolicitudReserva.class;
		String[] attrNames = {"iZona", "jZona", "tInicial", "tFinal", "vehiculo", "gestorZona", "hueco"};
		Type[] attrTypes = {int.class, int.class, LocalDateTime.class, LocalDateTime.class,
				Vehiculo.class, GestorZona.class, Hueco.class};
		Type[] attrSpecialization = {null, null, null, null, null, null, null};
		boolean[] debeSerPrivado = {true, true, true, true, true, true, true};
		boolean[] debeSerEstatico = {false, false, false, false, false, false, false}; 
		boolean[] debeSerFinal = {false, false, false, false, false, false, false}; 
		boolean[] debeSerPublico = {false, false, false, false, false, false, false};
				
		checkAttributes(className, attrNames, attrTypes, attrSpecialization,
				debeSerPrivado, debeSerEstatico, debeSerFinal, debeSerPublico);
	}
	
	/**
	 * Comprueba si el método heredado esValida() es correcto.
	 * Se comprueba una solicitud anticipada válida y 3 incorrectas:
	 * 1 y 2 por coordenadas incorrectas, y
	 * 3 por intervalo de tiempo imposible (Ti>Tf)
	 */
	@Test
	public void testEsValida() {
		LocalDateTime tI = LocalDateTime.of(2021, 10, 5, 1, 0);
		LocalDateTime tF = LocalDateTime.of(2021, 10, 5, 2, 30);
		Vehiculo car = new Vehiculo("7883CTB");
		
		SolicitudReservaAnticipada solicitud = new SolicitudReservaAnticipada(0, 1, tI, tF, car, TEnumPrioridad.BAJA);
		
		assertTrue("No se ha clasificado correctamente una solicitud válida", solicitud.esValida(gestorLocalidad));

		// solicitudes inválidas por coords incorrectas
		
		SolicitudReservaAnticipada solicitud1 = new SolicitudReservaAnticipada(2, 1, tI, tF, car, TEnumPrioridad.BAJA);

		assertFalse("No se ha clasificado correctamente una solicitud con coordenadas incorrectas", solicitud1.esValida(gestorLocalidad));

		SolicitudReservaAnticipada solicitud2 = new SolicitudReservaAnticipada(1, 2, tI, tF, car, TEnumPrioridad.BAJA);

		assertFalse("No se ha clasificado correctamente una solicitud con coordenadas incorrectas", solicitud2.esValida(gestorLocalidad));

		// solicitud inválida por tiempos incorrectos
		
		SolicitudReservaAnticipada solicitud3 = new SolicitudReservaAnticipada(1, 1, tF, tI, car, TEnumPrioridad.BAJA);

		assertFalse("No se ha clasificado correctamente una solicitud con un instance de inicio posterior al de finalización", solicitud3.esValida(gestorLocalidad));
	
		car.setSancionado(true);
		
		SolicitudReservaAnticipada solicitud4 = new SolicitudReservaAnticipada(0, 1, tI, tF, car, TEnumPrioridad.BAJA);
		
		assertFalse("No se ha clasificado correctamente una solicitud con un vehículo sancionado", solicitud4.esValida(gestorLocalidad));
	}
	
	/**
	 * Comprueba el método gestionarSolicitudReserva() cuando se completa la reserva porque hay hueco
	 */
	@Test
	public void testGestionarReservaCompletada() {
		LocalDateTime tI = LocalDateTime.of(2021, 10, 5, 1, 0);
		LocalDateTime tF = LocalDateTime.of(2021, 10, 5, 2, 30);
		Vehiculo car = new Vehiculo("7883CTB");
		
		SolicitudReservaAnticipada solicitud = new SolicitudReservaAnticipada(0, 1, tI, tF, car, TEnumPrioridad.BAJA);
		solicitud.gestionarSolicitudReserva(gestorLocalidad);
		
		assertEquals("No se ha guardado el gestor de zona correcto en la reserva", gestorLocalidad.getGestorZona(solicitud.getIZona(), 
				solicitud.getJZona()), solicitud.getGestorZona());
		
		Hueco hueco = solicitud.getHueco();
		
		assertTrue("No se ha guardado el hueco en la reserva", hueco != null);
		
		assertTrue("No se ha guardado el hueco reservado en el gestor de zona", gestorLocalidad.existeHuecoReservado(hueco, solicitud.getIZona(), 
				solicitud.getJZona()));		
	}
	
	/**
	 * Comprueba el método gestionarSolicitudReserva() cuando la reserva queda en espera
	 */
	@Test
	public void testGestionarReservaPospuesta() {
		LocalDateTime tI = LocalDateTime.of(2021, 10, 5, 1, 0);
		LocalDateTime tF = LocalDateTime.of(2021, 10, 5, 2, 30);
		Vehiculo car = new Vehiculo("7883CTB");
		
		SolicitudReserva solicitud = new SolicitudReservaAnticipada(0, 1, tI, tF, car, TEnumPrioridad.BAJA);
		solicitud.gestionarSolicitudReserva(gestorLocalidad);
		
		// se intentar realizar una segunda reserva sin éxito
		
		LocalDateTime tI1 = LocalDateTime.of(2021, 10, 5, 1, 10);
		LocalDateTime tF1 = LocalDateTime.of(2021, 10, 5, 2, 00);
		Vehiculo car1 = new Vehiculo("5813ESB");
		SolicitudReserva solicitud1 = new SolicitudReservaAnticipada(0, 1, tI1, tF1, car1, TEnumPrioridad.BAJA);

		solicitud1.gestionarSolicitudReserva(gestorLocalidad);

		Hueco hueco = solicitud1.getHueco();
		
		assertTrue("El hueco en la reserva debería ser null", hueco == null);		
	}
	
	private void checkAttributes(Class<?> className, String[] attrNames,
			Type[] attrTypes, Type[] attrSpecialization, boolean[] debeSerPrivado,
			boolean[] debeSerEstatico, boolean[] debeSerFinal, boolean[] debeSerPublico){
		Field atributo=null;
		for (int i=0;i<attrNames.length;i++){
			try {
				atributo = className.getDeclaredField(attrNames[i]);
				assertNotEquals("Debe declarar el atributo "+attrNames[i]+" en la clase "+className, null, atributo );
				
				assertEquals("Debe declarar el atributo "+attrNames[i]+" como "+(debeSerPrivado[i]?"":"NO")+" PRIVADO en la clase "+className, debeSerPrivado[i],Modifier.isPrivate(atributo.getModifiers()) );
				assertEquals("Debe declarar el atributo "+attrNames[i]+" como "+(debeSerEstatico[i]?"":"NO")+" DE CLASE en la clase "+className, debeSerEstatico[i],Modifier.isStatic(atributo.getModifiers()) );
				assertEquals("Debe declarar el atributo "+attrNames[i]+" como "+(debeSerFinal[i]?"":"NO")+" CONSTANTE (final) en la clase "+className, debeSerFinal[i],Modifier.isFinal(atributo.getModifiers()) );
				assertEquals("Debe declarar el atributo "+attrNames[i]+" como "+(debeSerPublico[i]?"":"NO")+" PUBLICO en la clase "+className, debeSerPublico[i],Modifier.isPublic(atributo.getModifiers()) );
				
				assertEquals("Debe declarar el atributo "+attrNames[i]+" de tipo "+attrTypes[i]+" en la clase "+className, attrTypes[i].getTypeName(),atributo.getType().getTypeName());
				if (attrSpecialization[i]!=null){
					if (((ParameterizedType)atributo.getGenericType()).getActualTypeArguments()==null ||
							((ParameterizedType)atributo.getGenericType()).getActualTypeArguments().length==0){
						fail("Debe declarar el atributo "+attrNames[i]+" de tipo "+attrTypes[i]+" y su tipo de parámetro debe ser "+attrSpecialization[i]+" en la clase "+className);
					}
					assertEquals("Debe declarar el atributo "+attrNames[i]+" de tipo "+attrTypes[i]+" y su tipo de parámetro debe ser "+attrSpecialization[i]+" en la clase "+className, attrSpecialization[i].getTypeName(), ((ParameterizedType)atributo.getGenericType()).getActualTypeArguments()[0].getTypeName() );
				}
				
			} catch (NoSuchFieldException e) {
				fail(e.getMessage()+"\nDebe declarar el atributo "+attrNames[i]+" en la clase "+className);
			} 
		}
	}

}
