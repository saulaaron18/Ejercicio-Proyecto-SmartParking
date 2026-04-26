package modelo.reservas.solicitudesreservas;

import java.time.LocalDateTime;

import modelo.gestoresplazas.GestorLocalidad;
import modelo.vehiculos.Vehiculo;

public class SolicitudReservaAnticipada extends SolicitudReserva{
	private TEnumPrioridad prioridad; 

	public SolicitudReservaAnticipada(int i, int j, LocalDateTime tI, LocalDateTime tF, Vehiculo vehiculo, TEnumPrioridad prioridad) {
		super(i, j, tI, tF, vehiculo);
		this.prioridad = prioridad;
	}

	public TEnumPrioridad getPrioridad() {
		return prioridad;
	}

	@Override
	public void gestionarSolicitudReserva(GestorLocalidad gestor) {
		super.gestionarSolicitudReserva(gestor);

		if(getHueco() == null) {
			gestor.getGestorZona(getIZona(), getJZona()).meterEnListaEspera(this);
		}
	}
}
