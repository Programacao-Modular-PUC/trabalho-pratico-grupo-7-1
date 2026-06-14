package com.marau.hospedagem.repository;

import com.marau.hospedagem.model.Quarto;
import com.marau.hospedagem.model.enums.StatusQuarto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuartoRepository extends JpaRepository<Quarto, Long> {
    List<Quarto> findByResidenciaId(Long residenciaId);
    List<Quarto> findByResidenciaIdAndStatus(Long residenciaId, StatusQuarto status);
}
