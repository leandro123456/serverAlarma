package serverAlarma.Controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import serverAlarma.Persistence.DAO.UserDAO;


@Controller
public class DeviceController {
	
	@GetMapping(value = "coiaca/releaseIntermedial1-0-1")
	public @ResponseBody byte[] releaseIntermedial110() throws IOException {
		System.out.println("llego la peticion!!");
	    Resource resource=null;
        try {
            Path filePath = FileSystems.getDefault().getPath("/home/cdash/alarmaIntermedial101.bin");
            resource = new UrlResource(filePath.toUri());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        byte[] resultado=IOUtils.toByteArray(resource.getInputStream());
	    System.out.println("resultado: "+ resultado);
	    return resultado;
	}
	
	@GetMapping(value = "coiaca/release1-0-1")
	public @ResponseBody byte[] getImage() throws IOException {
		System.out.println("llego la peticion!!");
	    Resource resource=null;
        try {
            Path filePath = FileSystems.getDefault().getPath("/home/cdash/alarma101.bin");
            resource = new UrlResource(filePath.toUri());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        byte[] resultado=IOUtils.toByteArray(resource.getInputStream());
	    System.out.println("resultado: "+ resultado);
	    return resultado;
	}
}
