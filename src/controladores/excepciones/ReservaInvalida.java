package controladores.excepciones;

public class ReservaInvalida extends Exception {
	private static final long serialVersionUID = 1L;

	public ReservaInvalida(String msg) {
		super(msg);
	}
}
