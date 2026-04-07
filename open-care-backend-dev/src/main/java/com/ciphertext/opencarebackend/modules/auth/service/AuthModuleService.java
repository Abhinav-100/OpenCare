package com.ciphertext.opencarebackend.modules.auth.service;

import com.ciphertext.opencarebackend.modules.auth.dto.ModuleAuthResponse;
import com.ciphertext.opencarebackend.modules.auth.dto.ModuleLoginRequest;
import com.ciphertext.opencarebackend.modules.auth.dto.ModuleRegisterRequest;
import com.ciphertext.opencarebackend.modules.shared.dto.ModuleOverviewResponse;

/**
 * Flow note: AuthModuleService belongs to the authentication module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public interface AuthModuleService {

    ModuleAuthResponse register(ModuleRegisterRequest request);

    ModuleAuthResponse login(ModuleLoginRequest request);

    ModuleOverviewResponse getOverview();
}