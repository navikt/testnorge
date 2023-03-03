package no.nav.registre.testnav.dollyfrontendredirect.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping("/**")
@RequiredArgsConstructor
public class RedirectController {

    @GetMapping
    public void method(HttpServletResponse httpServletResponse) {
        var location = "https://dolly.ekstern.dev.nav.no/";
        log.info("Redirecter til {}.", location);
        httpServletResponse.setHeader(HttpHeaders.LOCATION, location);
        httpServletResponse.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
    }
}