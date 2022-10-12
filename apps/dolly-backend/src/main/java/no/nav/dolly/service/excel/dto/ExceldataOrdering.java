package no.nav.dolly.service.excel.dto;

import java.util.List;

public record ExceldataOrdering(Long organisasjonId, List<Object[]> exceldata) {
}
