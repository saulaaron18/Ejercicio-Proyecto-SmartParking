package controladores;

import controladores.excepciones.PlazaOcupada;
import controladores.excepciones.ReservaInvalida;
import controladores.excepciones.SolicitudReservaInvalida;
import list.ArrayList;
import list.IList;
import modelo.gestoresplazas.GestorLocalidad;
import modelo.gestoresplazas.huecos.Plaza;
import modelo.reservas.EstadoValidez;
import modelo.reservas.Reserva;
import modelo.reservas.Reservas;
import modelo.reservas.solicitudesreservas.SolicitudReserva;
import modelo.reservas.solicitudesreservas.SolicitudReservaAnticipada;
import modelo.vehiculos.Vehiculo;


public class ControladorReservas {
	private Reservas registroReservas;
	private GestorLocalidad gestorLocalidad;

	public GestorLocalidad getGestorLocalidad() {
		return gestorLocalidad;
	}

	public Reservas getRegistroReservas() {
		return registroReservas;
	}

	public boolean esValidaReserva(int i, int j, int numPlaza, int numReserva, String noMatricula) {
		Reserva reserva = this.registroReservas.obtenerReserva(numReserva);
		if (reserva == null)
			return false;
		reserva.validar(i, j, numPlaza, noMatricula, gestorLocalidad);
		return reserva.getEstadoValidez() == EstadoValidez.OK;
	}

	//TO-DO alumno obligatorio

	public ControladorReservas(int[][] plazas, double[][] precios) {
		registroReservas = new Reservas();
		gestorLocalidad = new GestorLocalidad(plazas, precios);
	}


	//PRE: la solicitud es válida
	public int hacerReserva(SolicitudReserva solicitud) throws SolicitudReservaInvalida {
		if(!solicitud.esValida(gestorLocalidad)){
			throw new SolicitudReservaInvalida(
					"Reserva inválida.");
		}
		solicitud.gestionarSolicitudReserva(gestorLocalidad);
		return (solicitud.getHueco() != null) ?
				registroReservas.registrarReserva(solicitud) : -1;
	}

	public Reserva getReserva(int numReserva) {
		return registroReservas.obtenerReserva(numReserva);
	}

	//PRE: la plaza dada está libre y la reserva está validada
	public void ocuparPlaza(int i, int j, int numPlaza, int numReserva, Vehiculo vehiculo) throws PlazaOcupada, ReservaInvalida {
		Plaza plaza = registroReservas.obtenerReserva(numReserva).getHueco().getPlaza();
		if(!esValidaReserva(i, j, numPlaza, numReserva, vehiculo.getMatricula())){
			throw new ReservaInvalida(
					"Reserva inválida.");
		} else if(plaza.getVehiculo() != null){
			throw new PlazaOcupada(
					"Plaza ocupada.");
		}
		plaza.setVehiculo(vehiculo);
	}


	//TO-DO alumno opcional

	public void desocuparPlaza(int numReserva) {
		Reserva reserva = getReserva(numReserva);	
		reserva.getHueco().getPlaza().setVehiculo(null);
		reserva.liberarHuecoReservado();
	}

	public void anularReserva(int numReserva) {
		Reserva reserva = registroReservas.obtenerReserva(numReserva);
		reserva.liberarHuecoReservado();
		registroReservas.borrarReserva(numReserva);
	}


	// PRE (no es necesario comprobar): todas las solicitudes atendidas son válidas.
	public IList<Integer> getReservasRegistradasDesdeListaEspera(int i, int j) {
		IList<Integer> solicitudesRegistradas = new ArrayList<Integer>();
		IList<SolicitudReservaAnticipada> solicitudesAtendidas = 
				gestorLocalidad.getSolicitudesAtendidasListaEspera(i, j);

		for(int k=0;k<solicitudesAtendidas.size();k++) {
			solicitudesRegistradas.add(solicitudesRegistradas.size(),
					registroReservas.registrarReserva(solicitudesAtendidas.get(k)));
		}

		return solicitudesRegistradas;
	}
}
