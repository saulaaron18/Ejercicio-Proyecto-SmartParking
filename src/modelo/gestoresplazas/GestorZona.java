package modelo.gestoresplazas;

import java.time.LocalDateTime;
import java.util.Arrays;

import list.ArrayList;
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
		this.iZona = i;
		this.jZona = j;
		this.precio = precio;

		this.plazas = new Plaza[noPlazas];

		for (int k = 0; k < noPlazas; k++) {
			this.plazas[k] = new Plaza(k);
		}

		this.huecosReservados = new ArrayList<Hueco>();
		this.listaEspera = new ArrayList<SolicitudReservaAnticipada>();
		this.gestorHuecos = new GestorHuecos(this.plazas);
	}

	public Hueco reservarHueco(LocalDateTime tI, LocalDateTime tF) {
		Hueco huecoReservado = gestorHuecos.reservarHueco(tI, tF);

		if(huecoReservado != null) {
			huecosReservados.add(huecosReservados.size(), huecoReservado);
		}
		return huecoReservado;
	}

	public boolean existeHueco(LocalDateTime tI, LocalDateTime tF) {
		return gestorHuecos.existeHueco(tI, tF);
	}


	public void meterEnListaEspera(SolicitudReservaAnticipada solicitud) {
		int ordinalPrioridadSolicitud = solicitud.getPrioridad().ordinal();
		int i=0;
	    while (i < listaEspera.size() &&
	           listaEspera.get(i).getPrioridad().ordinal() >= ordinalPrioridadSolicitud) {
	    	i++;
	    }
	    
	    listaEspera.add(i, solicitud);
	}

	public boolean existeHuecoReservado(Hueco hueco) {
		boolean existe = false;
		for(int i=0;i<huecosReservados.size() && !existe;i++) {
			existe = huecosReservados.get(i).equals(hueco);
		}

		return existe;
	}

	//TO-DO alumno opcionales

	public void liberarHueco(Hueco hueco) {
		huecosReservados.remove(hueco);
		gestorHuecos.liberarHueco(hueco);
	}

	//PRE (no es necesario comprobar): las solicitudes de la lista de espera son válidas
	public IList<SolicitudReservaAnticipada> getSolicitudesAtendidasListaEspera() {
		ArrayList<SolicitudReservaAnticipada> solicitudesAtendidas = new ArrayList<SolicitudReservaAnticipada>();
		
		for(int i=0; i<listaEspera.size();i++) {
			SolicitudReservaAnticipada solicitud = listaEspera.get(i);
			Hueco hueco = reservarHueco(solicitud.getTInicial(), solicitud.getTFinal());
			
			if(hueco != null) {
				solicitud.setHueco(hueco);
				solicitudesAtendidas.add(solicitudesAtendidas.size(), solicitud);
				listaEspera.remove(solicitud);
			}
		}
		
		return solicitudesAtendidas;
	}




}
