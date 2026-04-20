package modelo.gestoresplazas;

import list.IList;
import modelo.gestoresplazas.huecos.Hueco;
import modelo.reservas.solicitudesreservas.SolicitudReservaAnticipada;

//TO-DO alumno obligatorio

public class GestorLocalidad {
	private GestorZona[][] gestoresZonas;

	public GestorLocalidad(int[][] plazas, double[][] precios) {
		this.gestoresZonas = new GestorZona[plazas.length][plazas[0].length];
		
		for(int i=0;i<plazas.length;i++) {
			for(int j=0;j<plazas[i].length;j++) {
				gestoresZonas[i][j] = new GestorZona(i, j, plazas[i][j], precios[i][j]);
			}
		}
	}
	
	public int getRadioMaxI() {
		return gestoresZonas.length - 1;
	}
	
	public int getRadioMaxJ() {
		return gestoresZonas[0].length - 1;
	}
	
	public boolean existeZona(int i, int j) {
		return i>=0 && j>=0 &&
				i<=getRadioMaxI() &&
				j<=getRadioMaxJ();
	}

	public boolean existeHuecoReservado(Hueco hueco, int i, int j) {
		return gestoresZonas[i][j].existeHuecoReservado(hueco);
	}

	public GestorZona getGestorZona(int i, int j) {
		if(existeZona(i, j)) {
			return gestoresZonas[i][j];
		}
		else {
			return null;
		}
	}
	
	//TO-DO alumno opcional
	
	public IList<SolicitudReservaAnticipada> getSolicitudesAtendidasListaEspera(int i, int j) {
		return getGestorZona(i, j).getSolicitudesAtendidasListaEspera();
	}
	
}
