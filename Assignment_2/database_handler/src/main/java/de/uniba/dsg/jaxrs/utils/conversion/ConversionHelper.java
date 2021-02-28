package de.uniba.dsg.jaxrs.utils.conversion;

import de.uniba.dsg.jaxrs.Configuration;
import de.uniba.dsg.jaxrs.dto.*;
import de.uniba.dsg.jaxrs.model.*;

import static de.uniba.dsg.jaxrs.utils.conversion.Role.*;

enum Role {
    Customer,
    Employee
}

class ConversionHelper {
    private static final String baseUrl = Configuration.loadProperties().getProperty("serverUri");

    public static BottleDto toDto(Bottle bottle, Role role) {
        BottleDto bottleDto = new BottleDto();

        bottleDto.setId(bottle.getId());
        bottleDto.setAlcoholic(bottle.isAlcoholic());
        bottleDto.setInStock(bottle.getInStock());
        bottleDto.setName(bottle.getName());
        bottleDto.setPrice(bottle.getPrice());
        bottleDto.setSupplier(bottle.getSupplier());
        bottleDto.setVolume(bottle.getVolume());
        bottleDto.setVolumePercent(bottle.getVolumePercent());

        if(role == Customer) {
            bottleDto.setHref(baseUrl + "/customer/bottles/" + bottleDto.getId());
        } else {
            bottleDto.setHref(baseUrl + "/employee/bottles/" + bottleDto.getId());
        }

        return bottleDto;
    }

    public static CrateDto toDto(Crate crate, Role role) {
        CrateDto crateDto = new CrateDto();

        crateDto.setId(crate.getId());
        crateDto.setInStock(crate.getInStock());
        crateDto.setNoOfBottles(crate.getNoOfBottles());
        crateDto.setPrice(crate.getPrice());
        crateDto.setBottle(ConversionHelper.toDto(crate.getBottle(), role));

        if(role == Customer) {
            crateDto.setHref(baseUrl + "/customer/crates/" + crateDto.getId());
        } else {
            crateDto.setHref(baseUrl + "/employee/crates/" + crateDto.getId());
        }

        return crateDto;
    }

    public static Bottle toModel(BottleDto bottleDto) {
        Bottle bottle = new Bottle();

        bottle.setId(bottleDto.getId());
        bottle.setAlcoholic(bottleDto.isAlcoholic());
        bottle.setInStock(bottleDto.getInStock());
        bottle.setName(bottleDto.getName());
        bottle.setPrice(bottleDto.getPrice());
        bottle.setSupplier(bottleDto.getSupplier());
        bottle.setVolume(bottleDto.getVolume());
        bottle.setVolumePercent(bottleDto.getVolumePercent());

        return bottle;
    }

    public static Crate toModel(CrateDto crateDto) {
        Crate crate = new Crate();

        crate.setId(crateDto.getId());
        crate.setInStock(crateDto.getInStock());
        crate.setNoOfBottles(crateDto.getNoOfBottles());
        crate.setPrice(crateDto.getPrice());
        crate.setBottle(ConversionHelper.toModel(crateDto.getBottle()));

        return crate;
    }
}
