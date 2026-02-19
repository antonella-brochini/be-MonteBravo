package com.monteBravo.be.Repository;

import java.util.List;
import java.util.Map;

public interface ReporteRepository {
    List<Object[]> ejecutarConsulta(String consulta, Map<String, Object> parametros);
}
