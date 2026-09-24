package no.nav.testnav.apps.statusfrontend.fagsystem.pdl;

import java.util.List;

public record PdlOrderResponse(PersonOrders hovedperson, List<PersonOrders> relasjoner) {

    public record PersonOrders(List<Order> ordrer) {
    }

    public record Order(List<Event> hendelser) {
    }

    public record Event(String status) {
    }
}
