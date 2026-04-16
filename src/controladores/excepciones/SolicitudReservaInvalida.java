package controladores.excepciones;

public class SolicitudReservaInvalida extends Exception {
	private static final long serialVersionUID = 1L;

	public SolicitudReservaInvalida(String msg) {
		super(msg);
	}
}
