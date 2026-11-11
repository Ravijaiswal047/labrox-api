package com.labrox.iam.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SwitchCentreRequest {
    @NotNull(message = "Centre ID is required")
    private Integer centreId;
}

