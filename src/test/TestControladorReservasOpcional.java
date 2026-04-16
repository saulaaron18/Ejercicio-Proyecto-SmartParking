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
import list.IList;
import modelo.gestoresplazas.GestorZona;
import modelo.gestoresplazas.huecos.Hueco;
import modelo.reservas.Reserva;
import modelo.reservas.solicitudesreservas.SolicitudReservaAnticipada;
import modelo.reservas.solicitudesreservas.TEnumPrioridad;
import modelo.vehiculos.Vehiculo;



public class TestControladorReservasOpcional {

	@Rule //Se establece un time out general para todos los tests. Se debe comentar esta línea y la de abajo para depurar
	public TestRule  globalTimeout = new DisableOnDebug(Timeout.millis(200)); // 200 milisegundos máximos por test	


	private ControladorReservas controlador;
	private int[][] plazas = {{1, 2},
			{3, 4}};
	private double[][] precios = {{1.0, 1.0}, {1.0, 2.0}};


	@Before
	public void setUp() {
		controlador = new ControladorReservas(plazas, precios);
	}

	/**
	 * Comprueba el método desocuparPlaza()
	 * 
	 * @throws SolicitudReservaInvalida
	 * @throws PlazaOcupada
	 * @throws ReservaInvalida
	 */
	@Test
	public void testDesocuparPlaza() throws SolicitudReservaInvalida, PlazaOcupada, ReservaInvalida {
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

		// se desocupa la plaza reservada
		controlador.desocuparPlaza(numReserva);

		assertNull("La plaza debería estar vacía (null) en la " + reserva, reserva.getHueco().getPlaza().getVehiculo());

		assertFalse("Se debería haber liberado el hueco reservado de la " + reserva, 
				reserva.getGestorZona().existeHuecoReservado(reserva.getHueco()));
	}

	/**
	 * Comprueba el método anularReserva() 
	 * 
	 * @throws SolicitudReservaInvalida
	 */
	@Test
	public void testAnularReserva() throws SolicitudReservaInvalida {
		int i = 0;
		int j = 1;
		LocalDateTime tI = LocalDateTime.of(2021, 10, 5, 1, 0);
		LocalDateTime tF = LocalDateTime.of(2021, 10, 5, 2, 30);
		Vehiculo car = new Vehiculo("7883CTB");

		SolicitudReservaAnticipada solicitud = new SolicitudReservaAnticipada(i, j, tI, tF, car, TEnumPrioridad.BAJA);

		// se hace una reserva
		int numReserva = controlador.hacerReserva(solicitud);

		Reserva reserva = controlador.getReserva(numReserva);

		// se anula la reserva
		controlador.anularReserva(numReserva);

		assertFalse("Se debería haber liberado el hueco reservado de la " + reserva, 
				reserva.getGestorZona().existeHuecoReservado(reserva.getHueco()));

		assertNull("No debería existir la " + reserva, controlador.getReserva(numReserva));

	}

	private void aplazarSolicitud(GestorZona gestor, int i, int j, int hi, int mi, int hf, int mf, String matricula) {
		LocalDateTime tI = LocalDateTime.of(2021, 10, 5, hi, mi);
		LocalDateTime tF = LocalDateTime.of(2021, 10, 5, hf, mf);
		Vehiculo car = new Vehiculo(matricula);

		SolicitudReservaAnticipada solicitud = new SolicitudReservaAnticipada(i, j, tI, tF, car, TEnumPrioridad.BAJA);
		gestor.meterEnListaEspera(solicitud);
		solicitud.setGestorZona(gestor);
	}

	
	/**
	 * Comprueba el método GetReservasAtendidasListaEspera() en todas las zonas
	 */
	@Test
	public void testGetReservasAtendidasListaEspera() {
		int t;
		for (int i=0; i<plazas.length; i++)
			for (int j=0; j<plazas[0].length; j++) {
				Hueco hueco = null;
				GestorZona gestor = null;
				
				// se reserva todo el tiempo en todas las plazas de la zona i, j
				for (int k=0; k<plazas[i][j]; k++) {
					gestor = controlador.getGestorLocalidad().getGestorZona(i, j);
					LocalDateTime tI = LocalDateTime.of(2021, 10, 5, 0, 0);
					LocalDateTime tF = LocalDateTime.of(2021, 10, 5, 2, 30);
					hueco = gestor.reservarHueco(tI, tF);

					LocalDateTime tI1 = LocalDateTime.of(2021, 10, 5, 2, 30);
					LocalDateTime tF1 = LocalDateTime.of(2021, 10, 5, 3, 0);
					gestor.reservarHueco(tI1, tF1);
				}
				// se crea la lista de espera para la zona i, j
				aplazarSolicitud(gestor, i, j, 0, 30, 2, 0, "car1");
				aplazarSolicitud(gestor, i, j, 0, 45, 1, 0, "car2");
				aplazarSolicitud(gestor, i, j, 2, 30, 2, 45, "car3");
				aplazarSolicitud(gestor, i, j, 0, 0, 0, 15, "car4");

				// se libera un hueco en una plaza de la zona i, j
				gestor.liberarHueco(hueco);

				// se obtienen los números de las reservas registradas a partir
				// de las solicitudes atendidas que había en la lista de espera
				IList<Integer> reservasNums = controlador.getReservasRegistradasDesdeListaEspera(i, j);

				// se comprueba que las dos reservas registradas son correctas

				int numReserva = reservasNums.get(0);
				Reserva reserva = controlador.getReserva(numReserva);
				t = plazas[i][j] - 1;

				assertEquals("Reserva incorrectamente registrada",
						"Reserva: " + numReserva + " car1 z" + i + ":" + j +
								" (00:30, 02:00, (" + t + "-null))", reserva.toString()); 

				numReserva = reservasNums.get(1);
				reserva = controlador.getReserva(numReserva);

				assertEquals("Reserva incorrectamente registrada",
						"Reserva: " + numReserva + " car4 z" + i + ":" + j +
								" (00:00, 00:15, (" + t + "-null))", reserva.toString());
				
				// se comprueba que la lista de espera contiene las dos reservas no registradas en el orden correcto
				
				String listaEspera = gestor.getListaEspera();
				assertEquals("Lista de espera actualizada incorrecta o con tal que las reservas no respetan el orden original",
						"[(Sol:" + i + " " + j + " 00:45 01:00 car2),\n"
						+ "(Sol:" + i + " " + j + " 02:30 02:45 car3)]", 
						listaEspera);
			}
	}
}
