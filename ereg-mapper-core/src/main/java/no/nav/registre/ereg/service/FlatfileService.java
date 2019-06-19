package no.nav.registre.ereg.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import no.nav.registre.ereg.mapper.EregMapper;
import no.nav.registre.ereg.provider.rs.request.EregDataRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlatfileService {

    private final EregMapper mapper;

    public String mapEreg(List<EregDataRequest> data) {

        return mapper.mapEregFromRequests(data);
    }

}
