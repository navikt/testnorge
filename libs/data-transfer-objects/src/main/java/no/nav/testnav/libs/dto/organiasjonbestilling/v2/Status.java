package no.nav.testnav.libs.dto.organiasjonbestilling.v2;


public enum Status {
    NOT_FOUND,
    ADDING_TO_QUEUE,
    IN_QUEUE_WAITING_TO_START,
    RUNNING,
    COMPLETED,
    ERROR,
    FAILED;

    public static Status from(Long kode) {
        if (kode < 0) {
            return Status.RUNNING;
        }
        if (kode == 0) {
            return Status.COMPLETED;
        }
        if (kode < 16) {
            return Status.ERROR;
        }
        return FAILED;
    }
}