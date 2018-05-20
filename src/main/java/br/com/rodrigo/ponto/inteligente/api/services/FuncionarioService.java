package br.com.rodrigo.ponto.inteligente.api.services;

import java.util.Optional;

import br.com.rodrigo.ponto.inteligente.api.models.Funcionario;

public interface FuncionarioService {
	
	/**
	 * Persiste um funcionário na base de dados
	 * 
	 * @param funcionario
	 * @return Funcionario
	 * @author rodri
	 */

	Funcionario persistir(Funcionario funcionario);

	/**
	 * Busca e retorna um funcionario dado um CPF
	 * 
	 * @param funcionario
	 * @return Optional<Funcionario>
	 * @author rodri
	 */

	Optional<Funcionario> buscarPorCpf(String cpf);

	/**
	 * Busca e retorna um funcionário dado um email
	 * 
	 * @param email
	 * @return Optional<Funcionario>
	 * @author rodri
	 */

	Optional<Funcionario> buscarPorEmail(String email);

	/**
	 * Busca e retorna um funcionario dado um ID
	 * 
	 * @param ID
	 * @return Optional<Funcionario>
	 * @author rodri
	 */

	Optional<Funcionario> buscarPorId(Long id);
}
