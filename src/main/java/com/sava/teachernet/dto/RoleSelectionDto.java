package com.sava.teachernet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RoleSelectionDto {

  @NotBlank(message = "Role must be selected")
  @Pattern(regexp = "ROLE_STUDENT|ROLE_TEACHER", message = "Invalid role selected")
  private String role;
}
