package win.agus4the.rbm.simulator.service.communications;

import win.agus4the.rbm.simulator.model.communications.Brand;
import win.agus4the.rbm.simulator.repo.communications.RbmMemoryRepository;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service handling brand operations.
 */
@Service
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Repository is injected and not exposed")
public class BrandService {
    private final RbmMemoryRepository repo;
    private static final Logger log = LoggerFactory.getLogger(BrandService.class);

    public BrandService(RbmMemoryRepository repo) {
        this.repo = repo;
    }

    public Brand create(String displayName) {
        Brand brand = repo.createBrand(displayName);
        log.info("Created brand id={} name={}", brand.getName(), displayName);
        return brand;
    }

    public Optional<Brand> get(String brandId) {
        Optional<Brand> brand = repo.getBrand(brandId);
        log.debug("Get brand id={} found={} ", brandId, brand.isPresent());
        return brand;
    }

    public List<Brand> list() {
        List<Brand> list = repo.listBrands();
        log.debug("List brands count={}", list.size());
        return list;
    }

    public Optional<Brand> patch(String brandId, Map<String, Object> patch) {
        if (patch.containsKey("displayName")) {
            Optional<Brand> updated = repo.updateBrand(brandId, (String) patch.get("displayName"));
            updated.ifPresent(b -> log.info("Patched brand id={} newName={}", brandId, patch.get("displayName")));
            return updated;
        }
        Optional<Brand> brand = repo.getBrand(brandId);
        if (brand.isEmpty()) {
            log.warn("Brand not found for patch id={}", brandId);
        }
        return brand;
    }

    public boolean delete(String brandId) {
        boolean deleted = repo.deleteBrand(brandId);
        if (deleted) {
            log.info("Deleted brand id={}", brandId);
        } else {
            log.warn("Brand not found for delete id={}", brandId);
        }
        return deleted;
    }
}
