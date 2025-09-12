package com.messi.rbm.simulator.controller;

import com.messi.rbm.simulator.model.Brand;
import com.messi.rbm.simulator.service.BrandService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for brand endpoints.
 */
@RestController
@RequestMapping("/v1/brands")
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
