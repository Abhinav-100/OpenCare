package com.ciphertext.opencarebackend.modules.provider.dto.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "doctors")
/**
 * Flow note: DoctorDocument belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public class DoctorDocument {
    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String name;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String bnName;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String bmdcNo;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String degrees;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String specializations;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;

    @Field(type = FieldType.Integer)
    private Integer districtId;

    @Field(type = FieldType.Text)
    private String districtName;

    @Field(type = FieldType.Integer)
    private Integer upazilaId;

    @Field(type = FieldType.Text)
    private String upazilaName;

    @Field(type = FieldType.Integer)
    private Integer unionId;

    @Field(type = FieldType.Text)
    private String unionName;

    @Field(type = FieldType.Boolean)
    private Boolean isVerified;

    @Field(type = FieldType.Boolean)
    private Boolean isActive;

    @Field(type = FieldType.Text)
    private String imageUrl;
}