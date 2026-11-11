package com.labrox.catalog.service;

import com.labrox.catalog.domain.Doctor;
import com.labrox.catalog.domain.Investigation;
import com.labrox.catalog.domain.RateListItem;
import com.labrox.catalog.repository.DoctorRepository;
import com.labrox.catalog.repository.InvestigationRepository;
import com.labrox.catalog.repository.RateListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CatalogService {

    private final InvestigationRepository investigationRepository;
    private final RateListRepository rateListRepository;
    private final DoctorRepository doctorRepository;

    public List<Investigation> searchInvestigations(Integer companyId, String query) {
        if (query == null || query.isBlank()) {
            return investigationRepository.findByCompanyIdAndActiveTrue(companyId);
        }
        return investigationRepository.findByCompanyIdAndTestNameContainingIgnoreCaseAndActiveTrue(companyId, query.trim());
    }

    public Optional<Investigation> getInvestigationByCode(Integer companyId, String testCode) {
        return investigationRepository.findByCompanyIdAndTestCodeAndActiveTrue(companyId, testCode);
    }

    public List<Doctor> searchDoctors(Integer companyId, String query) {
        if (query == null || query.isBlank()) {
            return doctorRepository.findByCompanyIdAndActiveTrue(companyId);
        }
        return doctorRepository.findByCompanyIdAndDoctorNameContainingIgnoreCaseAndActiveTrue(companyId, query.trim());
    }

    public Optional<BigDecimal> getInvestigationTariff(Integer companyId, Integer rateTypeId, Integer investigationId) {
        return rateListRepository.findByCompanyIdAndRateTypeIdAndInvestigationIdAndActiveTrue(companyId, rateTypeId, investigationId)
                .map(RateListItem::getRate);
    }
}
