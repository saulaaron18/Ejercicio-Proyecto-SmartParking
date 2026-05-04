package modelo.reservas.solicitudesreservas;

import java.time.LocalDateTime;

import modelo.gestoresplazas.GestorLocalidad;
import modelo.gestoresplazas.GestorZona;
import modelo.vehiculos.Vehiculo;

public class SolicitudReservaInmediata extends SolicitudReserva {
	
	private int radio;
	
	public SolicitudReservaInmediata(int i, int j, LocalDateTime tI, LocalDateTime tF, Vehiculo v, int radio) {
		super(i, j, tI, tF, v);
		this.radio = radio;
	}
	
	public boolean esValida(GestorLocalidad gestorLocalidad) {
		//TODO
	}
	
	public void gestionarSolicitudReserva(GestorLocalidad gestor) {
		//TODO
	}

}
