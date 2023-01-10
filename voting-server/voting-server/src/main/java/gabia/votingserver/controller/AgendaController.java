package gabia.votingserver.controller;

import gabia.votingserver.domain.Agenda;
import gabia.votingserver.dto.agenda.AgendaCreateRequestDto;
import gabia.votingserver.dto.agenda.AllAgendaResponseDto;
import gabia.votingserver.dto.agenda.SimpleAgendaResponseDto;
import gabia.votingserver.service.AgendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class AgendaController {

    private final AgendaService agendaService;

    @GetMapping("/agendas")
    public Page<AllAgendaResponseDto> agendas(
            @PageableDefault(size = 10, sort = "startsAt", direction = Sort.Direction.ASC) Pageable pageable) {
        return agendaService.getAgendas(pageable).map(AllAgendaResponseDto::from);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/agendas")
    public SimpleAgendaResponseDto create(@RequestBody AgendaCreateRequestDto agendaCreateRequestDto) {
        Agenda savedAgenda = agendaService.createAgenda(agendaCreateRequestDto);
        return SimpleAgendaResponseDto.from(savedAgenda);
    }

    @GetMapping("/agendas/{id}")
    public SimpleAgendaResponseDto agenda(@PathVariable("id") Long agendaId) {
        return SimpleAgendaResponseDto.from(agendaService.getAgenda(agendaId));
    }

    @DeleteMapping("/agendas/{id}")
    public void delete(@PathVariable("id") Long agendaId) {
        agendaService.removeAgenda(agendaId);
    }

    @PatchMapping("/agendas/{id}/terminate")
    public SimpleAgendaResponseDto end(@PathVariable("id") Long agendaId) {
        return SimpleAgendaResponseDto.from(agendaService.terminate(agendaId));
    }
}
