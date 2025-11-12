package com.vendeskgt.vendeskgt.service.util;

import com.vendeskgt.vendeskgt.domain.entity.TenantEntity;
import com.vendeskgt.vendeskgt.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SchemaNameGenerator {

    @Autowired
    private TenantRepository repository;

    private static final Logger log = LoggerFactory.getLogger(SchemaNameGenerator.class);

    public String generateUniqueSchemaName(String originalName) {

        String baseName = cleanBaseName(originalName);
        log.warn("generateUniqueSchemaName: baseName limpio={}", baseName);

        // Obtén todos los que empiecen con el mismo prefijo (ej: "tiendajhire")
        List<TenantEntity> existingTenants  = repository.findBySchemaNameStartingWithIgnoreCase(baseName);

        if (existingTenants.isEmpty()) {
            log.warn("Nombre único generado: {}", baseName);
            return baseName;
        }

        // 3. Extraer los nombres existentes en un Set para búsqueda O(1)
        Set<String> existingNames = existingTenants.stream()
                .map(tenant -> tenant.getSchemaName().toLowerCase())
                .collect(Collectors.toSet());

        // 4. Si el nombre base no existe, usarlo
        if (!existingNames.contains(baseName.toLowerCase())) {
            log.warn("Nombre único generado: {}", baseName);
            return baseName;
        }

        // 5. Buscar el siguiente número disponible
        int counter = 1;
        String candidateName;
        do {
            candidateName = baseName + "_" + counter;
            counter++;
        } while (existingNames.contains(candidateName.toLowerCase()));

        log.info("Nombre único generado: {}", candidateName);
        return candidateName;
    }


    private String cleanBaseName (String baseName){
        if(baseName == null || baseName.trim().isEmpty()){
            return "schema_" + System.currentTimeMillis();
        }
        return baseName
                .replaceAll("\\s+","")
                .toLowerCase()
                .replaceAll("[^a-z0-9]","");
    }
}
