package no.nav.testnav.apps.syntvedtakshistorikkservice.domain;

import no.nav.testnav.examples.reactiverestexample.controller.dto.CarDTO;
import no.nav.testnav.examples.reactiverestexample.repository.CarEntity;

public class Car {
    private final String regnumber;
    private final String color;
    private final String brand;

    public Car(String regnumber, String color, String brand) {
        this.regnumber = regnumber;
        this.color = color;
        this.brand = brand;
    }

    public Car(CarEntity entity) {
        this(entity.getRegnumber(), entity.getColor(), entity.getBrand());
    }

    public Car(CarDTO dto) {
        this(dto.regnumber(), dto.color(), dto.brand());
    }

    public CarDTO toDTO() {
        return new CarDTO(regnumber, color, brand);
    }

    public CarEntity toEntity(boolean isNew) {
        var entity = new CarEntity();
        entity.setBrand(brand);
        entity.setColor(color);
        entity.setRegnumber(regnumber);
        entity.setNew(isNew);
        return entity;
    }

    public String getRegistrationPlate() {
        return regnumber;
    }

    public String getColor() {
        return color;
    }

    public String getBrand() {
        return brand;
    }
}
