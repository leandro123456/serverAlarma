package serverAlarma;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.extern.slf4j.Slf4j;
import serverAlarma.Controller.MqttConnect;
import serverAlarma.Monitor.MonitorComponent;
import serverAlarma.util.Settings;

import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;

@Slf4j
@SpringBootApplication
@EnableScheduling
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
	@Bean
	public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> setConfiguration() {
		return factory -> {
			factory.setPort(Settings.getInstance().getServerPort());
			if(Settings.getInstance().getIsTLSEnable()) {
			Ssl ssl = new Ssl();
	    	//ssl.setKeyStore("/home/steven/Desktop/repo/serverAlarma/keystore/local/certserver.p12"); //in local
	    	ssl.setKeyStore("/var/cdash/bin/keystore/prod/certificateprod.p12"); //in production
	    	ssl.setKeyStorePassword("root");
	    	ssl.setKeyAlias("1");
	    	ssl.setKeyStoreType("PKCS12");
	    	factory.setSsl(ssl);
			}
		};
    }
    
//	@Bean
//	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
//		return args -> {
//			MqttClient client = MqttConnect.getInstance().getClient();
//			if(client !=null) {
//				log.info(MonitorComponent.getTime()+"  INFO	Start Proceduce Mqtt Connected: "+ client.isConnected());
//				if(client.isConnected()) {
////					client.subscribe("Deviceconfig/#");
//					client.subscribe("#");
//					log.info(MonitorComponent.getTime()+ "  INFO	Subscription Success to all Topics");
//				}
//			}
//			else
//				log.error(MonitorComponent.getTime()+ "  INFO	Client is NULL in start");		         
//		};
//	}
}

