package test;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDateTime;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;

import modelo.gestoresplazas.GestorLocalidad;
import modelo.gestoresplazas.huecos.Hueco;
import modelo.reservas.solicitudesreservas.SolicitudReservaInmediata;
import modelo.vehiculos.Vehiculo;

public class TestSolicitudReservaInmediataOpcional {

	@Rule //Se establece un time out general para todos los tests. Se debe comentar esta línea y la de abajo para depurar
	public TestRule  globalTimeout = new DisableOnDebug(Timeout.millis(100)); // 100 milisegundos máximos por test	


	private int[][] plazas = 
			{ 	{1, 1, 1, 1, 3},
				{1, 1, 2, 1, 3},
				{2, 1, 1, 1, 3},
				{3, 3, 3, 3, 3},
				{3, 3, 3, 3, 3} };
	private double[][] precios = 
		{ 	{4.0, 1.5, 1.0, 1.0, 2.0},
			{3.0, 1.0, 0.5, 1.0, 1.0},
			{1.0, 1.0, 1.5, 1.5, 2.0},
			{1.0, 1.0, 2.0, 1.0, 2.0},
			{2.0, 2.0, 2.0, 1.0, 2.0} };
	
	private double[][] preciosIguales = 
		{ 	{1.0, 1.0, 1.0, 1.0, 1.0},
			{1.0, 1.0, 1.0, 1.0, 1.0},
			{1.0, 1.0, 1.0, 1.0, 1.0},
			{1.0, 1.0, 1.0, 1.0, 1.0},
			{1.0, 1.0, 1.0, 1.0, 1.0} };


	/**
	 * Comprueba la definición del atributo radio
	 */
	@Test	
	public void testAtributosSolicitudReservaInmediata() {
		Class<?> className = SolicitudReservaInmediata.class;
		String[] attrNames = {"radio"};
		Type[] attrTypes = {int.class};
		Type[] attrSpecialization = {null};
		boolean[] debeSerPrivado = {true};
		boolean[] debeSerEstatico = {false}; 
		boolean[] debeSerFinal = {false}; 
		boolean[] debeSerPublico = {false};

		checkAttributes(className, attrNames, attrTypes, attrSpecialization,
				debeSerPrivado, debeSerEstatico, debeSerFinal, debeSerPublico);
	}

	/**
	 * Comprueba el método esValida() para solicitudes válidas e inválidas (radio o coords incorrectas)
	 */
	@Test
	public void testEsValida() {
		
		GestorLocalidad gestorLocalidad = new GestorLocalidad(plazas, precios);
		
		LocalDateTime tI = LocalDateTime.of(2021, 10, 5, 1, 0);
		LocalDateTime tF = LocalDateTime.of(2021, 10, 5, 2, 30);
		Vehiculo car = new Vehiculo("7883CTB");
		
		// Test 1: Radios correctos e incorrectos
		
		// Puntos iniciales probados: (0,1), (1,0), (4,3), (3,4), (0,4), (4,0), (3,2), (1,3)
		// Además se añaden casos para esquinas, bordes y el centro para cubrir más combinaciones:
		// (0,0), (4,4), (2,2), (2,0), (0,2), (4,2), (2,4), (1,4), (4,1), (2,1).
		
		int[] posI = {0, 0, 1, 1, 4, 4, 3, 3, 0, 0, 4, 4, 3, 3, 1, 1,
					0, 0, 4, 4, 2, 2, 2, 2, 0, 0,
					4, 4, 2, 2, 1, 1, 4, 4, 2, 2};
		int[] posJ = {1, 1, 0, 0, 3, 3, 4, 4, 4, 4, 0, 0, 2, 2, 3, 3,
					0, 0, 4, 4, 2, 2, 0, 0, 2, 2,
					2, 2, 4, 4, 4, 4, 1, 1, 1, 1};
		int[] radios = {7, 8, 7, 8, 7, 8, 7, 8, 8, 9, 8, 9, 5, 6, 6, 7,
					8, 9, 8, 9, 4, 5, 6, 7, 6, 7,
					6, 7, 6, 7, 7, 8, 7, 8, 5, 6};
		boolean[] validas = {true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false,
					true, false, true, false, true, false, true, false, true, false,
					true, false, true, false, true, false, true, false, true, false};

		SolicitudReservaInmediata solicitud;
		
		for(int i = 0; i < posI.length; i++) {
			solicitud = new SolicitudReservaInmediata(posI[i], posJ[i], tI, tF, car, radios[i]);
			if(validas[i]) {
				assertTrue("No se ha clasificado correctamente una solicitud válida", solicitud.esValida(gestorLocalidad));
			} else {
				assertFalse("No se ha clasificado correctamente una solicitud con un radio demasiado grande", solicitud.esValida(gestorLocalidad));
			}
		}

		// Prueba otros radios incorrectos
		
		solicitud = new SolicitudReservaInmediata(0, 1, tI, tF, car, -1);

		assertFalse("No se ha clasificado correctamente una solicitud con radio incorrecto", solicitud.esValida(gestorLocalidad));

		solicitud = new SolicitudReservaInmediata(0, 1, tI, tF, car, 0);

		assertFalse("No se ha clasificado correctamente una solicitud con radio incorrecto", solicitud.esValida(gestorLocalidad));

		
		// Test 2: Solicitudes inválidas por coords incorrectas

		SolicitudReservaInmediata solicitud1 = new SolicitudReservaInmediata(0, -1, tI, tF, car, 2);

		assertFalse("No se ha clasificado correctamente una solicitud con la J incorrecta", solicitud1.esValida(gestorLocalidad));

		solicitud1 = new SolicitudReservaInmediata(5, 0, tI, tF, car, 2);

		assertFalse("No se ha clasificado correctamente una solicitud con la I incorrecta", solicitud1.esValida(gestorLocalidad));

		solicitud1 = new SolicitudReservaInmediata(-1, 3, tI, tF, car, 2);

		assertFalse("No se ha clasificado correctamente una solicitud con la I incorrecta", solicitud1.esValida(gestorLocalidad));

	}

	/**
	 * Comprueba el método gestionarSolicitudReserva() realizando solicitudes inmediatas en distintas zonas, todas ellas con el mismo precio
	 */
	@Test
	public void testGestionarReservaCompletadaSinPrecios() {
		comprobarGestionarReservaCompletadaSinPrecios();
	}
	
	private void comprobarGestionarReservaCompletadaSinPrecios() {
		GestorLocalidad gestorLocalidad = new GestorLocalidad(plazas, preciosIguales);

		LocalDateTime tI = LocalDateTime.of(2021, 10, 5, 1, 0);
		LocalDateTime tF = LocalDateTime.of(2021, 10, 5, 2, 30);
		Vehiculo car = new Vehiculo("7883CTB");
		// Nota: la (1, 2) tiene dos plazas, y la (0,4) y (3,1) tienen tres.
		int[] pathI = {0, 0, 0, 1, 0, 1, 1, 2, 1, 0, 0, 0, 1, 2, 3, 3, 3}; //falta (2,0) para completar radio 3
		int[] pathJ = {1, 0, 2, 1, 3, 2, 2, 1, 0, 4, 4, 4, 3, 2, 1, 1, 1};

		// se realizan reservas consecutivas en las zonas (pathI[i], pathJ[i])
		// en el orden dado
		for(int i=0; i<pathI.length; i++) {
			SolicitudReservaInmediata solicitud = 
					new SolicitudReservaInmediata(0, 1, tI, tF, car, 3);
			solicitud.gestionarSolicitudReserva(gestorLocalidad);
			
			comprobarSolicitud(pathI[i], pathJ[i], solicitud, gestorLocalidad);
			//System.out.println(solicitud.getGestorZona());
		}
		
		// Segundo test: prueba con radio menor y completando recorrido
		GestorLocalidad gestorLocalidad2 = new GestorLocalidad(plazas, preciosIguales);
		
		int[] pathI2 = {0, 0, 0, 1, 0, 1, 1, 2}; //faltan (1,3) y (0,4)x3 para completar radio 3
		int[] pathJ2 = {1, 0, 2, 1, 3, 2, 2, 1};

		for(int i=0; i<pathI2.length; i++) {
			SolicitudReservaInmediata solicitud = 
					new SolicitudReservaInmediata(0, 1, tI, tF, car, 2);
			solicitud.gestionarSolicitudReserva(gestorLocalidad2);
			
			comprobarSolicitud(pathI2[i], pathJ2[i], solicitud, gestorLocalidad2);
			//System.out.println(solicitud.getGestorZona());
		}
		

	}
	
	/**
	 * Comprueba el método gestionarSolicitudReserva() realizando solicitudes inmediatas en distintas zonas
	 */
	@Test
	public void testGestionarReservaCompletada() {
		comprobarGestionarReservaCompletada();
	}
	
	private void comprobarGestionarReservaCompletada() {
		GestorLocalidad gestorLocalidad = new GestorLocalidad(plazas, precios);

		LocalDateTime tI = LocalDateTime.of(2021, 10, 5, 1, 0);
		LocalDateTime tF = LocalDateTime.of(2021, 10, 5, 2, 30);
		Vehiculo car = new Vehiculo("7883CTB");
		// Ahora priorizamos las zonas de menor precio
		int[] pathI = {0, 1, 1, 0, 1, 0, 2, 1};
		int[] pathJ = {1, 2, 2, 2, 1, 3, 1, 0};
		
		for(int i=0; i<pathI.length; i++) {
			SolicitudReservaInmediata solicitud = 
					new SolicitudReservaInmediata(0, 1, tI, tF, car, 2);
			solicitud.gestionarSolicitudReserva(gestorLocalidad);
			
			comprobarSolicitud(pathI[i], pathJ[i], solicitud, gestorLocalidad);
			//System.out.println(solicitud.getGestorZona());
		}
	}
	
	private void comprobarSolicitud(int i, int j, SolicitudReservaInmediata solicitud, GestorLocalidad gestorLocalidad) {		
		
		assertEquals("No se ha guardado el gestor de zona correcto en la reserva", 
				gestorLocalidad.getGestorZona(i, j), solicitud.getGestorZona());

		Hueco hueco = solicitud.getHueco();

		assertTrue("No se ha guardado el hueco en la reserva", hueco != null);

		assertTrue("No se ha guardado el hueco reservado en el gestor de zona", 
				gestorLocalidad.existeHuecoReservado(hueco, i, j));	
	}
	
	/**
	 * Comprueba el método gestionarSolicitudReserva() si no hay huecos con distintos radios
	 */
	@Test
	public void testGestionarReservaNoRealizada() {
		// nos aseguramos de que pasa el test de gestionar reservas, cuando hay huecos
		// para evitar que se pase este test si no está implementado el método gestionarSolicitudReserva()
		comprobarGestionarReservaCompletada(); 
		
		int[][] plazas1 = 
			{ 	{1, 0, 0, 0, 0},
				{1, 0, 0, 0, 0},
				{2, 0, 0, 0, 0},
				{3, 0, 0, 0, 0},
				{3, 0, 0, 0, 0} };
		
		GestorLocalidad gestorLocalidad = new GestorLocalidad(plazas1, preciosIguales);
		
		LocalDateTime tI = LocalDateTime.of(2021, 10, 5, 1, 0);
		LocalDateTime tF = LocalDateTime.of(2021, 10, 5, 2, 30);
		Vehiculo car = new Vehiculo("7883CTB");

		// No hay hueco en las zonas con radio 1 desde (2, 3)
		SolicitudReservaInmediata solicitud = new SolicitudReservaInmediata(2, 3, tI, tF, car, 1);
		solicitud.gestionarSolicitudReserva(gestorLocalidad);
		
		Hueco hueco = solicitud.getHueco();

		assertNull("Se ha guardado el hueco en la reserva y no debería haber", hueco);
		
		// No hay hueco en las zonas con radio 2 desde (2, 3)
		solicitud = new SolicitudReservaInmediata(2, 3, tI, tF, car, 2);
		solicitud.gestionarSolicitudReserva(gestorLocalidad);
		
		hueco = solicitud.getHueco();

		assertNull("Se ha guardado el hueco en la reserva y no debería haber", hueco);
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