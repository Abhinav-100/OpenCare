package com.ciphertext.opencarebackend.modules.content.service;
import com.ciphertext.opencarebackend.modules.shared.dto.home.BlogPost;
import com.ciphertext.opencarebackend.modules.shared.dto.home.FeaturedData;
import com.ciphertext.opencarebackend.modules.clinical.dto.home.HealthTip;
import com.ciphertext.opencarebackend.modules.shared.dto.home.Testimonial;
import com.ciphertext.opencarebackend.modules.provider.dto.response.MedicalSpecialityResponse;
import java.util.List;
import java.util.Map;

public interface HomeService {
    Map<String, Object> getHomeData();
    FeaturedData getFeaturedData();
    List<BlogPost> getBlogPosts();
    List<MedicalSpecialityResponse> getPopularSpecialties(int limit);
    List<Testimonial> getTestimonials();
    List<HealthTip> getHealthTips();
}
