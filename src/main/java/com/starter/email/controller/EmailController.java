package com.starter.email.controller;

import com.starter.common.dto.ApiResponse;
import com.starter.email.dto.EmailRequest;
import com.starter.email.dto.WelcomeEmailRequest;
import com.starter.email.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/emails")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Email", description = "Email sending APIs")
public class EmailController {
    
    private final EmailService emailService;
    
    @PostMapping("/send")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Send custom email (Admin only)",
        description = """
            Sends a custom email to a recipient. Supports both plain text and HTML formats.
            
            **Features:**
            - Plain text or HTML email support
            - Asynchronous sending (non-blocking)
            - Email validation
            - Detailed logging
            
            **Requirements:**
            - Admin role required
            - Valid email configuration (MAIL_HOST, MAIL_USERNAME, MAIL_PASSWORD)
            
            **Example Request:**
            ```json
            {
              "to": "user@example.com",
              "subject": "Welcome to our platform",
              "message": "Thank you for joining us!",
              "html": false
            }
            ```
            """
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Email queued successfully for sending",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid request data (invalid email format, missing required fields)",
            content = @Content
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Authentication required",
            content = @Content
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Admin role required",
            content = @Content
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "Failed to send email (check email configuration and logs)",
            content = @Content
        )
    })
    public ResponseEntity<ApiResponse<Void>> sendEmail(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Email details including recipient, subject, message, and format",
            required = true,
            content = @Content(schema = @Schema(implementation = EmailRequest.class))
        )
        @Valid @RequestBody EmailRequest request) {
        log.info("Sending email to: {}", request.getTo());
        
        if (request.isHtml()) {
            emailService.sendHtmlEmail(request.getTo(), request.getSubject(), request.getMessage());
        } else {
            emailService.sendSimpleEmail(request.getTo(), request.getSubject(), request.getMessage());
        }
        
        return ResponseEntity.ok(ApiResponse.success(
            "Email is being sent to " + request.getTo(), 
            null
        ));
    }
    
    @PostMapping("/welcome")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Send welcome email (Admin only)",
        description = """
            Sends a pre-formatted HTML welcome email to a new user.
            
            **Features:**
            - Professional HTML template
            - Personalized with user's name
            - Asynchronous sending
            - Automatic formatting
            
            **Use Cases:**
            - New user registration
            - Account activation
            - Onboarding process
            
            **Example Request:**
            ```json
            {
              "to": "newuser@example.com",
              "userName": "John Doe"
            }
            ```
            
            **Email Template Preview:**
            - Greeting with user's name
            - Welcome message
            - Professional signature
            """
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Welcome email queued successfully for sending",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid request data (invalid email format, missing required fields)",
            content = @Content
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Authentication required",
            content = @Content
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Admin role required",
            content = @Content
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "Failed to send email (check email configuration and logs)",
            content = @Content
        )
    })
    public ResponseEntity<ApiResponse<Void>> sendWelcomeEmail(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Welcome email details including recipient email and user name for personalization",
            required = true,
            content = @Content(schema = @Schema(implementation = WelcomeEmailRequest.class))
        )
        @Valid @RequestBody WelcomeEmailRequest request) {
        log.info("Sending welcome email to: {}", request.getTo());
        
        emailService.sendWelcomeEmail(request.getTo(), request.getUserName());
        
        return ResponseEntity.ok(ApiResponse.success(
            "Welcome email is being sent to " + request.getTo(), 
            null
        ));
    }
    
    @PostMapping("/test")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Send test email (Admin only)",
        description = """
            Sends a test email to verify email configuration is working correctly.
            
            **Purpose:**
            - Verify SMTP configuration
            - Test email delivery
            - Validate credentials
            - Check network connectivity
            
            **What it does:**
            - Sends a simple test message
            - Uses configured SMTP settings
            - Logs delivery status
            - Returns immediate response (async sending)
            
            **Troubleshooting:**
            - Check application logs for detailed error messages
            - Verify MAIL_HOST, MAIL_PORT, MAIL_USERNAME, MAIL_PASSWORD
            - Ensure firewall allows SMTP traffic
            - For Gmail: Use App Password, not regular password
            
            **Example:**
            ```
            POST /api/v1/emails/test?email=test@example.com
            ```
            """
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Test email queued successfully - Check recipient inbox and application logs",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid email address format",
            content = @Content
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Authentication required",
            content = @Content
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Admin role required",
            content = @Content
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "Failed to send email - Check email configuration (MAIL_HOST, MAIL_USERNAME, MAIL_PASSWORD) and application logs",
            content = @Content
        )
    })
    public ResponseEntity<ApiResponse<Void>> sendTestEmail(
        @io.swagger.v3.oas.annotations.Parameter(
            name = "email",
            description = "Recipient email address for the test email",
            required = true,
            example = "test@example.com",
            schema = @Schema(type = "string", format = "email")
        )
        @RequestParam String email
    ) {
        log.info("Sending test email to: {}", email);
        
        emailService.sendSimpleEmail(
            email,
            "Test Email from Spring Boot Starter",
            "This is a test email to verify your email configuration is working correctly.\n\n" +
            "If you received this email, your email service is properly configured!\n\n" +
            "Best regards,\nSpring Boot Starter Team"
        );
        
        return ResponseEntity.ok(ApiResponse.success(
            "Test email is being sent to " + email, 
            null
        ));
    }
}
