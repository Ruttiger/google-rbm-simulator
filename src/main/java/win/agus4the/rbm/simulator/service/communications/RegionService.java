package win.agus4the.rbm.simulator.service.communications;

import win.agus4the.rbm.simulator.model.communications.ManagementType;
import win.agus4the.rbm.simulator.model.communications.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service providing static regions.
 */
@Service
public class RegionService {
    private static final List<Region> REGIONS = List.of(
            new Region("REGION_UNSPECIFIED", "Unspecified", ManagementType.MANAGEMENT_TYPE_UNSPECIFIED),
            new Region("GOOGLE_TEST", "Google Test Region", ManagementType.GOOGLE_MANAGED),
            new Region("CARRIER_TEST", "Carrier Test Region", ManagementType.CARRIER_MANAGED)
    );
    private static final Logger log = LoggerFactory.getLogger(RegionService.class);

    public List<Region> list() {
        log.debug("Returning {} regions", REGIONS.size());
        return REGIONS;
    }
}
