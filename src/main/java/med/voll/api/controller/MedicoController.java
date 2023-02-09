package med.voll.api.controller;

import jakarta.validation.Valid;
import med.voll.api.dto.DadosCadastroMedico_DTO;
import med.voll.api.dto.DadosUpdateMedico_DTO;
import med.voll.api.dto.ListMedicoDTO;
import med.voll.api.dto.DadosMedicoDetalhadosDTO;
import med.voll.api.medico.Medico;
import med.voll.api.repository.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("medicos")
public class MedicoController {

    @Autowired //Injeção de dependencias - Instancia o MR e passa as informações
    private MedicoRepository repository;

    @Transactional //Transação ativa com o BD
    @PostMapping//Anootation, post
    //Anotação @RequesteBody, indica que a String json irá receber os valores passasos na requisição
    public ResponseEntity saveMed(@RequestBody @Valid DadosCadastroMedico_DTO dadosMedico, UriComponentsBuilder uriBuilder)
    {
        //recebe um DTO como paramentro do metodo e transformar em um Medico para então mandar para método Save()
       var medico = new Medico(dadosMedico);
        repository.save(medico);

        //Cod 201 Created
        var uri = uriBuilder.path("/medicos/{id}").buildAndExpand(medico.getId()).toUri();
        return ResponseEntity.created(uri).body(new DadosMedicoDetalhadosDTO(medico));

    }

    /* Método sem paginação
    @GetMapping //Anotation Get
    public List<ListMedicoDTO> listAll(){ //Método para listar todos os médicos
        // Stream() é um método do Java 8, eno médoto map() será feito um mapeamento da entidade Médico
        // para os atributos selecionados, chamando o construtor do DTO ListMedicoDTO, finalizando com o método toList()
        return repository.findAll().stream().map(ListMedicoDTO::new).toList() ;
    }
    */

    /*
    //Método com Paginação - Substitui o o List por Page e acrescenta os paramentros no listAll(Pageable paginacao) e no método findAll(paginacao)
    @GetMapping //Anotation Get
    public Page<ListMedicoDTO> listByStatusTrue(@PageableDefault(size=10, sort = {"nome"}) Pageable paginacao){ //Método para listar todos os médicos
        return repository.findAll(paginacao).map(ListMedicoDTO::new);
    }
     */

    //Método para atualizar
    @PutMapping
    @Transactional
    public ResponseEntity update(@RequestBody @Valid DadosUpdateMedico_DTO dadosMedico){
        var medico = repository.getReferenceById(dadosMedico.id());
        medico.atualizarInformacoes(dadosMedico);

        return ResponseEntity.ok(new DadosMedicoDetalhadosDTO(medico));
    }

    //Método para inativa - Neste modelo apenas inativa o medico
    @DeleteMapping("/{id}")
    @Transactional
    //A annotation @PathVariable vincula o Id  passado no @DeleteMapping com o id criado no método delete(Long id)
    public ResponseEntity inativa(@PathVariable Long id){
        var medico = repository.getReferenceById(id);
        medico.desabilitar();

        //Cod 204 No Content
        return ResponseEntity.noContent().build();
    }
    //Método para detalhar o medico
    @GetMapping("/{id}")
    //A annotation @PathVariable vincula o Id  passado no @DeleteMapping com o id criado no método delete(Long id)
    public ResponseEntity detalhar(@PathVariable Long id){
        var medico = repository.getReferenceById(id);
        //Cod 200 ok
        return ResponseEntity.ok(new DadosMedicoDetalhadosDTO(medico));
    }

    //Método com Paginação - Substitui o o List por Page e acrescenta os paramentros no listAll(Pageable paginacao) e no método findAll(paginacao)
    @GetMapping("/true") //Anotation Get
    public ResponseEntity<Page<ListMedicoDTO>> listByStatusTrue(@PageableDefault(size=10, sort = {"nome"}) Pageable paginacao){ //Método para listar todos os médicos
        var page =  repository.findAllByStatusTrue(paginacao).map(ListMedicoDTO::new);
        //Cod 200 ok
        return ResponseEntity.ok(page);
    }

    //Método com Paginação - Substitui o o List por Page e acrescenta os paramentros no listAll(Pageable paginacao) e no método findAll(paginacao)
    @GetMapping("/false") //Anotation Get
    public ResponseEntity<Page<ListMedicoDTO>> listByStatusFalse(@PageableDefault(size=5, sort = {"nome"}) Pageable paginacao){ //Método para listar todos os médicos
        var page =  repository.findAllByStatusFalse(paginacao).map(ListMedicoDTO::new);
        //Cod 200 ok
        return ResponseEntity.ok(page);
    }

    @GetMapping("/listAll") //Anotation Get
    public ResponseEntity<Page<ListMedicoDTO>> findAll(@PageableDefault(size=10, sort = {"nome"}) Pageable paginacao){ //Método para listar todos os médicos
        var page =  repository.findAll(paginacao).map(ListMedicoDTO::new);
        //Cod 200 ok
        return ResponseEntity.ok(page);
    }


    /*
    //Método para delete

    @DeleteMapping("/{id}")
    @Transactional
    //A annotation @PathVariable vincula o Id  passado no @DeleteMapping com o id criado no método delete(Long id)
    public void delete(@PathVariable Long id){
        repository.deleteById(id);
    }
     */


}