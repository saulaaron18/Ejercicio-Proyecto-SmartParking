package modelo.reservas.solicitudesreservas;

import java.time.LocalDateTime;

import modelo.gestoresplazas.GestorLocalidad;
import modelo.gestoresplazas.GestorZona;
import modelo.gestoresplazas.huecos.Hueco;
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

		for (int di = -radio; di <= radio && !encontrado; di++) {
			int djAbs = radio - Math.abs(di);

			if ((gestorLocalidad.existeZona(getIZona() + di, getJZona() + djAbs)) || 
					(djAbs != 0 && gestorLocalidad.existeZona(getIZona() + di, getJZona() - djAbs))) {
				encontrado = true;
			}
		}

		return esValido && encontrado;
	}

	@Override
	public void gestionarSolicitudReserva(GestorLocalidad gestor) {
		super.gestionarSolicitudReserva(gestor);
		if (getHueco() == null) {

			ArrayList<GestorZona> candidatos = new ArrayList<>();
			recolectarCandidatosEnOrdenHorario(candidatos, gestor);

			for (int i = 0; i < candidatos.size() && getHueco() == null; i++) {
				GestorZona candidata = candidatos.get(i);
				Hueco hueco = candidata.reservarHueco(getTInicial(), getTFinal());

				if (hueco != null) {
					setHueco(hueco);
					setGestorZona(candidata);
				}
			}
		}
	}

	private void recolectarCandidatosEnOrdenHorario(ArrayList<GestorZona> candidatos,
			GestorLocalidad gestor) {
		int i = getIZona();
		int j = getJZona();

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

			int pos = 0;
			double precioGestorZona = gestorZona.getPrecio();
			while (pos < candidatos.size() &&
					candidatos.get(pos).getPrecio() <= precioGestorZona) {
				pos++;
			}

			candidatos.add(pos, gestorZona);
		}
	}
}
