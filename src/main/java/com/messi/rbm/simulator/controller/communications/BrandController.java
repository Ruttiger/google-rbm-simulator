package com.messi.rbm.simulator.controller.communications;

import com.messi.rbm.simulator.model.communications.Brand;
import com.messi.rbm.simulator.service.communications.BrandService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller for brand endpoints.
 */
@RestController
@RequestMapping("/v1/brands")
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Dependencies are injected and not exposed")
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @PostMapping
    public ResponseEntity<Brand> create(@Valid @RequestBody Brand req) {
        Brand b = brandService.create(req.getDisplayName());
        return ResponseEntity.ok(b);
    }

    @GetMapping
    public Map<String, Object> list() {
        return Map.of("brands", brandService.list(), "nextPageToken", "");
    }

    @GetMapping("/{brandId}")
    public ResponseEntity<Brand> get(@PathVariable String brandId) {
        return brandService.get(brandId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{brandId}")
    public ResponseEntity<Brand> patch(@PathVariable String brandId, @RequestBody Map<String, Object> patch) {
        return brandService.patch(brandId, patch).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{brandId}")
    public ResponseEntity<Void> delete(@PathVariable String brandId) {
        return brandService.delete(brandId) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
