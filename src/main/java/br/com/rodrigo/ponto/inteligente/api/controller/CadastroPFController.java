package br.com.rodrigo.ponto.inteligente.api.controller;

import br.com.rodrigo.ponto.inteligente.api.dtos.CadastroPFDto;
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
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;

import javax.swing.text.html.Option;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@RestController
@RequestMapping("/api/cadastrar-pf")
@CrossOrigin(origins = "*")
public class CadastroPFController {

    private static final Logger log = LoggerFactory.getLogger(CadastroPFController.class);

    @Autowired
    private EmpresaService empresaService;

    @Autowired
    private FuncionarioService funcionarioService;

    public CadastroPFController() {
    }

    @PostMapping
    public ResponseEntity<Response<CadastroPFDto>> cadastrar(@Valid @RequestBody CadastroPFDto cadastroPFDto,
                                                             BindingResult bindingResult) throws NoSuchAlgorithmException {

        log.info("Cadastrando PF: {}", cadastroPFDto.toString());

        Response<CadastroPFDto> response = new Response<CadastroPFDto>();

        validarDados(cadastroPFDto, bindingResult);
        Funcionario funcionario = this.converterDtoParaFuncionario(cadastroPFDto, bindingResult);

        if(bindingResult.hasErrors()){
            log.error("Erro validando os dados de cadastro PF: {}", bindingResult.getAllErrors());
            bindingResult.getAllErrors().forEach(objectError -> response.getErrors().add(objectError.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Empresa> empresa = this.empresaService.buscarPorCnpj(cadastroPFDto.getCnpj());
        empresa.ifPresent(empresa1 -> funcionario.setEmpresa(empresa1));
        this.funcionarioService.persistir(funcionario);

        response.setData(this.converterCadastroPFDto(funcionario));
        return ResponseEntity.ok(response);

    }

    private void validarDados(CadastroPFDto cadastroPFDto, BindingResult result){

        Optional<Empresa> empresa = this.empresaService.buscarPorCnpj(cadastroPFDto.getCnpj());

        if(!empresa.isPresent()){
            result.addError(new ObjectError("empresa", "Empresa não cadastrada."));
        }

        this.funcionarioService.buscarPorCpf(cadastroPFDto.getCpf())
                .ifPresent(func -> result.addError(new ObjectError("funcionario", "CPF já existente")));

        this.funcionarioService.buscarPorEmail(cadastroPFDto.getEmail())
                .ifPresent(funcionario -> result.addError(new ObjectError("funcionario", "Email já existente")));
    }

    private Funcionario converterDtoParaFuncionario(CadastroPFDto cadastroPFDto, BindingResult bindingResult) throws NoSuchAlgorithmException{

        Funcionario funcionario = new Funcionario();

        funcionario.setNome(cadastroPFDto.getNome());
        funcionario.setEmail(cadastroPFDto.getEmail());
        funcionario.setCpf(cadastroPFDto.getCpf());
        funcionario.setPerfil(PerfilEnum.ROLE_ADMIN);
        funcionario.setSenha(PasswordUtils.gerarBCrypt(cadastroPFDto.getSenha()));

        cadastroPFDto.getQtdHorasAlmoco()
                .ifPresent(qtdHorasAlmoco -> funcionario.setQtdHorasAlmoco(Float.valueOf(qtdHorasAlmoco)));

        cadastroPFDto.getQtdHorasTrabalhoDia()
                .ifPresent(qtdHorasTrabalhoDia -> funcionario.setQtdHorasTrabalhoDia(Float.valueOf(qtdHorasTrabalhoDia)));

        cadastroPFDto.getValorHora().ifPresent(valorHora -> funcionario.setValorHora(new BigDecimal(valorHora)));

        return funcionario;
    }

    private CadastroPFDto converterCadastroPFDto(Funcionario funcionario){

        CadastroPFDto cadastroPFDto = new CadastroPFDto();
        cadastroPFDto.setId(funcionario.getId());
        cadastroPFDto.setNome(funcionario.getNome());
        cadastroPFDto.setEmail(funcionario.getEmail());
        cadastroPFDto.setCpf(funcionario.getCpf());
        cadastroPFDto.setCnpj(funcionario.getEmpresa().getCnpj());
        funcionario.getQtdHorasAlmocoOpt()
                .ifPresent(qtdhorasAlmoco -> cadastroPFDto.setQtdHorasAlmoco(Optional.of(Float.toString(qtdhorasAlmoco))));
        funcionario.getQtdHorasTrabalhoDiaOpt()
                .ifPresent(qtdHorasTrabalhoDia -> cadastroPFDto.setQtdHorasTrabalhoDia(Optional.of(Float.toString(qtdHorasTrabalhoDia))));
        funcionario.getValorHoraOpt()
                .ifPresent(valorHoraOpt -> cadastroPFDto.setValorHora(Optional.of(valorHoraOpt.toString())));

        return cadastroPFDto;
    }
}
