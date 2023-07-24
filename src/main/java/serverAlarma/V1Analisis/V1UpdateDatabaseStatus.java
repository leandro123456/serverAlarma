package serverAlarma.V1Analisis;


import java.util.Date;

import serverAlarma.Persistence.Postgresql.Model.DeviceParticular;
import serverAlarma.util.CommonRequest;


public class V1UpdateDatabaseStatus {

	public static void UpdateForTopic(String topic, String msg) {
		try {
			//System.out.println(new Date().toString() +" - Analisys topic: " + topic + " message: "+ msg);
			String[]topico= topic.split("/");
			if(!msg.equals("pending")) {
				CommonRequest commonRequest= new CommonRequest();
				DeviceParticular device= commonRequest.ObtainDeviceNameByDeviceID(topico[0]);
				if(device!=null) {
					String result=CommonRequest.SendUpdateDeviceStatusRequest(topico[0],topic,msg);
					System.out.println(new Date().toString() +" - Operation Result: "+ result);
				}
				else {
					System.err.println(new Date().toString() +" - Device is NULL: " + topico[0]);
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
