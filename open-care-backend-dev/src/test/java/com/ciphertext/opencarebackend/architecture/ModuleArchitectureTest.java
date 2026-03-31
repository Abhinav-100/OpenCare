package com.ciphertext.opencarebackend.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tngtech.archunit.core.domain.Dependency;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class ModuleArchitectureTest {

    private static final String BASE_PACKAGE = "com.ciphertext.opencarebackend";
    private static final Pattern MODULE_PATTERN =
            Pattern.compile(".*\\.modules\\.([^.]+)\\..*");

    private static JavaClasses importedClasses() {
        return new ClassFileImporter().importPackages(BASE_PACKAGE);
    }

    @Test
    void noLegacyFlatLayerPackages() {
        JavaClasses classes = importedClasses();
        List<String> legacyClasses =
            classes.stream()
                .map(JavaClass::getPackageName)
                .filter(
                    packageName ->
                        packageName.startsWith(
                                "com.ciphertext.opencarebackend.controller")
                            || packageName.startsWith(
                                "com.ciphertext.opencarebackend.service")
                            || packageName.startsWith(
                                "com.ciphertext.opencarebackend.repository")
                            || packageName.startsWith(
                                "com.ciphertext.opencarebackend.dto"))
                .toList();

        assertTrue(
            legacyClasses.isEmpty(),
            "Legacy flat controller/service/repository/dto packages must be empty: "
                + legacyClasses);
    }

    @Test
    void controllersMustNotDependOnRepositories() {
        ArchRule noControllerToRepository =
                noClasses()
                        .that()
                        .resideInAPackage("..modules..controller..")
                        .should()
                        .dependOnClassesThat()
                        .resideInAPackage("..modules..repository..");

        noControllerToRepository.check(importedClasses());
    }

    @Test
    void servicesCanOnlyUseRepositoriesFromSameModule() {
        JavaClasses classes = importedClasses();
        List<String> violations = new ArrayList<>();

        for (JavaClass origin : classes) {
            if (!origin.getPackageName().contains(".modules.")
                    || !origin.getPackageName().contains(".service.")) {
                continue;
            }

            String originModule = moduleName(origin.getPackageName());
            if (originModule == null) {
                continue;
            }

            for (Dependency dependency : origin.getDirectDependenciesFromSelf()) {
                JavaClass target = dependency.getTargetClass();
                String targetPackage = target.getPackageName();
                if (!targetPackage.contains(".modules.") || !targetPackage.contains(".repository.")) {
                    continue;
                }

                if (targetPackage.contains(".modules.shared.repository.specification.")) {
                    continue;
                }

                if (!target.getSimpleName().endsWith("Repository")) {
                    continue;
                }

                String targetModule = moduleName(targetPackage);
                if (targetModule == null) {
                    continue;
                }

                if (!originModule.equals(targetModule)) {
                    violations.add(
                            origin.getFullName()
                                    + " -> "
                                    + target.getFullName()
                                    + " ("
                                    + originModule
                                    + " -> "
                                    + targetModule
                                    + ")");
                }
            }
        }

        assertEquals(
                List.of(),
                violations,
                "Cross-module repository access is forbidden; services must use repositories from their own module only.");
    }

    private static String moduleName(String packageName) {
        Matcher matcher = MODULE_PATTERN.matcher(packageName);
        return matcher.matches() ? matcher.group(1) : null;
    }
}
