package com.monteBravo.be.Repository;

import com.monteBravo.be.DTO.emailDto.TemplateDTO;
import com.monteBravo.be.entity.TemplateVariable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface TemplateMailVariableRepository extends JpaRepository<TemplateVariable, Long> {
    @Query("SELECT new com.monteBravo.be.DTO.emailDto.TemplateDTO( " +
            "tm.id, tm.description, tm.name, tv.id, tv.variable, tm.asunto, tv.nombrePropiedad, tm.tipoEmail) " +
            "FROM TemplateMail tm " +
            "JOIN tm.variables tv " +
            "WHERE tm.id = :templateId")
    List<TemplateDTO> findVariablesByTemplateId(@Param("templateId") String templateId);

    @Query("SELECT new com.monteBravo.be.DTO.emailDto.TemplateDTO( " +
            "tm.id, tm.description, tm.name, tv.id, tv.variable, tm.asunto,tv.nombrePropiedad, tm.tipoEmail) " +
            "FROM TemplateMail tm " +
            "JOIN tm.variables tv")
    List<TemplateDTO> findAllTemplateVariables();
    List<TemplateVariable> findByVariableIn(List<String> variableNames);

}
