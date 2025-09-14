package com.messi.rbm.simulator.service.communications;

import com.messi.rbm.simulator.model.communications.Brand;
import com.messi.rbm.simulator.repo.communications.RbmMemoryRepository;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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

    public BrandService(RbmMemoryRepository repo) {
        this.repo = repo;
    }

    public Brand create(String displayName) {
        return repo.createBrand(displayName);
    }

    public Optional<Brand> get(String brandId) {
        return repo.getBrand(brandId);
    }

    public List<Brand> list() {
        return repo.listBrands();
    }

    public Optional<Brand> patch(String brandId, Map<String, Object> patch) {
        if (patch.containsKey("displayName")) {
            return repo.updateBrand(brandId, (String) patch.get("displayName"));
        }
        return repo.getBrand(brandId);
    }

    public boolean delete(String brandId) {
        return repo.deleteBrand(brandId);
    }
}
