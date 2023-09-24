package poc.scale.usage.eaterapp.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import poc.scale.usage.eaterapp.model.Version;

@RestController
@RequestMapping("/version")
public class VersionApi {
    @Autowired
    private Version version;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Version> getVersion() {
        return ResponseEntity.ok(version);
    }
}
