package com.vendeskgt.vendeskgt;

import com.vendeskgt.vendeskgt.domain.entity.TenantEntity;
import com.vendeskgt.vendeskgt.repository.TenantRepository;
import com.vendeskgt.vendeskgt.service.util.SchemaNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SchemaNameGeneratorTest {

    @Mock
    private TenantRepository repository;

    @InjectMocks
    private SchemaNameGenerator schemaNameGenerator;

    private TenantEntity createTenantEntity(String schemaName) {
        return TenantEntity.builder()
                .tenantId(UUID.randomUUID())
                .name("Test")
                .lastName("User")
                .schemaName(schemaName)
                .contactEmail("test@example.com")
                .isActive(true)
                .build();
    }

    @Test
    void testGenerateUniqueSchemaName_WhenNoExistingTenants_ShouldReturnBaseName() {
        // Arrange
        String originalName = "TiendaJhire";
        String expectedBaseName = "tiendajhire";
        when(repository.findBySchemaNameStartingWithIgnoreCase(expectedBaseName))
                .thenReturn(Collections.emptyList());

        // Act
        String result = schemaNameGenerator.generateUniqueSchemaName(originalName);

        // Assert
        assertEquals(expectedBaseName, result);
    }

    @Test
    void testGenerateUniqueSchemaName_WhenBaseNameExists_ShouldReturnWithSuffix1() {
        // Arrange
        String originalName = "TiendaJhire";
        String baseName = "tiendajhire";

        List<TenantEntity> existingTenants = Collections.singletonList(
                createTenantEntity("tiendajhire")
        );

        when(repository.findBySchemaNameStartingWithIgnoreCase(baseName))
                .thenReturn(existingTenants);

        // Act
        String result = schemaNameGenerator.generateUniqueSchemaName(originalName);

        // Assert
        assertEquals("tiendajhire_1", result);
    }

    @Test
    void testGenerateUniqueSchemaName_WhenMultipleDuplicatesExist_ShouldReturnNextAvailable() {
        // Arrange
        String originalName = "TiendaJhire";
        String baseName = "tiendajhire";

        List<TenantEntity> existingTenants = Arrays.asList(
                createTenantEntity("tiendajhire"),
                createTenantEntity("tiendajhire_1"),
                createTenantEntity("tiendajhire_2")
        );

        when(repository.findBySchemaNameStartingWithIgnoreCase(baseName))
                .thenReturn(existingTenants);

        // Act
        String result = schemaNameGenerator.generateUniqueSchemaName(originalName);

        // Assert
        assertEquals("tiendajhire_3", result);
    }

    @Test
    void testGenerateUniqueSchemaName_WhenGapsInNumbering_ShouldFillFirstGap() {
        // Arrange
        String originalName = "TiendaJhire";
        String baseName = "tiendajhire";

        List<TenantEntity> existingTenants = Arrays.asList(
                createTenantEntity("tiendajhire"),
                createTenantEntity("tiendajhire_1"),
                createTenantEntity("tiendajhire_3") // Gap en _2
        );

        when(repository.findBySchemaNameStartingWithIgnoreCase(baseName))
                .thenReturn(existingTenants);

        // Act
        String result = schemaNameGenerator.generateUniqueSchemaName(originalName);

        // Assert
        assertEquals("tiendajhire_2", result);
    }

    @Test
    void testGenerateUniqueSchemaName_WhenBaseNameNotExistsButSuffixedVersionsExist_ShouldReturnBaseName() {
        // Arrange
        String originalName = "TiendaJhire";
        String baseName = "tiendajhire";

        List<TenantEntity> existingTenants = Arrays.asList(
                createTenantEntity("tiendajhire_1"),
                createTenantEntity("tiendajhire_2")
                // No existe "tiendajhire" sin sufijo
        );

        when(repository.findBySchemaNameStartingWithIgnoreCase(baseName))
                .thenReturn(existingTenants);

        // Act
        String result = schemaNameGenerator.generateUniqueSchemaName(originalName);

        // Assert
        assertEquals("tiendajhire", result);
    }

    @Test
    void testGenerateUniqueSchemaName_WithSpecialCharacters_ShouldCleanAndGenerate() {
        // Arrange
        String originalName = "Tienda Jhire & Co.!";
        String expectedBaseName = "tiendajhireco";

        when(repository.findBySchemaNameStartingWithIgnoreCase(expectedBaseName))
                .thenReturn(Collections.emptyList());

        // Act
        String result = schemaNameGenerator.generateUniqueSchemaName(originalName);

        // Assert
        assertEquals(expectedBaseName, result);
    }

    @Test
    void testGenerateUniqueSchemaName_WithSpecialCharactersAndDuplicates_ShouldCleanAndAddSuffix() {
        // Arrange
        String originalName = "Tienda Jhire & Co.!";
        String baseName = "tiendajhireco";

        List<TenantEntity> existingTenants = Arrays.asList(
                createTenantEntity("tiendajhireco"),
                createTenantEntity("tiendajhireco_1")
        );

        when(repository.findBySchemaNameStartingWithIgnoreCase(baseName))
                .thenReturn(existingTenants);

        // Act
        String result = schemaNameGenerator.generateUniqueSchemaName(originalName);

        // Assert
        assertEquals("tiendajhireco_2", result);
    }

    @Test
    void testGenerateUniqueSchemaName_WithNullInput_ShouldGenerateTimestampBased() {
        // Arrange
        when(repository.findBySchemaNameStartingWithIgnoreCase(anyString()))
                .thenReturn(Collections.emptyList());

        // Act
        String result = schemaNameGenerator.generateUniqueSchemaName(null);

        // Assert
        assertTrue(result.startsWith("schema_"));
        assertTrue(result.length() > 7); // "schema_" + timestamp
    }

    @Test
    void testGenerateUniqueSchemaName_WithEmptyInput_ShouldGenerateTimestampBased() {
        // Arrange
        when(repository.findBySchemaNameStartingWithIgnoreCase(anyString()))
                .thenReturn(Collections.emptyList());

        // Act
        String result = schemaNameGenerator.generateUniqueSchemaName("   ");

        // Assert
        assertTrue(result.startsWith("schema_"));
        assertTrue(result.length() > 7); // "schema_" + timestamp
    }

    @Test
    void testGenerateUniqueSchemaName_WithOnlySpaces_ShouldRemoveSpaces() {
        // Arrange
        String originalName = "Tienda   Jhire";
        String expectedBaseName = "tiendajhire";

        when(repository.findBySchemaNameStartingWithIgnoreCase(expectedBaseName))
                .thenReturn(Collections.emptyList());

        // Act
        String result = schemaNameGenerator.generateUniqueSchemaName(originalName);

        // Assert
        assertEquals(expectedBaseName, result);
    }

    @Test
    void testGenerateUniqueSchemaName_CaseInsensitiveComparison() {
        // Arrange
        String originalName = "TiendaJhire";
        String baseName = "tiendajhire";

        List<TenantEntity> existingTenants = Arrays.asList(
                createTenantEntity("TiendaJhire"), // Mayúsculas en DB
                createTenantEntity("TIENDAJHIRE_1") // Todo mayúsculas
        );

        when(repository.findBySchemaNameStartingWithIgnoreCase(baseName))
                .thenReturn(existingTenants);

        // Act
        String result = schemaNameGenerator.generateUniqueSchemaName(originalName);

        // Assert
        assertEquals("tiendajhire_2", result);
    }

    @Test
    void testGenerateUniqueSchemaName_WithNumbersInOriginalName() {
        // Arrange
        String originalName = "Tienda123";
        String expectedBaseName = "tienda123";

        when(repository.findBySchemaNameStartingWithIgnoreCase(expectedBaseName))
                .thenReturn(Collections.emptyList());

        // Act
        String result = schemaNameGenerator.generateUniqueSchemaName(originalName);

        // Assert
        assertEquals(expectedBaseName, result);
    }

    @Test
    void testGenerateUniqueSchemaName_WithNumbersAndDuplicates() {
        // Arrange
        String originalName = "Tienda123";
        String baseName = "tienda123";

        List<TenantEntity> existingTenants = Collections.singletonList(
                createTenantEntity("tienda123")
        );

        when(repository.findBySchemaNameStartingWithIgnoreCase(baseName))
                .thenReturn(existingTenants);

        // Act
        String result = schemaNameGenerator.generateUniqueSchemaName(originalName);

        // Assert
        assertEquals("tienda123_1", result);
    }
}