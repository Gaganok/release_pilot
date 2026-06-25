package org.task.pilot.rest.dto;

import jakarta.validation.constraints.Positive;

public record PageRequest(
    @Positive(message = "Page index must be greater than 0")
    int pageIndex,

    @Positive(message = "Page size must be greater than 0")
    int pageSize
) {
}
