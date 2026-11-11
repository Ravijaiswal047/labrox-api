package com.labrox.diagnostic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveResultEntryRequest {
    private Integer companyId;
    private Long testId;
    private Integer technicianId;
    private List<ObservationEntryDto> observations;
}
