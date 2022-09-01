package serverAlarma.Persistence.Postgresql.Controller;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import serverAlarma.Persistence.Postgresql.JPA.Interface.IDeviceParticular;
import serverAlarma.Persistence.Postgresql.JPA.Interface.IDeviceStatus;
import serverAlarma.Persistence.Postgresql.Model.DeviceParticular;
import serverAlarma.Persistence.Postgresql.Model.DeviceStatus;

@CrossOrigin(origins = "*")
@RestController
public class DeviceParticularController {
	@Autowired
	private IDeviceParticular idevice;
	
	@Autowired
	private IDeviceStatus idevicestatus;
	
	//, consumes = "application/json"
	@PostMapping(value="/useralarm/getdevices")
	public String AllDevicesByuser(@RequestBody String jsoninfo) {
		JSONObject jsonRequest = new JSONObject(jsoninfo);
		String owner= jsonRequest.getString("mail");
		List<DeviceParticular> devices= idevice.findAllByOwner(owner);
		String resp="";
		if(!devices.isEmpty()) {
			for(DeviceParticular dev: devices) {
				resp=resp+"-"+dev.getDevid();
			}
			return resp;
		}
		else
			return "List empty";
	}
	
	
	@PostMapping(value="/usermanagement/getdevices")
	public String AllDevicesByuser1(@RequestBody String jsoninfo) {
		String jsonList=null;
		System.out.println("INFO: "+ jsoninfo);		
		jsoninfo=jsoninfo.replaceAll("[^a-zA-Z0-9-,]", "");
		String[] listArray= jsoninfo.split(",");
		List<DeviceParticular> listResult=new ArrayList<DeviceParticular>();
		for(int i=0; i<listArray.length; i++) {
			System.out.println(listArray[i]);
			DeviceParticular device= idevice.findByDevid(listArray[i]);
			if(device!=null) {
				listResult.add(device);
			}
			Gson gson = new Gson();
			jsonList=  gson.toJson(listResult);
			
			
			
		}
		System.out.println(jsonList);
		return jsonList;
	}
	
	@PutMapping(value="/devicemanagement/update/{deviceId}/{showZones}")
	public String deviceUpdate(@PathVariable String deviceId,@PathVariable Integer showZones,
			@RequestBody String jsoninfo) {
		System.out.println("zones: "+ showZones);
		JSONObject jsonRespose = new JSONObject();
		try {
			DeviceParticular device= idevice.findAllByDeviceId(deviceId);
			if(device!=null) {
				JSONObject json = new JSONObject(jsoninfo);
				System.out.println("INFORMACION A VALIDAR: "+json.toString());
				DeviceStatus devSatus= idevicestatus.findByDevid(deviceId);
				devSatus.setShowZones(showZones);
				idevicestatus.saveDeviceStatus(devSatus);
				
				//updateDevice
				if(!json.isNull("deviceName") && json.has("deviceName")) device.setDeviceName(json.getString("deviceName"));
				if(!json.isNull("armed_away") && json.has("armed_away")) device.setArmedAway(json.getString("armed_away"));
				if(!json.isNull("armedHome") && json.has("armedHome")) device.setArmedHome(json.getString("armedHome"));
				if(!json.isNull("armedNite") && json.has("armedNite")) device.setArmedNite(json.getString("armedNite"));
				if(!json.isNull("disarm") && json.has("disarm")) device.setDisarm(json.getString("disarm"));
				if(!json.isNull("activeOn") && json.has("activeOn")) device.setActiveOn(json.getString("activeOn"));
				if(!json.isNull("activeOff") && json.has("activeOff")) device.setActiveOff(json.getString("activeOff"));
				if(!json.isNull("topicStatus") && json.has("topicStatus")) device.setTopicStatus(json.getString("topicStatus"));
				if(!json.isNull("topicMsg") && json.has("topicMsg")) device.setTopicMsg(json.getString("topicMsg"));
				if(!json.isNull("topicZones") && json.has("topicZones")) device.setTopicZones(json.getString("topicZones"));
				if(!json.isNull("topicComands") && json.has("topicComands")) device.setTopicComands(json.getString("topicComands"));
				if(!json.isNull("topicTrouble") && json.has("topicTrouble")) device.setTopicTrouble(json.getString("topicTrouble"));
				if(!json.isNull("topicSignal") && json.has("topicSignal")) device.setTopicSignal(json.getString("topicSignal"));
				if(!json.isNull("topicActPartition") && json.has("topicActPartition")) device.setTopicActPartition(json.getString("topicActPartition"));
				
				idevice.saveDeviceParticular(device);
				System.out.println("actualizo device");
				
			}else {
				jsonRespose.put("result", "failed - device dont exist");
				return jsonRespose.toString();
			}
			jsonRespose.put("result", "ok");
			return jsonRespose.toString();
		}catch (Exception e) {
			e.printStackTrace();
			jsonRespose.put("result", "failed");
			return jsonRespose.toString();
		}
	}
	
	@PostMapping(value="/devicemanagement/create")
	public String deviceUpdate(@RequestBody String jsoninfo) {
		System.out.println(jsoninfo);
		JSONObject jsonRespose = new JSONObject();
		try {
			JSONObject json = new JSONObject(jsoninfo);
			System.out.println(json.toString());
			int showZones=Integer.parseInt(json.getString("showZones"));
			json.remove("showZones");
			System.out.println(json.toString());
			DeviceParticular device= new DeviceParticular();
			if(device!=null) {
//				DeviceStatus devSatus= idevicestatus.findByDevid(deviceId);
//				devSatus.setShowZones(showZones);
//				idevicestatus.saveDeviceStatus(devSatus);
//				
//				//DeviceParticular deviceParticular= new DeviceParticular(jsoninfo);
				
			}else {
				jsonRespose.put("result", "failed - device dont exist");
				return jsonRespose.toString();
			}
			jsonRespose.put("result", "ok");
			return jsonRespose.toString();
		}catch (Exception e) {
			jsonRespose.put("result", "failed");
			return jsonRespose.toString();
		}
	}
	
	@PutMapping(value="/devicemanagement/updatedata")
	public void deviceUpdateSatus(@RequestBody String jsoninfo) {
		try {
			System.out.println("device INFO: "+ jsoninfo);	
			JSONObject json = new JSONObject(jsoninfo);
			String topic=json.getString("topico");
			String msg= json.getString("payload");

			String[] topicP=topic.split("/");
			String deviceId=topicP[1];
			System.out.println(deviceId);
			String topicKey="";
			for(int i=2; i<topicP.length; i++) { 
				topicKey=topicKey+topicP[i]+"-";
			}
			if(topicKey.endsWith("-")) {
				topicKey=topicKey.substring(0, topicKey.length()-1);
			}
			System.out.println("topic key: "+ topicKey);
			DeviceParticular devParticular= idevice.findAllByDeviceId(deviceId);
			if(devParticular!=null) {
				DeviceStatus devStatus= idevicestatus.findByDevid(deviceId);
				if(devStatus!=null) {
					if(topic.equals(devParticular.getTopicActPartition())) {
						devStatus.setActPartition(Integer.parseInt(msg));
					}
					if(topic.equals(devParticular.getTopicTrouble())) {
						devStatus.setTrouble(Integer.parseInt(msg));
					}
					if(topic.equals(devParticular.getTopicStatus())) {
						devStatus.setStatus(msg);
					}

					JSONObject jsonData = null;
					if(devStatus.getImportData()!=null && !devStatus.getImportData().isEmpty()) {
						jsonData=new JSONObject(devStatus.getImportData());
						//if(jsonData.get(topicKey)!=null)
							jsonData.remove(topicKey);
						jsonData.put(topicKey, msg);
					}else {
						jsonData=new JSONObject();
						jsonData.put(topicKey, msg);
					}
					devStatus.setImportData(jsonData.toString());
					idevicestatus.saveDeviceStatus(devStatus);
					System.out.println("ya guardo");
				}else {
					DeviceStatus devSta= new DeviceStatus();
					devSta.setDevid(deviceId);
					devSta.setActPartition(0);
					devSta.setStatus("");
					devSta.setTrouble(0);
					devSta.setTipo(devParticular.getTipo());
					JSONObject jsonData = new JSONObject();
					jsonData.put(topicKey, msg);

					if(topic.equals(devParticular.getTopicActPartition())) {
						devSta.setActPartition(Integer.parseInt(msg));
					}
					if(topic.equals(devParticular.getTopicTrouble())) {
						devSta.setTrouble(Integer.parseInt(msg));
					}
					if(topic.equals(devParticular.getTopicStatus())) {
						devSta.setStatus(msg);
					}
					idevicestatus.saveDeviceStatus(devSta);
				}

			}
			else {
				System.out.println("device no exist!!! "+ deviceId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@GetMapping(value="/devicemanagement/getdata/{deviceId}")
	public String deviceDetail(@PathVariable String deviceId) {
		System.out.println("INFO: "+ deviceId);
		DeviceStatus dev=idevicestatus.findByDevid(deviceId);
		JSONObject jsonRequest = new JSONObject();
		if(dev!=null) {
			JSONObject jsonData = new JSONObject(dev.getImportData());
			String statusPartition= "";
			try {
				statusPartition=(String)jsonData.get("Partition"+dev.getActPartition());
			} catch (Exception e) {
				e.printStackTrace();
			}
			jsonRequest.put("activepartition", dev.getActPartition());
			jsonRequest.put("partitionstatus"+dev.getActPartition(), statusPartition);
			jsonRequest.put("globalstatus", dev.getStatus());
			jsonRequest.put("trouble", dev.getTrouble());
			jsonRequest.put("showzones", dev.getShowZones());
		}else {
		jsonRequest.put("activepartition", 1);
		jsonRequest.put("partitionstatus1", "disarmed");
		jsonRequest.put("globalstatus", "offline");
		jsonRequest.put("trouble", 0);
		jsonRequest.put("data", "demo-data");
		jsonRequest.put("showzones", 0);
		}
		return jsonRequest.toString();
	}
	
	
	@GetMapping(value="test/device/createdevice")
	public String TestCreateDevice() {
		try {
			DeviceParticular dev= new DeviceParticular();
			dev.setDevid("DSC010000000105");
			dev.setTipo("DSC01");
			dev.setDeviceName("Alarma Casa");
			dev.setUserowner("leandroplay1448@gmail.com");
			List<String> usershared=new ArrayList<>();
			usershared.add("juan jose");
			usershared.add("pedro");
			dev.setUsershared(usershared.toString());
			dev.setArmedAway("A");
			dev.setArmedHome("S");
			dev.setArmedNite("N");
			dev.setDisarm("D");
			dev.setTopicStatus("52B5493A/DSC010000000105/Status");
			dev.setTopicSignal("52B5493A/DSC010000000105/keepAlive");
			dev.setTopicComands("52B5493A/DSC010000000105/cmd");
			dev.setTopicActPartition("52B5493A/DSC010000000105/activePartition");
			dev.setTopicMsg("52B5493A/DSC010000000105/MNTR");
			dev.setTopicZones("52B5493A/DSC010000000105/Zone");
			dev.setTopicTrouble("52B5493A/DSC010000000105/Trouble");
			dev.setActiveZones(2);
			
			
			idevice.saveDeviceParticular(dev);
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}
}
