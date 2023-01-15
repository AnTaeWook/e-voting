package gabia.votingserver.controller;

import gabia.votingserver.domain.Agenda;
import gabia.votingserver.domain.type.Role;
import gabia.votingserver.dto.agenda.*;
import gabia.votingserver.dto.agenda.single.AgendaResponseFactory;
import gabia.votingserver.dto.agenda.single.SimpleAgendaResponseDto;
import gabia.votingserver.dto.vote.VoteRequestDto;
import gabia.votingserver.service.AgendaService;
import gabia.votingserver.service.VoteService;
import gabia.votingserver.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class AgendaController {

    private final AgendaService agendaService;
    private final VoteService voteService;

    @GetMapping("/agendas")
    public Page<AllAgendaResponseDto> agendas(
            @PageableDefault(sort = "startsAt", direction = Sort.Direction.ASC) Pageable pageable) {
        return agendaService.getAgendas(pageable).map(AllAgendaResponseDto::from);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/agendas")
    public SimpleAgendaResponseDto create(@RequestBody @Valid AgendaCreateRequestDto agendaCreateRequestDto) {
        Agenda savedAgenda = agendaService.createAgenda(agendaCreateRequestDto);
        return AgendaResponseFactory.getDto(Role.ADMIN, savedAgenda);
    }

    @GetMapping("/agendas/{id}")
    public SimpleAgendaResponseDto agenda(@PathVariable("id") Long agendaId) {
        Agenda agenda = agendaService.getAgenda(agendaId);
        Role role = Role.valueOf(SecurityUtil.getCurrentUserRole());

        if (LocalDateTime.now().isAfter(agenda.getEndsAt()) && role.equals(Role.ADMIN)) {
            return AgendaResponseFactory.getDto(agenda, voteService.getVotesWithAgenda(agenda));
        }
        return AgendaResponseFactory.getDto(role, agenda);
    }

    @PostMapping("/votes")
    public SimpleAgendaResponseDto vote(@RequestBody @Valid VoteRequestDto voteRequestDto) {
        Agenda agenda = agendaService.vote(
                SecurityUtil.getCurrentUserId(),
                voteRequestDto.getAgendaId(),
                voteRequestDto.getType(),
                voteRequestDto.getQuantity());
        return AgendaResponseFactory.getDto(Role.USER, agenda);
    }

    @DeleteMapping("/agendas/{id}")
    public void delete(@PathVariable("id") Long agendaId) {
        agendaService.removeAgenda(agendaId);
    }

    @PatchMapping("/agendas/{id}/terminate")
    public SimpleAgendaResponseDto end(@PathVariable("id") Long agendaId) {
        Agenda agenda = agendaService.terminate(agendaId);
        return AgendaResponseFactory.getDto(Role.ADMIN, agenda);
    }
}
