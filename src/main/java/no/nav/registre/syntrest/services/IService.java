package no.nav.registre.syntrest.services;

import java.util.concurrent.CompletableFuture;

public interface IService {
    public Object getDataFromNAIS(Object request);
    public String isAlive();
}
