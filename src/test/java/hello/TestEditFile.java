package hello;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.gson.Gson;

import net.minidev.json.JSONObject;
import serverAlarma.Persistence.Postgresql.Model.DeviceParticular;

public class TestEditFile {
	
	@Test
	public void parseListtoArrayLIst() {
		String listMap= "[DSC0100000010,DSADSADASDSA]";
		listMap=listMap.replaceAll("[^a-zA-Z0-9-,]", "");
		String[] listArray= listMap.split(",");
		List<DeviceParticular> listResult=new ArrayList<DeviceParticular>();
		for(int i=0; i<listArray.length; i++) {
			System.out.println(listArray[i]);

			//DeviceParticular device= idevice.findByDevid(listArray[i]);
//			if(device!=null) {
//				listResult.add(device);
//			}
//			Gson gson = new Gson();
//			String jsonList =  gson.toJson(listResult);
//			System.out.println(jsonList);
			
		}
		
		String resp="";
	}
	
	//@Test
	public void testPasarBoolean() {
		String val="1";
		Boolean bool = Boolean.parseBoolean(val);
		System.out.println("res: "+ bool );
	}
	
	//@Test
	public void generateRandomColor(){
		String[] letters = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
		String color = "";
		for (int i = 0; i < 8; i++ ) {
		    color += letters[(int) Math.round(Math.random() * 15)];
		}
		System.out.println( color);
	}
	
	//@Test
	public void testEditFile() {
		String eid = "89034011560010000000000000000123";
		String isdpAid = "A0000005591010FFFFFFFF8900001000";
		String iccid = "8942306000041201241";
		String msisdn = "423651234321";
		String FileName = "/home/steven/Desktop/templateINI.ini";
		BufferedReader br = null;
		BufferedWriter bw = null;

		try {
			br = new BufferedReader(new FileReader(FileName));
			bw = new BufferedWriter(new FileWriter("/home/steven/Desktop/" + eid + ".ini"));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("EID"))
					line = line.replace("EID", eid + "\n");
				else if (line.contains("ISDPAID"))
					line = line.replace("ISDPAID", isdpAid + "\n");
				else if (line.contains("ICCID"))
					line = line.replace("ICCID", iccid + "\n");
				else if (line.contains("MSISDN"))
					line = line.replace("MSISDN", msisdn + "\n");
				else
					line = line + "\n";
				bw.write(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (bw != null)
					bw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
