package com.messi.rbm.simulator.service.communications;

import com.messi.rbm.simulator.model.communications.ManagementType;
import com.messi.rbm.simulator.model.communications.Region;
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

    public List<Region> list() {
        return REGIONS;
    }
}
