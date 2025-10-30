package com.vendeskgt.vendeskgt.service;

import com.google.firebase.auth.FirebaseAuthException;
import com.vendeskgt.vendeskgt.domain.dto.TenantWithSubscriptionRegistrationRequest;
import com.vendeskgt.vendeskgt.domain.dto.TenantRegistrationResponse;
import com.vendeskgt.vendeskgt.domain.entity.TenantEntity;
import com.vendeskgt.vendeskgt.controllerAdvice.FirebaseCustomException;
import com.vendeskgt.vendeskgt.firebase.FirebaseCreateUserService;
import com.vendeskgt.vendeskgt.mapper.TenantMapper;
import com.vendeskgt.vendeskgt.repository.TenantRepository;

import com.vendeskgt.vendeskgt.service.util.SchemaNameGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateTenantService {

    private static final Logger log = LoggerFactory.getLogger(CreateTenantService.class);

    private final TenantRepository repository;
    private final TenantMapper mapper;
    private final FirebaseCreateUserService firebaseService;
    private final SchemaNameGenerator schemaNameGenerator;

    @Autowired
    public CreateTenantService(TenantRepository repository, TenantMapper mapper, FirebaseCreateUserService firebaseService, SchemaNameGenerator schemaNameGenerator) {
        this.repository = repository;
        this.mapper = mapper;
        this.firebaseService = firebaseService;
        this.schemaNameGenerator = schemaNameGenerator;
    }

    @Transactional
    public TenantRegistrationResponse register(TenantWithSubscriptionRegistrationRequest request) {

        //Captura request del cliente y se mapea a la entidad llamada customer
        TenantEntity customer = mapper.toEntity(request);

        //almaceno en la variable schemaName el nuevo esquemaName generado en caso de nombre duplicado
        String schemaName = schemaNameGenerator.generateUniqueSchemaName(customer.getSchemaName());

        //seteo del nuevo esquemaName en la entidad customer usando.setSchemaName
        customer.setSchemaName(schemaName);

        //guarda, genera UUID y captura la entidad persistida
        TenantEntity savedCustomer = repository.saveAndFlush(customer);
        log.error(savedCustomer.getSchemaName());

        //Llamado a firebase para guardar el usuario con custom claims
        try {
            firebaseService.registerUser(request.contactEmail(), request.password(), savedCustomer.getTenantId(), "Tenant");
        } catch(FirebaseAuthException e) {
            repository.deleteById(savedCustomer.getTenantId());
            log.error("Rollback exitoso: Tenant {} eliminado de DB debido a fallo en Firebase", savedCustomer.getTenantId());
            throw new FirebaseCustomException(
                    e.getErrorCode(),        // "UNAUTHORIZED_DOMAIN"
                    e.getMessage()          // mensaje descriptivo
            );
        }

        return mapper.toResponse(savedCustomer);
    }
}
