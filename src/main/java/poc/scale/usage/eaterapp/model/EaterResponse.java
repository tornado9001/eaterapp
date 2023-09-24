package poc.scale.usage.eaterapp.model;

import java.util.Map;


/**
 * This record is for quick response crafting.
 */
public record EaterResponse(Version version, Map<String, String> data) {

}

