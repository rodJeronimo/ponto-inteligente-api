package br.com.rodrigo.ponto.inteligente.api.controller;

import br.com.rodrigo.ponto.inteligente.api.dtos.CadastroPJDto;
import br.com.rodrigo.ponto.inteligente.api.enums.PerfilEnum;
import br.com.rodrigo.ponto.inteligente.api.models.Empresa;
import br.com.rodrigo.ponto.inteligente.api.models.Funcionario;
import br.com.rodrigo.ponto.inteligente.api.response.Response;
import br.com.rodrigo.ponto.inteligente.api.services.EmpresaService;
import br.com.rodrigo.ponto.inteligente.api.services.FuncionarioService;
import br.com.rodrigo.ponto.inteligente.api.utils.PasswordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/cadastrar-pj")
@CrossOrigin(origins = "*")
public class CadastroPJController {

    private static final Logger log = LoggerFactory.getLogger(CadastroPJController.class);

    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private EmpresaService empresaService;

    public CadastroPJController() {
    }

    @PostMapping
    public ResponseEntity<Response<CadastroPJDto>> cadastrar(@Valid @RequestBody CadastroPJDto cadastroPJDto,
                                                        BindingResult result) throws NoSuchAlgorithmException {
        log.info("Cadastrando PJ: {}", cadastroPJDto.toString());
        Response<CadastroPJDto> response = new Response<CadastroPJDto>();

        validarDadosExistentes(cadastroPJDto, result);
        Empresa empresa = converterDTOParaEmpresa((cadastroPJDto));
        Funcionario funcionario = converterDTOParaFuncionario(cadastroPJDto, result);

        if(result.hasErrors()){

            log.error("Erro validando dados de cadastro PJ: {}", result.getAllErrors());
            result.getAllErrors().forEach(objectError -> response.getErrors().add(objectError.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        this.empresaService.persistir(empresa);
        funcionario.setEmpresa(empresa);
        this.funcionarioService.persistir(funcionario);

        response.setData(this.converterCadastroPJDto(funcionario));
        return ResponseEntity.ok(response);
    }

    private void validarDadosExistentes(CadastroPJDto cadastroPJDto, BindingResult result) {

        this.empresaService.buscarPorCnpj(cadastroPJDto.getCnpj())
                .ifPresent(emp -> result.addError(new ObjectError("empresa", "Empresa Já existente")));

        this.funcionarioService.buscarPorCpf(cadastroPJDto.getCpf())
                .ifPresent(func-> result.addError(new ObjectError("funcionario", "Funcionário já existente")));

        this.funcionarioService.buscarPorEmail(cadastroPJDto.getEmail())
                .ifPresent(func -> result.addError(new ObjectError("funcionario", "Email já existente")));

    }

    private Empresa converterDTOParaEmpresa(CadastroPJDto cadastroPJDto){

        Empresa empresa = new Empresa();

        empresa.setCnpj(cadastroPJDto.getCnpj());
        empresa.setRazaoSocial(cadastroPJDto.getRazaosocial());

        return empresa;
    }

    private Funcionario converterDTOParaFuncionario(CadastroPJDto cadastroPJDto, BindingResult result) throws NoSuchAlgorithmException
    {
        Funcionario funcionario = new Funcionario();

        funcionario.setNome(cadastroPJDto.getNome());
        funcionario.setEmail(cadastroPJDto.getEmail());
        funcionario.setCpf(cadastroPJDto.getCpf());
        funcionario.setPerfil(PerfilEnum.ROLE_ADMIN);
        funcionario.setSenha(PasswordUtils.gerarBCrypt(cadastroPJDto.getSenha()));

        return funcionario;
    }

    private CadastroPJDto converterCadastroPJDto(Funcionario funcionario){
        CadastroPJDto cadastroPJDto = new CadastroPJDto();

        cadastroPJDto.setId(funcionario.getId());
        cadastroPJDto.setNome(funcionario.getNome());
        cadastroPJDto.setEmail(funcionario.getEmail());
        cadastroPJDto.setCpf(funcionario.getCpf());
        cadastroPJDto.setRazaosocial(funcionario.getEmpresa().getRazaoSocial());
        cadastroPJDto.setCnpj(funcionario.getEmpresa().getCnpj());

        return cadastroPJDto;
    }
}
