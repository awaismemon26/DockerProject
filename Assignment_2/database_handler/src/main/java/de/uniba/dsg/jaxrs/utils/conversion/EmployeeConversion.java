package de.uniba.dsg.jaxrs.utils.conversion;

import de.uniba.dsg.jaxrs.dto.*;
import de.uniba.dsg.jaxrs.model.*;

import static de.uniba.dsg.jaxrs.utils.conversion.Role.Employee;

public class EmployeeConversion{

    public static BottleDto toDto(Bottle bottle) {
        return ConversionHelper.toDto(bottle, Employee);
    }

    public static CrateDto toDto(Crate crate) {
        return ConversionHelper.toDto(crate, Employee);
    }

    public static Bottle toModel(BottleDto bottleDto) {
        return ConversionHelper.toModel(bottleDto);
    }

    public static Crate toModel(CrateDto crateDto) {
        return ConversionHelper.toModel(crateDto);
    }
}
