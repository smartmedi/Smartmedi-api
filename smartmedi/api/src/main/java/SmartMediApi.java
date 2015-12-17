import com.smartmedi.app.api.Routes.*;
import com.smartmedi.app.api.appDI.AppDI;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.restexpress.RestExpress;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by vignesh on 17/4/15.
 */
public class SmartMediApi {

   public static void main(String args[]){
       String file= Thread.currentThread().getContextClassLoader().getResource("api.properties").getFile();
        PropertiesConfiguration propertiesConfiguration=new PropertiesConfiguration();
        propertiesConfiguration.setDelimiterParsingDisabled(true);
        try {
            propertiesConfiguration.load(file);
        } catch (ConfigurationException e){
            e.printStackTrace();
        }
        final AnnotationConfigApplicationContext annotation_context=new AnnotationConfigApplicationContext();
        annotation_context.getBeanFactory().registerSingleton("config",propertiesConfiguration);
        annotation_context.register(AppDI.class);
        annotation_context.refresh();
        int buffer_size= propertiesConfiguration.getInt("restexpress.receive.buffer.size");
        int thread_count= propertiesConfiguration.getInt("restexpress.io.thread.count");
        int executor_thread_count= propertiesConfiguration.getInt("restexpress.executor.thread.count");
        int server_port=propertiesConfiguration.getInt("server.port");
        RestExpress restExpress=new RestExpress().setName("API").setKeepAlive(true).setReuseAddress(true);
        restExpress.setReceiveBufferSize(buffer_size);
        restExpress.setIoThreadCount(thread_count);
        restExpress.setExecutorThreadCount(executor_thread_count);

        AllRoutes.defineRoutes(restExpress,annotation_context);
        restExpress.setMaxContentSize(1024 * 1024 * 10);
        int maxPorts = propertiesConfiguration.getInt("server.port.max", 1);
        int startPort = propertiesConfiguration.getInt("server.port", server_port);
        for (int i = 0; i < maxPorts; i++) {
            restExpress.bind(startPort + i);
        }


    }
}
