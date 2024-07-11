package com.compass.application.dtos;

import com.compass.application.validations.ValidBoolean;

public record EnableProductDTO(

        @ValidBoolean(message = "Product availability must be specified (true or false)")
        Boolean enabled
) {
}
