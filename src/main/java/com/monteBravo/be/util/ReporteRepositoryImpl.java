package com.monteBravo.be.util;

import com.monteBravo.be.Repository.ReporteRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class ReporteRepositoryImpl implements ReporteRepository {

    private final EntityManager entityManager;

    public ReporteRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Object[]> ejecutarConsulta(String consulta, Map<String, Object> parametros) {
        Query query = entityManager.createNativeQuery(consulta);

        // Asignar parámetros a la consulta
        if (parametros != null) {
            for (Map.Entry<String, Object> entry : parametros.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }

        return query.getResultList();
    }
}
