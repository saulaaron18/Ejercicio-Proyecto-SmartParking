package modelo.reservas.solicitudesreservas;

import java.time.LocalDateTime;

import modelo.gestoresplazas.GestorLocalidad;
import modelo.vehiculos.Vehiculo;

public class SolicitudReservaInmediata extends SolicitudReserva {
	private int radio;

	public SolicitudReservaInmediata(int i, int j, LocalDateTime tI, LocalDateTime tF, Vehiculo vehiculo, int radio) {
		super(i, j, tI, tF, vehiculo);
		this.radio = radio;
	}

	@Override
	public boolean esValida(GestorLocalidad gestorLocalidad) {
		boolean esValido = super.esValida(gestorLocalidad) && radio>0;
		boolean encontrado = !esValido;
		
		//di = i - getIZona
		//dj = j - getJZona
		//
		//r = |di|+|dj| --> |di| = r - |dj|
		//de -r a r, es decir O(2r+1) <-> O(2r)
		for (int di = -radio; di <= radio && !encontrado; di++) {
	        int djAbs = radio - Math.abs(di);

	        if (gestorLocalidad.existeZona(getIZona() + di, getJZona() + djAbs)) {
	            encontrado = true;
	        }
	        if (!encontrado && djAbs != 0 && 
	        		gestorLocalidad.existeZona(getIZona() + di, getJZona() - djAbs)) {
	            encontrado = true;
	        }
	    }

		return esValido && encontrado;
	}
	
	@Override
	public void gestionarSolicitudReserva(GestorLocalidad gestor) {
		super.gestionarSolicitudReserva(gestor);
	}
}
