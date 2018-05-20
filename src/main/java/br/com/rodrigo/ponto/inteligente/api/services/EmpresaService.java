package br.com.rodrigo.ponto.inteligente.api.services;

import java.util.Optional;

import br.com.rodrigo.ponto.inteligente.api.models.Empresa;

public interface EmpresaService {

	/**
	 * Retorna uma empresa dado um CNPJ.
	 * 
	 * @Param cnpj
	 * 
	 * @return Optional <Empresa>
	 */

	Optional<Empresa> buscarPorCnpj(String cnpj);
	
	/**
	 * Cadastra uma nova empresa na base de dados
	 * 
	 * @Param empresa
	 * 
	 * @return Empresa
	 */
	
	Empresa persistir(Empresa empresa);
}
