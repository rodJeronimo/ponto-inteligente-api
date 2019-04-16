package br.com.rodrigo.ponto.inteligente.api.controller;

import br.com.rodrigo.ponto.inteligente.api.dtos.EmpresaDTO;
import br.com.rodrigo.ponto.inteligente.api.models.Empresa;
import br.com.rodrigo.ponto.inteligente.api.response.Response;
import br.com.rodrigo.ponto.inteligente.api.services.EmpresaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.Optional;

@RestController
@RequestMapping("/api/empresas")
@CrossOrigin(origins = "*")
public class EmpresaController {

    private static final Logger logger = LoggerFactory.getLogger(EmpresaController.class);

    @Autowired
    private EmpresaService empresaService;

    public EmpresaController() {
    }

    @GetMapping(value = "/cnpj/{cnpj}")
    public ResponseEntity<Response<EmpresaDTO>> buscarPorCnpj(@PathVariable("cnpj") String cnpj){
        logger.info("Buscando empresa por CNPJ: {}", cnpj);

        Response<EmpresaDTO> response = new Response<EmpresaDTO>();

        Optional<Empresa> empresa = empresaService.buscarPorCnpj(cnpj);

        if(!empresa.isPresent()) {
            logger.info("Empresa não encontrada para o CNPJ: {}", cnpj);
            response.getErrors().add("Empresa não encontrada para o CNPJ: " + cnpj);
            return ResponseEntity.badRequest().body(response);
        }

        response.setData(this.converterEmpresaDto(empresa.get()));
        return ResponseEntity.ok(response);
    }

    private EmpresaDTO converterEmpresaDto(Empresa empresa){

        EmpresaDTO empresaDTO = new EmpresaDTO();

        empresaDTO.setId(empresa.getId());
        empresaDTO.setRazaoSocial(empresa.getRazaoSocial());
        empresaDTO.setCnpj(empresa.getCnpj());

        return empresaDTO;
    }
}
