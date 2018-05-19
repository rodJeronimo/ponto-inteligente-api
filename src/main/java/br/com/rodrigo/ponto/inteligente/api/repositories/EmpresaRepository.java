package br.com.rodrigo.ponto.inteligente.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import br.com.rodrigo.ponto.inteligente.api.models.Empresa;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

	@Transactional(readOnly = true)
	Empresa findByCnpj(String cnpj);
}
