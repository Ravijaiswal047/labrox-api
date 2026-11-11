package com.labrox.catalog.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
public class FormulaEvaluatorService {

    /**
     * Evaluates standard clinical formulas:
     * - Indirect Bilirubin: [TotalBilirubin] - [DirectBilirubin]
     * - Globulin: [TotalProtein] - [Albumin]
     * - A/G Ratio: [Albumin] / [Globulin]
     * - Anion Gap: [Sodium] - ([Chloride] + [Bicarbonate])
     */
    public BigDecimal evaluateFormula(String formulaType, Map<String, BigDecimal> inputs) {
        if (formulaType == null) return null;

        switch (formulaType.toUpperCase()) {
            case "INDIRECT_BILIRUBIN":
                BigDecimal totalBili = inputs.get("TotalBilirubin");
                BigDecimal directBili = inputs.get("DirectBilirubin");
                if (totalBili != null && directBili != null) {
                    return totalBili.subtract(directBili).setScale(2, RoundingMode.HALF_UP);
                }
                break;

            case "GLOBULIN":
                BigDecimal totalProt = inputs.get("TotalProtein");
                BigDecimal albumin = inputs.get("Albumin");
                if (totalProt != null && albumin != null) {
                    return totalProt.subtract(albumin).setScale(2, RoundingMode.HALF_UP);
                }
                break;

            case "AG_RATIO":
                BigDecimal alb = inputs.get("Albumin");
                BigDecimal glob = inputs.get("Globulin");
                if (alb != null && glob != null && glob.compareTo(BigDecimal.ZERO) > 0) {
                    return alb.divide(glob, 2, RoundingMode.HALF_UP);
                }
                break;

            case "ANION_GAP":
                BigDecimal na = inputs.get("Sodium");
                BigDecimal cl = inputs.get("Chloride");
                BigDecimal hco3 = inputs.get("Bicarbonate");
                if (na != null && cl != null && hco3 != null) {
                    return na.subtract(cl.add(hco3)).setScale(1, RoundingMode.HALF_UP);
                }
                break;
        }

        return null;
    }
}
