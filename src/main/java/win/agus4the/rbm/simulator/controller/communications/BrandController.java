package win.agus4the.rbm.simulator.controller.communications;

import win.agus4the.rbm.simulator.model.communications.Brand;
import win.agus4the.rbm.simulator.service.communications.BrandService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import java.util.Map;

/**
 * Controller for brand endpoints.
 */
@RestController
@RequestMapping("/v1/brands")
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Dependencies are injected and not exposed")
public class BrandController {

    private final BrandService brandService;
    private static final Logger log = LoggerFactory.getLogger(BrandController.class);

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @PostMapping
    public ResponseEntity<Brand> create(@Valid @RequestBody Brand req) {
        Brand b = brandService.create(req.getDisplayName());
        log.info("Brand created id={} name={}", b.getName(), b.getDisplayName());
        return ResponseEntity.ok(b);
    }

    @GetMapping
    public Map<String, Object> list() {
        List<Brand> brands = brandService.list();
        log.debug("Listing brands count={}", brands.size());
        return Map.of("brands", brands, "nextPageToken", "");
    }

    @GetMapping("/{brandId}")
    public ResponseEntity<Brand> get(@PathVariable String brandId) {
        return brandService.get(brandId)
                .map(brand -> {
                    log.info("Retrieved brand id={}", brandId);
                    return ResponseEntity.ok(brand);
                })
                .orElseGet(() -> {
                    log.warn("Brand not found id={}", brandId);
                    return ResponseEntity.notFound().build();
                });
    }

    @PatchMapping("/{brandId}")
    public ResponseEntity<Brand> patch(@PathVariable String brandId, @RequestBody Map<String, Object> patch) {
        return brandService.patch(brandId, patch)
                .map(brand -> {
                    log.info("Patched brand id={} patchKeys={}", brandId, patch.keySet());
                    return ResponseEntity.ok(brand);
                })
                .orElseGet(() -> {
                    log.warn("Brand not found for patch id={}", brandId);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{brandId}")
    public ResponseEntity<Void> delete(@PathVariable String brandId) {
        boolean deleted = brandService.delete(brandId);
        if (deleted) {
            log.info("Deleted brand id={}", brandId);
            return ResponseEntity.noContent().build();
        }
        log.warn("Brand not found for delete id={}", brandId);
        return ResponseEntity.notFound().build();
    }
}
