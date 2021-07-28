package no.nav.registre.syntrest.kubernetes.exception;

public class KubernetesException extends RuntimeException {
    public KubernetesException(String message) {
        super(message);
    }
}