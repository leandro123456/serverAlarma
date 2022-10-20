package hello;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
	
	@Test
	public void testDays() {
		System.out.println(formatterDay(new Date()));
		System.out.println(targetDay(360));
	}
	
	
	public static String targetDay(int days) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -days);
		Date nowMinus = c.getTime();
		return formatterDay(nowMinus);
	}

	public static String formatterDay( Date date) {
		String pattern = "yyyy-MM-dd";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		return  simpleDateFormat.format(date);
		
	}
}
