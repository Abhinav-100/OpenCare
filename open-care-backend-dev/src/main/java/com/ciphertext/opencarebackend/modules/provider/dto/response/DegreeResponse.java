package com.ciphertext.opencarebackend.modules.provider.dto.response;
import com.ciphertext.opencarebackend.modules.provider.dto.response.enums.DegreeTypeResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DegreeResponse {
    private Integer id;
    private String name;
    private String abbreviation;
    private DegreeTypeResponse degreeType;
}
