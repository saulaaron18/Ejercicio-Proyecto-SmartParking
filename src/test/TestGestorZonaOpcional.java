package test;

import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;

import list.IList;
import modelo.gestoresplazas.GestorZona;
import modelo.gestoresplazas.huecos.Hueco;
import modelo.reservas.solicitudesreservas.SolicitudReservaAnticipada;
import modelo.reservas.solicitudesreservas.TEnumPrioridad;
import modelo.vehiculos.Vehiculo;

public class TestGestorZonaOpcional {
	
	@Rule //Se establece un time out general para todos los tests. Se debe comentar esta línea y la de abajo para depurar
    public TestRule  globalTimeout = new DisableOnDebug(Timeout.millis(100)); // 100 milisegundos máximos por test	
	

	private GestorZona gestor;

	/**
	 * Comprueba el método liberarHueco() 
	 */
	@Test
	public void testLiberarHueco() {	
		gestor = new GestorZona(0, 1, 3, 1.0);

		LocalDateTime tI = LocalDateTime.of(2021, 10, 5, 1, 0);
		LocalDateTime tF = LocalDateTime.of(2021, 10, 5, 2, 30);

		Hueco hueco = gestor.reservarHueco(tI, tF);

		LocalDateTime tI1 = LocalDateTime.of(2021, 10, 5, 0, 0);
		LocalDateTime tF1 = LocalDateTime.of(2021, 10, 5, 0, 10);

		gestor.reservarHueco(tI1, tF1);

		gestor.liberarHueco(hueco);

		assertFalse("El hueco liberado no debería estar en la lista de huecos reservados", 
				gestor.getEstadoHuecosReservados().contains("01:00, 02:30"));

		assertTrue("No se ha incluido el hueco reservado en la lista de huecos libres", 
				gestor.getEstadoHuecosLibres().contains("00:10, 03:00, " + hueco.getPlaza()));
	}


	
	private void aplazarSolicitud(int hi, int mi, int hf, int mf, String matricula) {
		LocalDateTime tI = LocalDateTime.of(2021, 10, 5, hi, mi);
		LocalDateTime tF = LocalDateTime.of(2021, 10, 5, hf, mf);
		Vehiculo car = new Vehiculo(matricula);

		SolicitudReservaAnticipada solicitud = new SolicitudReservaAnticipada(0, 1, tI, tF, car, TEnumPrioridad.BAJA);
		gestor.meterEnListaEspera(solicitud);
	}
	
	/**
	 * Comprueba el método getSolicitudesAtendidasListaEspera()
	 */
	@Test
	public void testGetSolicitudesAtendidasListaEspera() {
		gestor = new GestorZona(0, 1, 1, 1.0); // una plaza

		LocalDateTime tI = LocalDateTime.of(2021, 10, 5, 0, 0);
		LocalDateTime tF = LocalDateTime.of(2021, 10, 5, 2, 30);
		Hueco hueco = gestor.reservarHueco(tI, tF);

		LocalDateTime tI1 = LocalDateTime.of(2021, 10, 5, 2, 30);
		LocalDateTime tF1 = LocalDateTime.of(2021, 10, 5, 3, 0);
		gestor.reservarHueco(tI1, tF1);

		// se crea la lista de espera
		aplazarSolicitud(0, 30, 2, 0, "car1");
		aplazarSolicitud(0, 45, 1, 0, "car2");
		aplazarSolicitud(2, 30, 2, 45, "car3");
		aplazarSolicitud(0, 0, 0, 15, "car4");
		
		gestor.liberarHueco(hueco);
		
		IList<SolicitudReservaAnticipada> lista = gestor.getSolicitudesAtendidasListaEspera();

		// se deben atender las de car1 y car4 únicamente		
		assertEquals("No se han atendido todas las solicitudes pendientes correctas", "[(Sol:0 1 00:30 02:00 car1),\n"
				+ "(Sol:0 1 00:00 00:15 car4)]", lista.toString());		
		assertEquals("No se ha actualizado la lista de espera correctamente", "[(Sol:0 1 00:45 01:00 car2),\n"
				+ "(Sol:0 1 02:30 02:45 car3)]", gestor.getListaEspera());
	}


}
