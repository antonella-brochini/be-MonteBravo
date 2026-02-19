package com.monteBravo.be.Services;

import com.monteBravo.be.Controller.TemplateMailController;
import com.monteBravo.be.DTO.emailDto.TemplateDTO;
import com.monteBravo.be.Repository.TemplateMailRepository;
import com.monteBravo.be.Repository.TemplateMailVariableRepository;
import com.monteBravo.be.entity.TemplateMail;
import com.monteBravo.be.entity.TemplateVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TemplateMailService {
    private static final Logger logger = LoggerFactory.getLogger(TemplateMailService.class);

    private final TemplateMailRepository templateMailRepository;
    private final TemplateMailVariableRepository templateMailVariableRepository;

    public TemplateMailService(TemplateMailRepository templateMailRepository, TemplateMailVariableRepository templateMailVariableRepository) {
        this.templateMailRepository = templateMailRepository;
        this.templateMailVariableRepository = templateMailVariableRepository;
    }


    public List<TemplateMail> getAllTemplates() {
        return templateMailRepository.findAll();
    }


    public Optional<TemplateMail> getTemplateById(String id) {
        return templateMailRepository.findById(id);
    }


    public TemplateMail saveTemplate(TemplateMail templateMail) {
        return templateMailRepository.save(templateMail);
    }

    public List<TemplateVariable> saveAllVariables(List<TemplateVariable> templateVariables) {
        // Dividir en variables nuevas y existentes por ID
        List<TemplateVariable> nuevasVariables = new ArrayList<>(
                templateVariables.stream()
                        .filter(tv -> tv.getId() == null) // Variables nuevas no tienen ID
                        .toList()
        );

        List<TemplateVariable> variablesConId = templateVariables.stream()
                .filter(tv -> tv.getId() != null) // Variables existentes tienen ID
                .toList();

        // Buscar las variables existentes en la base de datos
        List<Long> ids = variablesConId.stream()
                .map(TemplateVariable::getId)
                .toList();

        Map<Long, TemplateVariable> existentesEnBD = templateMailVariableRepository.findAllById(ids)
                .stream()
                .collect(Collectors.toMap(TemplateVariable::getId, tv -> tv));

        // Separar las variables existentes y las no encontradas
        List<TemplateVariable> actualizadas = variablesConId.stream()
                .filter(tv -> existentesEnBD.containsKey(tv.getId())) // Solo las que están en la BD
                .map(tv -> {
                    TemplateVariable existente = existentesEnBD.get(tv.getId());
                    existente.setVariable(tv.getVariable());
                    existente.setNombrePropiedad(tv.getNombrePropiedad());
                    return existente;
                })
                .toList();

        List<TemplateVariable> noEncontradas = variablesConId.stream()
                .filter(tv -> !existentesEnBD.containsKey(tv.getId())) // ID no encontrado en la BD
                .peek(tv -> tv.setId(null)) // Convertirlas en nuevas variables
                .toList();

        // Agregar las no encontradas a la lista de nuevas variables
        nuevasVariables.addAll(noEncontradas);

        // Guardar las actualizadas y las nuevas
        templateMailVariableRepository.saveAll(actualizadas);
        return templateMailVariableRepository.saveAll(nuevasVariables);
    }


    public List<TemplateDTO> getVariablesByTemplateId(String templateId) {
        return templateMailVariableRepository.findVariablesByTemplateId(templateId);
    }

    public List<TemplateDTO> getAllTemplateVariables() {
        return templateMailVariableRepository.findAllTemplateVariables();
    }

    public Optional<TemplateVariable> getVariable(Long id) {
        return templateMailVariableRepository.findById(id);
    }


    public void deleteTemplate(String id) {
        templateMailRepository.deleteById(id);
    }

    public ResponseEntity<?> deleteVariables(List<Integer> idVariables) {

        try {   // Convertir los IDs a Long
            List<Long> idVariablesLong = idVariables.stream()
                    .map(Long::valueOf)
                    .toList();

            // Filtrar los IDs que existen en la base de datos
            List<Long> idsExistentes = idVariablesLong.stream()
                    .filter(templateMailVariableRepository::existsById)
                    .toList();

            if (idsExistentes.isEmpty()) {
                logger.info("No se encontraron variables con el/los ID proporcionados");
                return ResponseEntity.status(204).body("No se encontraron variables con el/los ID proporcionados");
            }

            // Eliminar los IDs existentes
            templateMailVariableRepository.deleteAllById(idsExistentes);

            return ResponseEntity.ok("Variables eliminadas correctamente: " + idsExistentes);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(500).body("Error del servidor al procesar la operacion");
        }
    }

}
