#!/bin/bash
# Security Vulnerability Monitoring Script
# Run this weekly to check for available dependency updates

set -e

echo "=================================================="
echo "Security Vulnerability Check"
echo "Date: $(date '+%Y-%m-%d')"
echo "=================================================="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}1. Checking for Spring Boot 3.4.5 availability...${NC}"
if curl -s "https://repo1.maven.org/maven2/org/springframework/boot/spring-boot-starter-parent/3.4.5/spring-boot-starter-parent-3.4.5.pom" | grep -q "404"; then
    echo -e "${RED}   ✗ Spring Boot 3.4.5 not yet available${NC}"
else
    echo -e "${GREEN}   ✓ Spring Boot 3.4.5 is available! Update pom.xml${NC}"
fi
echo ""

echo -e "${YELLOW}2. Checking for MinIO 8.6.0 availability...${NC}"
if curl -s "https://repo1.maven.org/maven2/io/minio/minio/8.6.0/minio-8.6.0.pom" | grep -q "404"; then
    echo -e "${RED}   ✗ MinIO 8.6.0 not yet available${NC}"
else
    echo -e "${GREEN}   ✓ MinIO 8.6.0 is available! Update pom.xml${NC}"
fi
echo ""

echo -e "${YELLOW}3. Running Trivy security scan...${NC}"
echo ""
if command -v trivy &> /dev/null; then
    trivy fs . --severity HIGH,CRITICAL --format table
else
    echo -e "${RED}   ✗ Trivy not installed. Install with: brew install trivy${NC}"
fi
echo ""

echo -e "${YELLOW}4. Checking Maven dependencies for updates...${NC}"
mvn versions:display-dependency-updates | grep -A 2 "org.springframework.boot\|io.minio\|org.postgresql\|commons-io" || echo "   No updates found"
echo ""

echo "=================================================="
echo "Next Steps:"
echo "1. If Spring Boot 3.4.5 is available:"
echo "   - Update version in pom.xml line 8"
echo "   - Remove CVE-2025-22235 from .trivyignore"
echo ""
echo "2. If MinIO 8.6.0 is available:"
echo "   - Update version in pom.xml line 171"
echo "   - Remove CVE-2025-59952 from .trivyignore"
echo ""
echo "3. After updates:"
echo "   - Run: mvn clean verify -DskipTests"
echo "   - Run: trivy fs . --severity HIGH,CRITICAL"
echo "   - Rebuild Docker images"
echo "=================================================="

