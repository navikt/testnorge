package no.nav.testnav.libs.reporting;

@FunctionalInterface
public interface Reportable {
    void run(Reporting reporting);
}