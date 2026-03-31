package com.ciphertext.opencarebackend.mapper;
import com.ciphertext.opencarebackend.modules.provider.dto.request.MedicalTestRequest;
import com.ciphertext.opencarebackend.modules.provider.dto.response.MedicalTestResponse;
import com.ciphertext.opencarebackend.entity.MedicalTest;
import org.springframework.stereotype.Component;

@Component
public class MedicalTestMapper {
    public MedicalTestResponse toResponse(MedicalTest medicalTest) {
        if (medicalTest == null) {
            return null;
        }

        MedicalTestResponse response = new MedicalTestResponse();
        response.setId(medicalTest.getId());
        response.setParentId(medicalTest.getParentId());
        response.setName(medicalTest.getName());
        response.setBnName(medicalTest.getBnName());
        response.setAlternativeNames(medicalTest.getAlternativeNames());
        response.setDescription(medicalTest.getDescription());
        response.setHospitalCount(medicalTest.getHospitalCount());
        return response;
    }

    public MedicalTest toEntity(MedicalTestRequest request) {
        if (request == null) {
            return null;
        }

        MedicalTest entity = new MedicalTest();
        entity.setParentId(request.getParentId());
        entity.setName(request.getName());
        entity.setBnName(request.getBnName());
        entity.setAlternativeNames(request.getAlternativeNames());
        entity.setDescription(request.getDescription());
        return entity;
    }

    public void partialUpdate(MedicalTestRequest request, MedicalTest medicalTest) {
        if (request == null || medicalTest == null) {
            return;
        }

        if (request.getParentId() != null) {
            medicalTest.setParentId(request.getParentId());
        }
        if (request.getName() != null) {
            medicalTest.setName(request.getName());
        }
        if (request.getBnName() != null) {
            medicalTest.setBnName(request.getBnName());
        }
        if (request.getAlternativeNames() != null) {
            medicalTest.setAlternativeNames(request.getAlternativeNames());
        }
        if (request.getDescription() != null) {
            medicalTest.setDescription(request.getDescription());
        }
    }
}
