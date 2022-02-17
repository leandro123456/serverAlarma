package hello;

import java.util.Date;

import org.junit.Test;


public class TestNotificaciones {

	//@Test
	public void testEsperar() {
		try { 
			System.out.println("espera: "+ new Date().toString());
			Thread.sleep(5000);
			System.out.println("termino espera: "+ new Date().toString());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Thread Interrupted");
        }
		System.out.println("termino");
	}
	
}
