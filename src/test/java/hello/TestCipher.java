package hello;

import org.junit.Test;

import serverAlarma.util.EncryptorPassword;

public class TestCipher {

	//@Test
	public void testCipherInfo() {
		try {
			String val=EncryptorPassword.encrypt("MENSAJElaqueva1");
			System.out.println(val);
			
			String res=EncryptorPassword.decrypt(val);
			System.out.println(res);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
