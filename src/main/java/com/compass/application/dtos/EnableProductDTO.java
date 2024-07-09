package com.compass.application.dtos;

import com.compass.application.validations.ValidBoolean;

public record EnableProductDTO(@ValidBoolean Boolean enabled) {
}
