//package no.nav.registre.testnorge.helsepersonellservice.provider;
//
//import no.nav.registre.testnorge.helsepersonellservice.service.HelsepersonellService;
//import no.nav.testnav.libs.dto.helsepersonell.v1.HelsepersonellListeDTO;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.CacheControl;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.concurrent.TimeUnit;
//
//
//@RestController
//@RequestMapping("/api/v1/helsepersonell")
//public class HelsepersonellController {
//
//    private final HelsepersonellService helsepersonellService;
//    private final Integer helsepersonellCacheHours;
//
//
//    public HelsepersonellController(
//            HelsepersonellService helsepersonellService,
//            @Value("${helsepersonell.controller.cache.hours}") Integer helsepersonellCacheHours
//    ) {
//        this.helsepersonellService = helsepersonellService;
//        this.helsepersonellCacheHours = helsepersonellCacheHours;
//    }
//
//    @GetMapping
//    public ResponseEntity<HelsepersonellListeDTO> getHelsepersonell() {
//        var cacheControl
//                = CacheControl.maxAge(helsepersonellCacheHours, TimeUnit.HOURS)
//                .noTransform()
//                .mustRevalidate();
//        var helsepersonell = helsepersonellService.getHelsepersonell();
//        return ResponseEntity
//                .ok()
//                .cacheControl(cacheControl)
//                .body(helsepersonell.toDTO());
//    }
//}
