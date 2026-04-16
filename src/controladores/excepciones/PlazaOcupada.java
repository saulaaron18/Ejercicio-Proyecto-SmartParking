package controladores.excepciones;

public class PlazaOcupada extends Exception {
	private static final long serialVersionUID = 1L;

	public PlazaOcupada(String msg) {
		super(msg);
	}
}
