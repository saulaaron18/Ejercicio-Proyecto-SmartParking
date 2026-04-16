package test;

import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;

import controladores.ControladorReservas;
import controladores.excepciones.PlazaOcupada;
import controladores.excepciones.ReservaInvalida;
import controladores.excepciones.SolicitudReservaInvalida;
import modelo.gestoresplazas.GestorLocalidad;
import modelo.reservas.EstadoValidez;
import modelo.reservas.Reserva;
import modelo.reservas.solicitudesreservas.SolicitudReservaAnticipada;
import modelo.reservas.solicitudesreservas.TEnumPrioridad;
import modelo.vehiculos.Vehiculo;



public class TestControladorReservasOblig {
	
	@Rule //Se establece un time out general para todos los tests. Se debe comentar esta línea y la de abajo para depurar
    public TestRule  globalTimeout = new DisableOnDebug(Timeout.millis(100)); // 100 milisegundos máximos por test	
	

	private ControladorReservas controlador;
	private int[][] plazas = {{2, 1},
			{4, 3}};
	private double[][] precios = {{1.0, 1.0}, {1.0, 2.0}};


	@Before
	public void setUp() {
		controlador = new ControladorReservas(plazas, precios);
	}

	/**
	 * Comprueba el constructor de ControladorReservas
	 */
	@Test
	public void testConst() {
		assertNotNull("No se ha inicializado el registro de reservas", controlador.getRegistroReservas());

		GestorLocalidad gestor = controlador.getGestorLocalidad();

		assertNotNull("No se ha inicializado el gestor de localidad", gestor);
	}
	
	
	/**
	 * 
	 * Comprueba que el método hacerReserva() lanza la excepción SolicitudReservaInvalida si la coordenada I no es correcta
	 * 
	 * @throws SolicitudReservaInvalida
	 */
	@Test (expected=SolicitudReservaInvalida.class)	
	public void testHacerReservaInvalida1() throws SolicitudReservaInvalida {
		LocalDateTime tI = LocalDateTime.of(2021, 10, 5, 1, 0);
		LocalDateTime tF = LocalDateTime.of(2021, 10, 5, 2, 30);
		Vehiculo car = new Vehiculo("7883CTB");

		// solicitud inválida por coords incorrectas

		SolicitudReservaAnticipada solicitud1 = new SolicitudReservaAnticipada(2, 1, tI, tF, car, TEnumPrioridad.BAJA);

		controlador.hacerReserva(solicitud1);
	}
	
	/**
	 *
	 * 
	 * Comprueba que el método hacerReserva() lanza la excepción SolicitudReservaInvalida si la coordenada J no es correcta
	 * 
	 * @throws SolicitudReservaInvalida
	 */
	@Test (expected=SolicitudReservaInvalida.class)	
	public void testHacerReservaInvalida2() throws SolicitudReservaInvalida {
		LocalDateTime tI = LocalDateTime.of(2021, 10, 5, 1, 0);
		LocalDateTime tF = LocalDateTime.of(2021, 10, 5, 2, 30);
		Vehiculo car = new Vehiculo("7883CTB");

		// solicitud inválida por coords incorrectas
		
		SolicitudReservaAnticipada solicitud2 = new SolicitudReservaAnticipada(1, 2, tI, tF, car, TEnumPrioridad.BAJA);
		
		controlador.hacerReserva(solicitud2);
	}
	/**
	 * 
	 * Comprueba que el método hacerReserva() lanza la excepción SolicitudReservaInvalida si Ti>Tf
	 * 
	 * @throws SolicitudReservaInvalida
	 */
	@Test (expected=SolicitudReservaInvalida.class)	
	public void testHacerReservaInvalida3() throws SolicitudReservaInvalida {
		LocalDateTime tI = LocalDateTime.of(2021, 10, 5, 1, 0);
		LocalDateTime tF = LocalDateTime.of(2021, 10, 5, 2, 30);
		Vehiculo car = new Vehiculo("7883CTB");
		
		// solicitud inválida por tiempos incorrectos

		SolicitudReservaAnticipada solicitud3 = new SolicitudReservaAnticipada(1, 1, tF, tI, car, TEnumPrioridad.BAJA);

		controlador.hacerReserva(solicitud3);
	}
	
	/**
	 * 
	 * Comprueba que el método hacerReserva() lanza la excepción SolicitudReservaInvalida si el vehiculo está sancionado
	 * 
	 * @throws SolicitudReservaInvalida
	 */
	@Test (expected=SolicitudReservaInvalida.class)	
	public void testHacerReservaInvalida4() throws SolicitudReservaInvalida {
		LocalDateTime tI = LocalDateTime.of(2021, 10, 5, 1, 0);
		LocalDateTime tF = LocalDateTime.of(2021, 10, 5, 2, 30);
		Vehiculo car = new Vehiculo("7883CTB");
		car.setSancionado(true);
		
		// solicitud inválida por vehiculo sancionado

		SolicitudReservaAnticipada solicitud3 = new SolicitudReservaAnticipada(0, 1, tI, tF, car, TEnumPrioridad.BAJA);

		controlador.hacerReserva(solicitud3);
	}
	
	/**
	 * Comprueba si el método hacerReserva() realiza una reserva anticipada
	 * 
	 */
	@Test
	public void testHacerReservaValida() throws SolicitudReservaInvalida {
		int i = 0;
		int j = 1;
		LocalDateTime tI = LocalDateTime.of(2021, 10, 5, 1, 0);
		LocalDateTime tF = LocalDateTime.of(2021, 10, 5, 2, 30);
		Vehiculo car = new Vehiculo("7883CTB");
		
		SolicitudReservaAnticipada solicitud = new SolicitudReservaAnticipada(i, j, tI, tF, car, TEnumPrioridad.BAJA);
		
		int numReserva = controlador.hacerReserva(solicitud);
		
		int numPlaza = controlador.getReserva(numReserva).getHueco().getPlaza().getNumero();
		
		assertTrue("No se ha realizado la reserva o la reserva creada no se corresponde con la solicitud", controlador.esValidaReserva(i, j, numPlaza, numReserva, car.getMatricula()));
	}
	
	/**
	 * Comprueba si el método hacerReserva() incluye una solicitud de reserva anticipada en la lista de espera
	 * 
	 */
	@Test
	public void testHacerReservaPospuesta() throws SolicitudReservaInvalida {
		// esta zona solo tiene una plaza
		int i = 0;
		int j = 1;
		LocalDateTime tI = LocalDateTime.of(2021, 10, 5, 1, 0);
		LocalDateTime tF = LocalDateTime.of(2021, 10, 5, 2, 30);
		Vehiculo car = new Vehiculo("7883CTB");
		
		SolicitudReservaAnticipada solicitud = new SolicitudReservaAnticipada(i, j, tI, tF, car, TEnumPrioridad.BAJA);
		
		// se hace una reserva
		controlador.hacerReserva(solicitud);
		
		LocalDateTime tI1 = LocalDateTime.of(2021, 10, 5, 1, 0);
		LocalDateTime tF1 = LocalDateTime.of(2021, 10, 5, 1, 30);
		Vehiculo car1 = new Vehiculo("5483ATB");
		SolicitudReservaAnticipada solicitud1 = new SolicitudReservaAnticipada(i, j, tI1, tF1, car1, TEnumPrioridad.BAJA);
		
		// se mete en lista de espera
		int numReserva1 = controlador.hacerReserva(solicitud1);
				
		assertEquals("Se debería haber devuelto un -1 (reserva en lista de espera)", -1, numReserva1);	
	}
	
	/**
	 * Comprueba el método getReserva()
	 */
	@Test
	public void testGetReserva() {
		int i = 0;
		int j = 1;
		LocalDateTime tI = LocalDateTime.of(2021, 10, 5, 1, 0);
		LocalDateTime tF = LocalDateTime.of(2021, 10, 5, 2, 30);
		Vehiculo car = new Vehiculo("7883CTB");
		
		SolicitudReservaAnticipada solicitud = new SolicitudReservaAnticipada(i, j, tI, tF, car, TEnumPrioridad.BAJA);
		
		int numReserva = controlador.getRegistroReservas().registrarReserva(solicitud);
		Reserva reserva = controlador.getRegistroReservas().obtenerReserva(numReserva);
		
		assertEquals("No se devuelve la reserva correcta con número " + numReserva, reserva, controlador.getReserva(numReserva));
	}
	
	/**
	 * Comprueba si el método ocuparPlaza() lanza la excepción ReservaInvalida si se intentar ocupar una plaza no reservada
	 * 
	 * @throws SolicitudReservaInvalida
	 * @throws PlazaOcupada
	 * @throws ReservaInvalida
	 */
	@Test (expected=ReservaInvalida.class)	
	public void testOcuparPlazaReservaInvalida() throws SolicitudReservaInvalida, PlazaOcupada, ReservaInvalida {
		int i = 0;
		int j = 1;
		LocalDateTime tI = LocalDateTime.of(2021, 10, 5, 1, 0);
		LocalDateTime tF = LocalDateTime.of(2021, 10, 5, 2, 30);
		Vehiculo car = new Vehiculo("7883CTB");
		
		SolicitudReservaAnticipada solicitud = new SolicitudReservaAnticipada(i, j, tI, tF, car, TEnumPrioridad.BAJA);
		
		// se hace una reserva
		int numReserva = controlador.hacerReserva(solicitud);
		
		// se intenta ocupar una plaza (plazas.length) que no está en la reserva
		controlador.ocuparPlaza(i, j, plazas.length, numReserva, car);		
	}
	
	/**
	 * Comprueba si el método ocuparPlaza() lanza la excepción PlazaOcupada
	 * 
	 * @throws SolicitudReservaInvalida
	 * @throws PlazaOcupada
	 * @throws ReservaInvalida
	 */
	@Test (expected=PlazaOcupada.class)	
	public void testOcuparPlazaOcupada() throws SolicitudReservaInvalida, PlazaOcupada, ReservaInvalida {
		int i = 0;
		int j = 1;
		LocalDateTime tI = LocalDateTime.of(2021, 10, 5, 1, 0);
		LocalDateTime tF = LocalDateTime.of(2021, 10, 5, 2, 30);
		Vehiculo car = new Vehiculo("7883CTB");
		
		SolicitudReservaAnticipada solicitud = new SolicitudReservaAnticipada(i, j, tI, tF, car, TEnumPrioridad.BAJA);
		
		// se hace una reserva
		int numReserva = controlador.hacerReserva(solicitud);
		
		// se ocupa la plaza reservada
		controlador.ocuparPlaza(i, j, solicitud.getHueco().getPlaza().getNumero(), numReserva, car);		

		// se intenta ocupar una plaza ocupada
		controlador.ocuparPlaza(i, j, solicitud.getHueco().getPlaza().getNumero(), numReserva, car);		
	}
	
	/**
	 * Comprueba si el método ocuparPlaza() ocupa una plaza libre reservada
	 * 
	 * @throws SolicitudReservaInvalida
	 * @throws PlazaOcupada
	 * @throws ReservaInvalida
	 */
	@Test
	public void testOcuparPlazaOK() throws SolicitudReservaInvalida, PlazaOcupada, ReservaInvalida {
		int i = 0;
		int j = 1;
		LocalDateTime tI = LocalDateTime.of(2021, 10, 5, 1, 0);
		LocalDateTime tF = LocalDateTime.of(2021, 10, 5, 2, 30);
		Vehiculo car = new Vehiculo("7883CTB");
		
		SolicitudReservaAnticipada solicitud = new SolicitudReservaAnticipada(i, j, tI, tF, car, TEnumPrioridad.BAJA);
		
		// se hace una reserva
		int numReserva = controlador.hacerReserva(solicitud);
		
		// se ocupa la plaza reservada
		controlador.ocuparPlaza(i, j, solicitud.getHueco().getPlaza().getNumero(), numReserva, car);		

		Reserva reserva = controlador.getReserva(numReserva);
		
		assertEquals("No se ha comprobado la validez de la reserva", EstadoValidez.OK, reserva.getEstadoValidez());
		
		assertEquals("La plaza no se ha ocupado con el coche " + car, car, reserva.getHueco().getPlaza().getVehiculo());
		
	}
	

}
