package test;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;

import modelo.gestoresplazas.GestorLocalidad;
import modelo.gestoresplazas.GestorZona;
import modelo.gestoresplazas.huecos.Hueco;

public class TestGestorLocalidadOblig {
	
	@Rule //Se establece un time out general para todos los tests. Se debe comentar esta línea y la de abajo para depurar
    public TestRule  globalTimeout = new DisableOnDebug(Timeout.millis(100)); // 100 milisegundos máximos por test	
	
	
	private GestorLocalidad gestor;
	private int[][] plazas = {{1, 2},
			{3, 4}};
	private double[][] precios = {{1.0, 1.0}, {1.0, 2.0}};
	
	@Before
	public void setUp() {
		gestor = new GestorLocalidad(plazas, precios);
	}

	/**
	 * Comprueba que se ha definido correctamente el atributo gestoresZonas
	 */
	@Test
	public void testAtributo() {
		Class<?> className = GestorLocalidad.class;
		try {
			Field atributo = className.getDeclaredField("gestoresZonas");
			assertTrue("Debe declarar el atributo gestoresZonas como PRIVADO en la clase "+className, Modifier.isPrivate(atributo.getModifiers()));
			
			assertEquals("Debe declarar el atributo gestoresZonas de tipo GestorZona[][] en la clase "+className, 
					GestorZona[][].class.getTypeName(),atributo.getType().getTypeName());
		} catch (NoSuchFieldException e) {
			fail("Debe declarar el atributo gestoresZonas en la clase GestorLocalidad");
		}		
		
	}
	
	/**
	 * Comprueba el constructor de GestorLocalidad
	 */
	@Test
	public void testConst() {
		
		String listaPlazas = "";
		int cont = 0;

		for (int i=0; i<plazas.length; i++)
			for (int j=0; j<plazas[0].length; j++, cont++) {
				listaPlazas += "(" + cont + "-null)";
				assertEquals("No se han creado las plazas vacías requeridas para la zona "
						+ "o el método getGestorZona() no es correcto " + i + " " + j , 
						"[" + listaPlazas + "]",
						gestor.getGestorZona(i, j).getPlazas());
				listaPlazas += ", ";
				assertTrue("No se ha inicializado la zona con el precio adecuado", 
						gestor.getGestorZona(i, j).getPrecio() == precios[i][j]);
			}
	}
	
	/**
	 * Comprueba el método getRadioMax()
	 */
	@Test
	public void testGetMaxRadio() {
		assertEquals("No se ha devuelto el radio max I correcto", 1, gestor.getRadioMaxI());
		assertEquals("No se ha devuelto el radio max J correcto", 1, gestor.getRadioMaxJ());
	}

	/**
	 * Comprueba el método existeZona() para todas las zonas existentes y las 
	 * no existentes que se encuentren en la frontera externa (length+1)
	 */
	@Test
	public void testExisteZona() {
		for (int i=-1; i<plazas.length+1; i++)
			for (int j=-1; j<plazas[0].length+1; j++) {
				if (0 <= i && i < plazas.length && 0 <= j && j < plazas[0].length)
					assertTrue("La zona " + i + " " + j + " debería existir", gestor.existeZona(i, j));
				else
					assertFalse("La zona " + i + " " + j + " NO debería existir", gestor.existeZona(i, j));

			}
	}
	
	/**
	 * Comprueba el método existeHuecoReservado() tanto si existe como si no
	 */
	@Test
	public void testExisteHuecoReservado() {
		GestorZona gestorZona = gestor.getGestorZona(0, 1);
		LocalDateTime tI = LocalDateTime.of(2021, 10, 5, 1, 0);
		LocalDateTime tF = LocalDateTime.of(2021, 10, 5, 2, 30);

		Hueco hueco = gestorZona.reservarHueco(tI, tF);
		
		assertTrue("Debería existir el hueco " + hueco + " en la zona 0 1", 
				gestor.existeHuecoReservado(hueco, 0, 1));
		
		assertFalse("No debería existir el hueco " + hueco + " en la zona 1 0", 
				gestor.existeHuecoReservado(hueco, 1, 0));		
	}
	
	
	
}

