package no.nav.registre.syntrest.services;

public interface IService {
    Object getDataFromNAIS(Object request);
    String isAlive();
}
