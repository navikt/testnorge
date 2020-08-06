package no.nav.registre.testnorge.libs.reporting;

@FunctionalInterface
public interface Reportable {
    void run(Reporting reporting);
}