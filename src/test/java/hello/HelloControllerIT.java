package hello;

import java.net.URI;
import java.util.Date;
import java.util.Random;

import org.junit.Test;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import serverAlarma.Controller.MailController;
import serverAlarma.util.Utils;


public class HelloControllerIT {
	
	//@Test
	public void testCreateHashPassword() {
		System.out.println(Utils.hashPassword("sapo"));
	}
	
//	@Test
	public void generarRandomLongitud9(){
		for(int i=0; i<10 ;i++){
			Random numsala = new Random();
			String val = numsala.nextInt()+"";
			val=val.replace("-", "");
			if(val.length()>9)
				val=val.substring(0, 9);
			System.out.println("num: "+ val);
		}
	}
	

	
	//@Test
	public void testSendNotificationTwo() {
		
		
		try {
			String token= "caRfPUL-HakRXf3Kvk097D:APA91bEIE52nR9_-HktR58gtPMS3Q__u-_pQeTXBb2aCOGNVxrgr6uxiRqhnFZtqyGNoDYqizBzGfc9mmsRcDG_bkuaNIn7RDBcZRA8LGa3uzoDU9WikaHiAj90YJtFhlj8OyBG8nNPw";
			URI uri= new URI("https://www.facebook.com");
			OkHttpClient client = new OkHttpClient();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body1 = RequestBody.create(mediaType, 
					"{\"notification\":{ "
							+ "\"title\": \"cDash Notificacion\", "
							+ "\"score\": \"5x1\","
							+ "\"time_to_live\": 5,"
							+ "\"click_action\": \""+uri+"\","
							+ "\"body\": \"This is a Firebase Cloud Messaging Device Group Message!\","
							+ "},"
							+ "\"to\":\""+token+"\""
							+ "}");
			
			System.out.println("este es el mensaje que envio: "+ body1);	

			Request request = new Request.Builder()
					.url("https://fcm.googleapis.com/fcm/send")
					.method("POST", body1)
					.addHeader("Authorization","key=AIzaSyAmq0sl80gwzNIj7b74Y-QrpwpBSe-itWI")
					.addHeader("Content-Type","application/json")
					.build();
			Response response = client.newCall(request).execute();
			System.out.println(response.code());
			String responseString= response.body().string();
			System.out.println(responseString);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public Message allPlatformsMessage() {
	    Message message = Message.builder()
	        .setNotification(new Notification(
	            "$GOOG up 1.43% on the day",
	            "$GOOG gained 11.80 points to close at 835.67, up 1.43% on the day."))
	        .setAndroidConfig(AndroidConfig.builder()
	            .setTtl(3600 * 1000)
	            .setNotification(AndroidNotification.builder()
	                .setIcon("stock_ticker_update")
	                .setColor("#f45342")
	                .build())
	            .build())
	        .setApnsConfig(ApnsConfig.builder()
	            .setAps(Aps.builder()
	                .setBadge(42)
	                .build())
	            .build())
	        .setTopic("industry-tech")
	        .build();
	    return message;
	  }
	
}
