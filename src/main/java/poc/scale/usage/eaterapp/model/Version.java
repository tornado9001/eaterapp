package poc.scale.usage.eaterapp.model;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class Version {
    // server name with default name by machine
    @Value("${server.name}")
    private String server;
    // server version with default version by machine
    private String version = "1.0.0";
}
