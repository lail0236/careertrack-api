
package com.careertrack.api.dto;

public record LoginResponse(
    String accessToken,
    String tokenType,
    long expiresIn
) {
}
