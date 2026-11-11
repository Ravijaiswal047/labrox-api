package com.labrox.sample.controller;

import com.labrox.common.dto.ApiResponse;
import com.labrox.sample.domain.SampleCollection;
import com.labrox.sample.domain.SampleRejectionReason;
import com.labrox.sample.dto.SampleCollectRequest;
import com.labrox.sample.dto.SampleReceiveRequest;
import com.labrox.sample.dto.SampleRejectRequest;
import com.labrox.sample.repository.SampleRejectionReasonRepository;
import com.labrox.sample.service.SampleCollectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/samples")
@RequiredArgsConstructor
@Tag(name = "Sample Logistics & Accessioning API", description = "Endpoints for phlebotomy, barcode generation, lab accessioning, and rejection")
public class SampleController {

    private final SampleCollectionService sampleService;
    private final SampleRejectionReasonRepository rejectionReasonRepository;

    @PostMapping("/collect")
    @Operation(summary = "Collect specimen and generate barcode")
    public ResponseEntity<ApiResponse<SampleCollection>> collectSample(@RequestBody SampleCollectRequest request) {
        SampleCollection sample = sampleService.collectSample(request);
        return ResponseEntity.ok(ApiResponse.ok("Specimen collected successfully", sample));
    }

    @PostMapping("/receive")
    @Operation(summary = "Accession/receive sample into laboratory")
    public ResponseEntity<ApiResponse<SampleCollection>> receiveSample(@RequestBody SampleReceiveRequest request) {
        return sampleService.receiveSample(request)
                .map(s -> ResponseEntity.ok(ApiResponse.ok("Specimen received in lab", s)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/reject")
    @Operation(summary = "Reject compromised sample (e.g. Hemolyzed, Clotted, QNS)")
    public ResponseEntity<ApiResponse<SampleCollection>> rejectSample(@RequestBody SampleRejectRequest request) {
        return sampleService.rejectSample(request)
                .map(s -> ResponseEntity.ok(ApiResponse.ok("Specimen marked as REJECTED", s)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{barcode}")
    @Operation(summary = "Lookup sample details by barcode")
    public ResponseEntity<ApiResponse<SampleCollection>> getSample(
            @PathVariable("barcode") String barcode,
            @RequestParam(defaultValue = "1") Integer companyId) {
        return sampleService.getByBarcode(companyId, barcode)
                .map(s -> ResponseEntity.ok(ApiResponse.ok(s)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pending-accession")
    @Operation(summary = "List samples collected but not yet accessioned in lab")
    public ResponseEntity<ApiResponse<List<SampleCollection>>> getPendingAccession(
            @RequestParam(defaultValue = "1") Integer companyId) {
        return ResponseEntity.ok(ApiResponse.ok(sampleService.getPendingAccession(companyId)));
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "List all sample containers for an order")
    public ResponseEntity<ApiResponse<List<SampleCollection>>> getOrderSamples(
            @PathVariable("orderId") String orderId,
            @RequestParam(defaultValue = "1") Integer companyId) {
        return ResponseEntity.ok(ApiResponse.ok(sampleService.getByOrderId(companyId, orderId)));
    }

    @GetMapping("/rejection-reasons")
    @Operation(summary = "Get active rejection reasons list")
    public ResponseEntity<ApiResponse<List<SampleRejectionReason>>> getRejectionReasons() {
        return ResponseEntity.ok(ApiResponse.ok(rejectionReasonRepository.findByActiveTrue()));
    }
}
