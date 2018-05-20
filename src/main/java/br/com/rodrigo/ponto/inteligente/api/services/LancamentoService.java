package br.com.rodrigo.ponto.inteligente.api.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import br.com.rodrigo.ponto.inteligente.api.models.Lancamento;

public interface LancamentoService {
	/**
	 * Busca funcionario pelo ID
	 * 
	 * @param funcionarioId
	 * @param pageRequest
	 * @return Page<Lancamento>
	 * 
	 */
	
	Page<Lancamento> buscarPorFuncionarioId(Long funcionarioId, PageRequest pageRequest);
	
	/**
	 * Retorna o lançamento por ID
	 * 
	 * @param id
	 * @return Optional<Lancamento>
	 * 
	 */
	
	Optional<Lancamento> buscarPorid(Long id);
	
	/**
	 * Persiste o lançamento na base de dados
	 * 
	 * @param lancamento
	 * @return Lancamento
	 * 
	 */
	
	Lancamento persistir(Lancamento lancamento);
	
	/**
	 * Remove um lançamento da base de dados
	 * 
	 * @param id
	 * 
	 */
	
	void remover(Long id);
}
