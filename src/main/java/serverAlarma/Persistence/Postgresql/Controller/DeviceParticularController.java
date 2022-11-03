package serverAlarma.Persistence.Postgresql.Controller;

import java.util.ArrayList;
import java.util.Date;
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

import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.gson.Gson;

import serverAlarma.Persistence.Postgresql.JPA.Interface.IDeviceParticular;
import serverAlarma.Persistence.Postgresql.JPA.Interface.IDeviceStatus;
import serverAlarma.Persistence.Postgresql.JPA.Interface.IUserAlarm;
import serverAlarma.Persistence.Postgresql.Model.DeviceParticular;
import serverAlarma.Persistence.Postgresql.Model.DeviceStatus;
import serverAlarma.Persistence.Postgresql.Model.UserAlarm;
import serverAlarma.util.Utils;

@CrossOrigin(origins = "*")
@RestController
public class DeviceParticularController {
	@Autowired
	private IDeviceParticular idevice;
	
	@Autowired
	private IDeviceStatus idevicestatus;
	
	@Autowired
	private IUserAlarm iuser;
	
	@GetMapping(value="/devicemanagement/findbydevid/{deviceId}")
	public DeviceParticular deviceUpdate(@PathVariable String deviceId) {
		System.out.println(new Date().toString() +" - DeviceId: "+ deviceId);
		DeviceParticular device=idevice.findAllByDeviceId(deviceId);
		return device;
	}
	
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
		//System.out.println("INFO: "+ jsoninfo);		
		jsoninfo=jsoninfo.replaceAll("[^a-zA-Z0-9-,]", "");
		String[] listArray= jsoninfo.split(",");
		List<DeviceParticular> listResult=new ArrayList<DeviceParticular>();
		for(int i=0; i<listArray.length; i++) {
			//System.out.println(listArray[i]);
			DeviceParticular device= idevice.findByDevid(listArray[i]);
			if(device!=null) {
				listResult.add(device);
			}
			Gson gson = new Gson();
			jsonList=  gson.toJson(listResult);
			
			
			
		}
		//System.out.println(jsonList);
		return jsonList;
	}
	
	@PutMapping(value="/devicemanagement/update/{deviceId}/{showZones}")
	public String deviceUpdate(@PathVariable String deviceId,@PathVariable Integer showZones,
			@RequestBody String jsoninfo) {
		//System.out.println("zones: "+ showZones);
		JSONObject jsonRespose = new JSONObject();
		try {
			DeviceParticular device= idevice.findAllByDeviceId(deviceId);
			if(device!=null) {
				JSONObject json = new JSONObject(jsoninfo);
				//System.out.println("INFORMACION A VALIDAR: "+json.toString());
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
				//System.out.println("actualizo device");
				
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
	
	@PostMapping(value="/devicemanagement/create/{showZones}")
	public String deviceUpdate(@RequestBody String jsoninfo,@PathVariable Integer showZones) {
		//System.out.println(jsoninfo);
		JSONObject jsonRespose = new JSONObject();
		try {
			JSONObject json1 = new JSONObject(jsoninfo);
			//System.out.println(json1.toString());
			String deviceId=json1.getString("devid");
			DeviceParticular device= idevice.findAllByDeviceId(deviceId);
			if(device!=null) {
				DeviceStatus devSatus= idevicestatus.findByDevid(deviceId);
				devSatus.setShowZones(showZones);
				idevicestatus.saveDeviceStatus(devSatus);
				
				JSONObject json = new JSONObject(jsoninfo);
				//System.out.println("INFORMACION A VALIDAR: "+json.toString());
				DeviceStatus devSatus1= idevicestatus.findByDevid(deviceId);
				devSatus1.setShowZones(showZones);
				idevicestatus.saveDeviceStatus(devSatus1);
				
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
				//System.out.println("se actualizo el device");
				//update UserList
				String resUser=updateUserDevices(json.getString("userowner"),deviceId);
				if(!resUser.equals("ok")) {
					jsonRespose.put("result", "failed");
					jsonRespose.put("id",9999);
					return jsonRespose.toString();
				}
				
			}else {
				device= new DeviceParticular();
				JSONObject json = new JSONObject(jsoninfo);
				//System.out.println("INFORMACION A VALIDAR: "+json.toString());
				DeviceStatus devSatus= idevicestatus.findByDevid(deviceId);
				if(devSatus!=null) {
				devSatus.setShowZones(showZones);
				}
				else {
					devSatus= new DeviceStatus();
					devSatus.setActPartition(1);
					devSatus.setDevid(deviceId);
					devSatus.setShowZones(showZones);
					devSatus.setStatus("offline");
					devSatus.setTipo(json.getString("tipo"));
					devSatus.setTrouble(0);
					JSONObject tmpjs= new JSONObject();
					tmpjs.put("Status", "offline");
					tmpjs.put("MNTR-DetailPartition1","Partition ready");
					tmpjs.put("activePartition", "1");
					tmpjs.put("Partition1", "disarmed");
					tmpjs.put("Partition2", "disarmed");
					tmpjs.put("Partition3", "disarmed");
					tmpjs.put("Partition4", "disarmed");
					tmpjs.put("Partition5", "disarmed");
					tmpjs.put("Partition6", "disarmed");
					tmpjs.put("Partition7", "disarmed");
					tmpjs.put("Partition8", "disarmed");
					JSONObject intJson= new JSONObject();
					intJson.put("\"deviceID\"", "\""+deviceId+"\"");
					intJson.put("\"DSC\"",1);
					intJson.put("\"MQTT\"", 1);
					intJson.put("\"dBm\"", -68);
					tmpjs.accumulate("keepAlive", intJson);
					devSatus.setImportData(tmpjs.toString());
				}
				idevicestatus.saveDeviceStatus(devSatus);
				
				//createDevice
				if(!json.isNull("devid") && json.has("devid")) device.setDevid(json.getString("devid"));
				if(!json.isNull("tipo") && json.has("tipo")) device.setTipo(json.getString("tipo"));
				if(!json.isNull("userowner") && json.has("userowner")) device.setUserowner(json.getString("userowner"));
				
				if(!json.isNull("activeOn") && json.has("activeOn")) device.setActiveOn(json.getString("activeOn"));
				if(!json.isNull("activeOff") && json.has("activeOff")) device.setActiveOff(json.getString("activeOff"));
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
				//System.out.println("se creo el device");
				//update UserList
				String resUser=updateUserDevices(json.getString("userowner"),deviceId);
				if(!resUser.equals("ok")) {
					jsonRespose.put("result", "failed");
					jsonRespose.put("id",9999);
					return jsonRespose.toString();
				}
				
			}

			
			jsonRespose.put("result", "ok");
			jsonRespose.put("id",device.getId());
			return jsonRespose.toString();
		}catch (Exception e) {
			e.printStackTrace();
			jsonRespose.put("result", "failed");
			jsonRespose.put("id",9999);
			return jsonRespose.toString();
		}
	}
	
	
	@GetMapping(value="/devicemanagement/delete/{deviceId}/{usermail}")
	public String deviceUpdate(@PathVariable String deviceId,@PathVariable String usermail) {
		JSONObject jsonRespose = new JSONObject();
		//System.out.println(usermail);
		String user=new String(Base64.decodeBase64(usermail.getBytes()));
		//System.out.println("user: "+ user);
		try {
			DeviceParticular device=idevice.findAllByDeviceId(deviceId);
			if(device!=null) {
				if(device.getUserowner().equals(user)) {
					device.setUserowner("");
					idevice.saveDeviceParticular(device);
				}
				if(device.getUsershared()!=null && device.getUsershared().contains(user)) {
					String tmpus=device.getUsershared().replace(user,"");
					tmpus= tmpus.replace("[,", "[");
					tmpus=tmpus.replace(",,", ",");
					tmpus=tmpus.replace(",]", "]");
					device.setUsershared(tmpus);
					idevice.saveDeviceParticular(device);
				}
				UserAlarm user1= iuser.findUserAlarmByEmail(user);
				if(user1!=null) {
					String tmpus=user1.getDevices().replace(deviceId, "");
					tmpus= tmpus.replace("[,", "[");
					tmpus=tmpus.replace(",,", ",");
					tmpus=tmpus.replace(",]", "]");
					user1.setDevices(tmpus);
					iuser.saveUserAlarm(user1);
				}else {
					jsonRespose.put("result", "failed - unknow user mail");
					jsonRespose.put("id",deviceId);
					return jsonRespose.toString();
				}
				jsonRespose.put("result", "ok");
				jsonRespose.put("id",deviceId);
				return jsonRespose.toString();
			}
			else {
				jsonRespose.put("result", "failed - unknow device");
				jsonRespose.put("id",deviceId);
				return jsonRespose.toString();
			}
		}catch (Exception e) {
			e.printStackTrace();
			jsonRespose.put("result", "failed -"+ e.getMessage());
			jsonRespose.put("id","error");
			return jsonRespose.toString();
		}
	}
	
	private String updateUserDevices(String email, String deviceId) {
		String result="";
		//System.out.println("mail: "+ email);
		UserAlarm user= iuser.findUserAlarmByEmail(email);
		//System.err.println("usurio: "+ user);
		if(user!=null) {
			String devices= user.getDevices();
			if(devices!=null && !devices.equals("")) {
			devices=devices.replaceAll("]", ","+deviceId+"]");
			}
			else
				devices="["+deviceId+"]";
			user.setDevices(devices);
			iuser.saveUserAlarm(user);
			result="ok";
		}else {
			result="error user null";
		}
			
		//System.out.println("se actualizo el usuario");
		return result;
		
	}
	
	@PostMapping(value="/devicestatus/update")
	public String deviceDeviceSatus(@RequestBody String jsoninfo) {
		try {
			JSONObject json = new JSONObject(jsoninfo);
			String topic=json.getString("topico");
			String msg= json.getString("payload");
			String deviceId= json.getString("deviceid");
			
			DeviceStatus devStatus= idevicestatus.findByDevid(deviceId);
			DeviceParticular devParticular= idevice.findAllByDeviceId(deviceId);
			
			if(devStatus!=null) {
				devStatus= Utils.updateDeviceStatusValues(devParticular,devStatus,msg,topic,deviceId);
				idevicestatus.saveDeviceStatus(devStatus);
				return "success";
			}
			return "devStatus is null";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	

	@PutMapping(value="/devicemanagement/updatedata")
	public void deviceUpdateSatus(@RequestBody String jsoninfo) {
		try {
			JSONObject json = new JSONObject(jsoninfo);
			String topic=json.getString("topico");
			String msg= json.getString("payload");

			String[] topicP=topic.split("/");
			String deviceId=topicP[1];
			String topicKey=Utils.obtainTopicKey(topicP);
			DeviceParticular devParticular= idevice.findAllByDeviceId(deviceId);
			if(devParticular!=null) {
				DeviceStatus devStatus= idevicestatus.findByDevid(deviceId);
				if(devStatus!=null) {
					devStatus= Utils.updateDeviceStatusValues(devParticular,devStatus,msg,topic,null);
					idevicestatus.saveDeviceStatus(devStatus);
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
		//System.out.println("INFO: "+ deviceId);
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
	
	@PostMapping(value="/devicemanagment/getpartition")
	public String PartitionDisplayInfo(@RequestBody String jsoninfo) {
		JSONObject jsonResponse= new JSONObject();
		try {
			//System.out.println("device INFO: "+ jsoninfo);	
			JSONObject json = new JSONObject(jsoninfo);
			String deviceId=json.getString("deviceid");
			String partitionNumber= json.getString("partitionNumber");
			DeviceStatus device= idevicestatus.findByDevid(deviceId);
			if(device!=null) {
				String data=device.getImportData();
				JSONObject jsonData=new JSONObject(data);
				String result=jsonData.getString("Partition"+partitionNumber);
				jsonResponse.put("result", "ok");
				jsonResponse.put("partition", result);
				return jsonResponse.toString();
			}else {
				jsonResponse.put("result", "failed - Device is null");
				return jsonResponse.toString();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			jsonResponse.put("result", "failed - "+ e.getMessage());
			return jsonResponse.toString();
		}
		
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
