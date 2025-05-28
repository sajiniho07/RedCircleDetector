package org.example.redcircledetector.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.redcircledetector.service.ImageProcessingService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/image")
public class ImageProcessingController {

    private final ImageProcessingService imageProcessingService;

    @Operation(summary = "Detect red objects and draw circles around them", description = "Receives an image, detects red objects using OpenCV, draws circles around them, and saves the result.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image processed and saved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Invalid image file", content = @Content)
    })
    @PostMapping(value = "/process", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> processImage(@RequestParam("file") MultipartFile file) {
        try {
            imageProcessingService.processRedCircles(file);
            return ResponseEntity.ok("Image processed and saved successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing image: " + e.getMessage());
        }
    }
}