package modelo.gestoresplazas;

import list.IList;
import modelo.gestoresplazas.huecos.Hueco;
import modelo.reservas.solicitudesreservas.SolicitudReservaAnticipada;

//TO-DO alumno obligatorio

public class GestorLocalidad {
	//TO-DO falta atributo

	public GestorLocalidad(int[][] plazas, double[][] precios) {
		//TO-DO
	}
	
	public int getRadioMaxI() {
		//TO-DO
		return - 1;
	}
	
	public int getRadioMaxJ() {
		//TO-DO
		return - 1;
	}
	
	public boolean existeZona(int i, int j) {
		//TO-DO
		return false;
	}

	public boolean existeHuecoReservado(Hueco hueco, int i, int j) {
		//TO-DO
		return false;
	}

	public GestorZona getGestorZona(int i, int j) {
		//TO-DO
		return null;
	}
	
	//TO-DO alumno opcional
	
	public IList<SolicitudReservaAnticipada> getSolicitudesAtendidasListaEspera(int i, int j) {
		//TO-DO
		return null;
	}
	
}
