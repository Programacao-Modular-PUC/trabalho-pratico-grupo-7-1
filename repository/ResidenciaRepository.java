package com.marau.hospedagem.repository;

import com.marau.hospedagem.model.Residencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResidenciaRepository extends JpaRepository<Residencia, Long> {
}
