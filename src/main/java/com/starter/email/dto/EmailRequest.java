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
    description = "Email sending request with support for both plain text and HTML formats",
    example = """
        {
          "to": "user@example.com",
          "subject": "Welcome to our platform",
          "message": "Thank you for joining us!",
          "html": false
        }
        """
)
public class EmailRequest {
    
    @NotBlank(message = "Recipient email is required")
    @Email(message = "Invalid email format")
    @Schema(
        description = "Recipient email address",
        example = "user@example.com",
        required = true,
        format = "email"
    )
    private String to;
    
    @NotBlank(message = "Subject is required")
    @Size(max = 200, message = "Subject must not exceed 200 characters")
    @Schema(
        description = "Email subject line",
        example = "Welcome to our platform",
        required = true,
        maxLength = 200
    )
    private String subject;
    
    @NotBlank(message = "Message content is required")
    @Size(max = 10000, message = "Message must not exceed 10000 characters")
    @Schema(
        description = "Email message content (plain text or HTML based on 'html' flag)",
        example = "Thank you for joining us! We're excited to have you on board.",
        required = true,
        maxLength = 10000
    )
    private String message;
    
    @Schema(
        description = "Send as HTML email. If true, the message will be rendered as HTML. If false, sent as plain text.",
        example = "false",
        defaultValue = "false"
    )
    private boolean html;
}
