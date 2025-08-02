package com.sava.teachernet.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleSelectionDto {

  @NotBlank(message = "Role must be selected")
  private String role;
}
