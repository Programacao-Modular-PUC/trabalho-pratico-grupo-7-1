package com.marau.hospedagem.service;

import com.marau.hospedagem.model.Residencia;
import com.marau.hospedagem.repository.ResidenciaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ResidenciaService {

    private final ResidenciaRepository repository;

    public ResidenciaService(ResidenciaRepository repository) {
        this.repository = repository;
    }

    public List<Residencia> listarTodas() {
        return repository.findAll();
    }

    public Residencia buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Residência não encontrada: " + id));
    }

    public Residencia criar(Residencia residencia) {
        return repository.save(residencia);
    }

    public Residencia atualizar(Long id, Residencia dados) {
        Residencia existente = buscarPorId(id);
        existente.setEndereco(dados.getEndereco());
        existente.setNumero(dados.getNumero());
        existente.setBairro(dados.getBairro());
        existente.setCep(dados.getCep());
        existente.setTelefone(dados.getTelefone());
        existente.setEmail(dados.getEmail());
        return repository.save(existente);
    }

    public void deletar(Long id) {
        buscarPorId(id);
        repository.deleteById(id);
    }
}
