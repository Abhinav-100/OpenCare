package com.ciphertext.opencarebackend.modules.provider.dto.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "hospitals")
/**
 * Flow note: HospitalDocument belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public class HospitalDocument {
    @Id
    private Integer id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String name;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String bnName;

    @Field(type = FieldType.Integer)
    private Integer numberOfBed;

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

    @Field(type = FieldType.Keyword)
    private String hospitalType;

    @Field(type = FieldType.Keyword)
    private String organizationType;

    @GeoPointField
    private GeoPoint location;

    @Field(type = FieldType.Text)
    private String websiteUrl;

    @Field(type = FieldType.Text)
    private String imageUrl;
}