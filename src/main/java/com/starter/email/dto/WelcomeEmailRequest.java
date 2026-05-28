package com.starter.email.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    description = "Welcome email request with pre-formatted HTML template",
    example = """
        {
          "to": "newuser@example.com",
          "userName": "John Doe"
        }
        """
)
public class WelcomeEmailRequest {
    
    @NotBlank(message = "Recipient email is required")
    @Email(message = "Invalid email format")
    @Schema(
        description = "Recipient email address for the welcome email",
        example = "newuser@example.com",
        required = true,
        format = "email"
    )
    private String to;
    
    @NotBlank(message = "User name is required")
    @Size(min = 1, max = 100, message = "User name must be between 1 and 100 characters")
    @Schema(
        description = "User's full name for personalization in the welcome email",
        example = "John Doe",
        required = true,
        minLength = 1,
        maxLength = 100
    )
    private String userName;
}
