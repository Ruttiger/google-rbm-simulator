package win.agus4the.rbm.simulator.controller.communications;

import win.agus4the.rbm.simulator.service.communications.RegionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller exposing static regions.
 */
@RestController
public class RegionController {

    private final RegionService regionService;
    private static final Logger log = LoggerFactory.getLogger(RegionController.class);

    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    @GetMapping("/v1/regions")
    public Map<String, Object> listRegions() {
        log.info("Listing regions");
        return Map.of("regions", regionService.list());
    }
}
