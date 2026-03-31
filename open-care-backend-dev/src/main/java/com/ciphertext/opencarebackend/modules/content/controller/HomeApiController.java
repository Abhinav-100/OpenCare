package com.ciphertext.opencarebackend.modules.content.controller;

import com.ciphertext.opencarebackend.modules.clinical.dto.home.HealthTip;
import com.ciphertext.opencarebackend.modules.content.service.HomeService;
import com.ciphertext.opencarebackend.modules.provider.dto.response.MedicalSpecialityResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.home.BlogPost;
import com.ciphertext.opencarebackend.modules.shared.dto.home.FeaturedData;
import com.ciphertext.opencarebackend.modules.shared.dto.home.Testimonial;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;





@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
@Tag(name = "Home", description = "Public endpoints for home page data such as featured items, blogs, specialties and tips")
public class HomeApiController {
    private final HomeService homeService;

    @Operation(summary = "Get aggregated home data", description = "Retrieves aggregated data for the home page used by the frontend")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getHomeData() {
        return ResponseEntity.ok(homeService.getHomeData());
    }

    @Operation(summary = "Get featured data", description = "Retrieves featured items for the home page")
    @GetMapping("/featured")
    public ResponseEntity<FeaturedData> getFeaturedDataEndpoint() {
        return ResponseEntity.ok(homeService.getFeaturedData());
    }

    @Operation(summary = "Get blogs", description = "Retrieves blog posts for the home page")
    @GetMapping("/blogs")
    public ResponseEntity<List<BlogPost>> getBlogPostsEndpoint() {
        return ResponseEntity.ok(homeService.getBlogPosts());
    }

    @Operation(summary = "Get popular specialties", description = "Retrieves the most popular medical specialties for display on the home page")
    @GetMapping("/specialties")
    public ResponseEntity<List<MedicalSpecialityResponse>> getSpecialtiesEndpoint() {
        return ResponseEntity.ok(homeService.getPopularSpecialties(10));
    }

    @Operation(summary = "Get testimonials", description = "Retrieves testimonials to display on the home page")
    @GetMapping("/testimonials")
    public ResponseEntity<List<Testimonial>> getTestimonials() {
        return ResponseEntity.ok(homeService.getTestimonials());
    }

    @Operation(summary = "Get health tips", description = "Retrieves health tips to display on the home page")
    @GetMapping("/health-tips")
    public ResponseEntity<List<HealthTip>> getHealthTips() {
        return ResponseEntity.ok(homeService.getHealthTips());
    }
}