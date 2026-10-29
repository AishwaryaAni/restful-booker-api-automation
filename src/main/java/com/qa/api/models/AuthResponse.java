package com.qa.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Success returns {"token": "..."}; bad credentials return {"reason": "Bad credentials"}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AuthResponse(String token, String reason) { }
