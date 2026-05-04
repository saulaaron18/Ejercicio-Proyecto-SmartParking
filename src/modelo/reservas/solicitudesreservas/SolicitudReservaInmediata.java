package modelo.reservas.solicitudesreservas;

import java.time.LocalDateTime;
import modelo.gestoresplazas.GestorLocalidad;
import modelo.gestoresplazas.GestorZona;
import modelo.vehiculos.Vehiculo;
import list.ArrayList;

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
		if (getHueco() == null) {

			ArrayList<GestorZona> candidatos = new ArrayList<GestorZona>();
			recolectarCandidatosEnOrdenHorario(candidatos, gestor);

			while (getHueco() == null && candidatos.size() > 0) {
				int idxMejor = indiceZonaMasBarata(candidatos);
				GestorZona mejorGestorZona = candidatos.get(idxMejor);
				candidatos.removeElementAt(idxMejor);

				setGestorZona(mejorGestorZona);
				//Intentamos reservar el hueco (puede ser null)
				setHueco(mejorGestorZona.reservarHueco(getTInicial(), getTFinal()));
			}
		}
	}

	private int indiceZonaMasBarata(ArrayList<GestorZona> candidatos) {
		int idx = 0;
		for (int i = 1; i < candidatos.size(); i++) {
			if (candidatos.get(i).getPrecio() < candidatos.get(idx).getPrecio()) {
				idx = i;
			}
			//En caso de empate, el primero
		}
		return idx;
	}

	private void recolectarCandidatosEnOrdenHorario(ArrayList<GestorZona> candidatos,
			GestorLocalidad gestor) {
		int i = getIZona(), j = getJZona();

		for (int d = 1; d <= radio; d++) {

			for (int k = 0; k < d; k++)
				anadirSiExiste(candidatos, gestor, i - k, j - d + k); 


			for (int k = 0; k < d; k++)
				anadirSiExiste(candidatos, gestor, i - d + k, j + k);


			for (int k = 0; k < d; k++)
				anadirSiExiste(candidatos, gestor, i + k, j + d - k);


			for (int k = 0; k < d; k++)
				anadirSiExiste(candidatos, gestor, i + d - k, j - k);
		}
	}

	private void anadirSiExiste(ArrayList<GestorZona> candidatos, GestorLocalidad gestor, int i, int j) {
		GestorZona gestorZona = gestor.getGestorZona(i, j);
		if (gestorZona != null) {
			candidatos.add(candidatos.size(), gestorZona);
		}
	}
}
