package com.labrox.sample.service;

import com.labrox.sample.domain.SampleCollection;
import com.labrox.sample.dto.SampleCollectRequest;
import com.labrox.sample.dto.SampleReceiveRequest;
import com.labrox.sample.dto.SampleRejectRequest;
import com.labrox.sample.repository.SampleCollectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SampleCollectionService {

    private final SampleCollectionRepository sampleRepository;
    private final BarcodeGeneratorService barcodeGenerator;

    @Transactional
    public SampleCollection collectSample(SampleCollectRequest request) {
        String barcode = barcodeGenerator.generateBarcode();

        SampleCollection sample = SampleCollection.builder()
                .companyId(request.getCompanyId() != null ? request.getCompanyId() : 1)
                .barcodeNo(barcode)
                .orderId(request.getOrderId())
                .patientId(request.getPatientId())
                .patientName(request.getPatientName())
                .sampleTypeId(request.getSampleTypeId())
                .sampleTypeName(request.getSampleTypeName())
                .tubeColor(request.getTubeColor() != null ? request.getTubeColor() : "Purple/EDTA")
                .status("COLLECTED")
                .collectionTime(Instant.now())
                .collectedBy(request.getCollectorId())
                .build();

        SampleCollection saved = sampleRepository.save(sample);
        log.info("Specimen collected successfully: Barcode={}, Order={}, Tube={}", barcode, request.getOrderId(), sample.getTubeColor());
        return saved;
    }

    @Transactional
    public Optional<SampleCollection> receiveSample(SampleReceiveRequest request) {
        Optional<SampleCollection> sampleOpt = sampleRepository.findByCompanyIdAndBarcodeNo(
                request.getCompanyId() != null ? request.getCompanyId() : 1,
                request.getBarcodeNo()
        );

        if (sampleOpt.isEmpty()) {
            return Optional.empty();
        }

        SampleCollection sample = sampleOpt.get();
        sample.setStatus("RECEIVED");
        sample.setReceivedTime(Instant.now());
        sample.setReceivedBy(request.getReceiverId());

        SampleCollection updated = sampleRepository.save(sample);
        log.info("Specimen accessioned/received in lab: Barcode={}, ReceiverId={}", sample.getBarcodeNo(), request.getReceiverId());
        return Optional.of(updated);
    }

    @Transactional
    public Optional<SampleCollection> rejectSample(SampleRejectRequest request) {
        Optional<SampleCollection> sampleOpt = sampleRepository.findByCompanyIdAndBarcodeNo(
                request.getCompanyId() != null ? request.getCompanyId() : 1,
                request.getBarcodeNo()
        );

        if (sampleOpt.isEmpty()) {
            return Optional.empty();
        }

        SampleCollection sample = sampleOpt.get();
        sample.setStatus("REJECTED");
        sample.setRejectionReasonId(request.getReasonId());
        sample.setRejectionRemarks(request.getRemarks());

        SampleCollection updated = sampleRepository.save(sample);
        log.warn("Specimen REJECTED: Barcode={}, ReasonId={}, Remarks={}", sample.getBarcodeNo(), request.getReasonId(), request.getRemarks());
        return Optional.of(updated);
    }

    public Optional<SampleCollection> getByBarcode(Integer companyId, String barcodeNo) {
        return sampleRepository.findByCompanyIdAndBarcodeNo(companyId != null ? companyId : 1, barcodeNo);
    }

    public List<SampleCollection> getByOrderId(Integer companyId, String orderId) {
        return sampleRepository.findByCompanyIdAndOrderId(companyId != null ? companyId : 1, orderId);
    }

    public List<SampleCollection> getPendingAccession(Integer companyId) {
        return sampleRepository.findByCompanyIdAndStatus(companyId != null ? companyId : 1, "COLLECTED");
    }
}
