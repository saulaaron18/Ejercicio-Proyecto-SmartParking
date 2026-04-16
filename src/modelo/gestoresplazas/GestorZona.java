package modelo.gestoresplazas;

import java.time.LocalDateTime;
import java.util.Arrays;

import list.IList;
import modelo.gestoresplazas.huecos.GestorHuecos;
import modelo.gestoresplazas.huecos.Hueco;
import modelo.gestoresplazas.huecos.Plaza;
import modelo.reservas.solicitudesreservas.SolicitudReservaAnticipada;

public class GestorZona {
	private int iZona;
	private int jZona;
	private Plaza[] plazas;
	private double precio;
	private IList<SolicitudReservaAnticipada> listaEspera;
	private GestorHuecos gestorHuecos;
	private IList<Hueco> huecosReservados;
	
	public int getI() {
		return iZona;
	}
	
	public int getJ() {
		return jZona;
	}
	
	public double getPrecio() {
		return precio;
	}
	
	public String getId() {
		return "z" + iZona + ":" + jZona;
	}
	
	public String getEstadoHuecosLibres() {
		return this.gestorHuecos.toString();
	}
	
	public String getEstadoHuecosReservados() {
		return this.huecosReservados.toString();
	}
	
	public String getListaEspera() {
		return this.listaEspera.toString();
	}
	
	public String getPlazas() {
		return Arrays.toString(this.plazas);
	}
	
	public String toString() {
		return getId() + ": " + getEstadoHuecosReservados();
	}
	
	//TO-DO alumno obligatorios
	
	public GestorZona(int i, int j, int noPlazas, double precio) {
		//TO-DO
	}
	
	public Hueco reservarHueco(LocalDateTime tI, LocalDateTime tF) {
		//TO-DO
		return null;
	}
	
	public boolean existeHueco(LocalDateTime tI, LocalDateTime tF) {
		return false;
	}
	
	
	public void meterEnListaEspera(SolicitudReservaAnticipada solicitud) {
		//TO-DO
	}
	
	public boolean existeHuecoReservado(Hueco hueco) {
		//TO-DO
		return false;
	}
	
	//TO-DO alumno opcionales
	
	public void liberarHueco(Hueco hueco) {
		//TO-DO
	}

	//PRE (no es necesario comprobar): las solicitudes de la lista de espera son válidas
	public IList<SolicitudReservaAnticipada> getSolicitudesAtendidasListaEspera() {
		//TO-DO
		return null;
	}

	


}
