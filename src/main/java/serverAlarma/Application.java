package serverAlarma;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import javassist.bytecode.Mnemonic;
import serverAlarma.Controller.MqttConnect;
import serverAlarma.Monitor.MonitorComponent;
import serverAlarma.Persistence.Postgresql.JPA.Interface.IUserBrocker;

import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;


@SpringBootApplication
@EnableScheduling
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
	@Bean
	public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> setConfiguration() {
		return factory -> {
			factory.setPort(8099);
		};
    }
    
	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			MqttClient client = MqttConnect.getInstance().getClient();
			if(client !=null) {
				System.out.println(MonitorComponent.getTime()+"  INFO	Start Proceduce Mqtt Connected: "+ client.isConnected());
				if(client.isConnected()) {
					client.subscribe("Deviceconfig/#");
					client.subscribe("homeassistant/#");
					System.out.println(MonitorComponent.getTime()+ "  INFO	Subscription Success to all Topics");
				}
			}
			else
				System.out.println(MonitorComponent.getTime()+ "  INFO	Client is NULL in start");		         
		};
	}
}

