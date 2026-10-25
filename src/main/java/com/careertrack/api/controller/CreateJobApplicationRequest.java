
package com.careertrack.api.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateJobApplicationRequest(
        @NotBlank
        @Size(max = 150)
        String company,

        @NotBlank
        @Size(max = 150)
        String position,

        @Size(max = 100)
        String location,

        @Size(max = 2048)
        String jobUrl,

        LocalDate dateApplied,

        @Size(max = 2000)
        String notes
) {}
