
package com.careertrack.api.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateJobApplicationRequest(

        @NotBlank(message = "Company is required.")
        @Size(max = 150, message = "Company must not exceed 150 characters.")
        String company,

        @NotBlank(message = "Position is required.")
        @Size(max = 150, message = "Position must not exceed 150 characters.")
        String position,

        @Size(max = 100, message = "Location must not exceed 100 characters.")
        String location,

        @Size(max = 2048, message = "Job URL must not exceed 2048 characters.")
        String jobUrl,

        LocalDate dateApplied,

        @Size(max = 2000, message = "Notes must not exceed 2000 characters.")
        String notes

) {}
