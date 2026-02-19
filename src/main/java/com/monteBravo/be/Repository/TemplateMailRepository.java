package com.monteBravo.be.Repository;

import com.monteBravo.be.entity.TemplateMail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateMailRepository extends JpaRepository<TemplateMail, String> {
}
