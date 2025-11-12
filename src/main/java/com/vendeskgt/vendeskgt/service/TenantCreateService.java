package com.vendeskgt.vendeskgt.service;

import com.google.firebase.auth.FirebaseAuthException;
import com.vendeskgt.vendeskgt.domain.dto.TenantWithSubscriptionRegistrationRequest;
import com.vendeskgt.vendeskgt.domain.dto.TenantRegistrationResponse;
import com.vendeskgt.vendeskgt.domain.entity.TenantEntity;
import com.vendeskgt.vendeskgt.controllerAdvice.FirebaseCustomException;
import com.vendeskgt.vendeskgt.domain.entity.TenantSubscriptionEntity;
import com.vendeskgt.vendeskgt.domain.enums.PaymentMethodEnum;
import com.vendeskgt.vendeskgt.domain.enums.PlanTypeEnum;
import com.vendeskgt.vendeskgt.domain.enums.RoleEnum;
import com.vendeskgt.vendeskgt.firebase.FirebaseCreateUserService;
import com.vendeskgt.vendeskgt.mapper.TenantMapper;
import com.vendeskgt.vendeskgt.repository.TenantRepository;

import com.vendeskgt.vendeskgt.repository.TenantSubscriptionRepository;
import com.vendeskgt.vendeskgt.service.util.SchemaNameGenerator;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TenantCreateService {

    private static final Logger log = LoggerFactory.getLogger(TenantCreateService.class);

    private final TenantRepository repository;
    private final TenantMapper mapper;
    private final FirebaseCreateUserService firebaseService;
    private final SchemaNameGenerator schemaNameGenerator;
    private final TenantSubscriptionRepository subscriptionRepository;
    private final EntityManager entityManager;

    @Autowired
    public TenantCreateService(TenantRepository repository, TenantMapper mapper, FirebaseCreateUserService firebaseService,
                                  SchemaNameGenerator schemaNameGenerator, TenantSubscriptionRepository subscriptionRepository,
                               EntityManager entityManager) {
        this.repository = repository;
        this.mapper = mapper;
        this.firebaseService = firebaseService;
        this.schemaNameGenerator = schemaNameGenerator;
        this.subscriptionRepository = subscriptionRepository;
        this.entityManager = entityManager;
    }

    @Transactional(rollbackFor = Exception.class)
    public TenantRegistrationResponse register(TenantWithSubscriptionRegistrationRequest request) {

        // 1️⃣ Validaciones
        if (request.name() == null || request.name().isBlank()) throw new IllegalArgumentException("Nombre obligatorio");
        if (request.lastName() == null || request.lastName().isBlank()) throw new IllegalArgumentException("Apellido obligatorio");
        if (request.contactEmail() == null || request.contactEmail().isBlank()) throw new IllegalArgumentException("Email obligatorio");
        if (request.schemaName() == null || request.schemaName().isBlank()) throw new IllegalArgumentException("Nombre de la empresa obligatorio");


        //Captura request del cliente y se mapea a la entidad llamada customer
        TenantEntity customer = mapper.toEntity(request);

        //almaceno en la variable schemaName el nuevo esquemaName generado en caso de nombre duplicado
        String schemaName = schemaNameGenerator.generateUniqueSchemaName(customer.getSchemaName());

        //seteo del nuevo esquemaName en la entidad customer usando.setSchemaName
        customer.setSchemaName(schemaName);

        //guarda, genera UUID y captura la entidad persistida
        TenantEntity savedCustomer = repository.saveAndFlush(customer);
        log.error(savedCustomer.getSchemaName());


        // ✅ 4️⃣ Guardar suscripción
        TenantSubscriptionEntity subscription = TenantSubscriptionEntity.builder()
                .tenantEntity(savedCustomer)
                .planType(PlanTypeEnum.valueOf(request.subscription().planType()))
                .paymentMethod(PaymentMethodEnum.valueOf(request.subscription().paymentMethod()))
                .subscriptionStatus(true)
                .build();

        TenantSubscriptionEntity savedSubscription = subscriptionRepository.saveAndFlush(subscription);

        savedSubscription = subscriptionRepository.findById(savedSubscription.getId()).orElseThrow();

        entityManager.refresh(savedSubscription);

        log.info("🔍 DEBUG savedSubscription: id={}, planType={}, startDate={}, endDate={}",
                savedSubscription.getId(),
                savedSubscription.getPlanType(),
                savedSubscription.getStartDate(),
                savedSubscription.getEndDate()
        );

        log.info("✅ Suscripción creada para tenant {}", savedCustomer.getTenantId());


        //Llamado a firebase para guardar el usuario con custom claims
        try {
            firebaseService.registerUser(request.contactEmail(), request.password(), savedCustomer.getTenantId(), RoleEnum.TENANT);
        } catch(FirebaseAuthException e) {
            repository.deleteById(savedCustomer.getTenantId());
            log.error("Rollback exitoso: Tenant {} eliminado de DB debido a fallo en Firebase", savedCustomer.getTenantId());
            throw new FirebaseCustomException(
                    e.getErrorCode(),        // "UNAUTHORIZED_DOMAIN"
                    e.getMessage()          // mensaje descriptivo
            );
        }

        return mapper.toResponse(savedCustomer, savedSubscription);
    }
}
