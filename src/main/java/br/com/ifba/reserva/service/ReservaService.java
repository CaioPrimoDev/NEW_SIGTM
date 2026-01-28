package br.com.ifba.reserva.service;

import br.com.ifba.evento.repository.EventoRepository;
import br.com.ifba.infrastructure.exception.BusinessException;
import br.com.ifba.pontoturistico.repository.PontoTuristicoRepository;
import br.com.ifba.reserva.entity.Reserva;
import br.com.ifba.reserva.repository.ReservaRepository;
// --- ALTERAÇÃO 1: Importar a Interface do Serviço de Sessão, não a Entidade ---
import br.com.ifba.sessao.service.UsuarioSessionIService;
import br.com.ifba.usuario.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author juant
 */
@Service
@RequiredArgsConstructor
public class ReservaService implements ReservaIService {

    // Constantes de erro e mensagens
    private static final String RESERVA_NULL = "Dados da Reserva não fornecidos.";
    private static final String RESERVA_NOT_FOUND = "Reserva com o ID informado não foi encontrada.";
   // private static final String PERMISSAO_NEGADA = "Permissão negada. A reserva não pertence ao usuário logado ou o usuário não é um gestor.";
    private static final String USUARIO_NAO_AUTENTICADO = "Acesso negado. Nenhum usuário autenticado na sessão.";
    private static final String GESTOR = "GESTOR";
    private static final String PARCEIRO = "PARCEIRO"; // Constante usada agora
    private static final String ITEM_RESERVA_OBRIGATORIO = "A reserva deve estar associada a um Ponto Turístico ou a um Evento.";
    private static final String ITEM_INEXISTENTE = "O Ponto Turístico ou Evento selecionado para a reserva não existe.";
    private static final String USUARIO_JA_POSSUI_RESERVA = "O usuário já possui uma reserva para este item nesta data.";

    private static final Logger log = LoggerFactory.getLogger(ReservaService.class);

    private final ReservaRepository reservaRepository;
    private final PontoTuristicoRepository pontoTuristicoRepository;
    private final EventoRepository eventoRepository;

    // --- ALTERAÇÃO 2: Injetar o SERVICE, não a entidade ---
    // Isso resolve o NullPointerException
    private final UsuarioSessionIService usuarioSessionService;


    // Valida os campos essenciais de uma Reserva.
    private void validarDadosReserva(Reserva reserva) {
        if (reserva == null) throw new IllegalArgumentException(RESERVA_NULL);
        if (reserva.getUsuario() == null) throw new BusinessException("O usuário da reserva é obrigatório.");

        // Valida se pelo menos um e apenas um item foi associado
        if (reserva.getPontoTuristico() == null && reserva.getEvento() == null) {
            throw new BusinessException(ITEM_RESERVA_OBRIGATORIO);
        }
        if (reserva.getPontoTuristico() != null && reserva.getEvento() != null) {
            throw new BusinessException("A reserva não pode ser para um Ponto Turístico e um Evento simultaneamente.");
        }

        // Valida a existência do item que foi preenchido
        if (reserva.getPontoTuristico() != null) {
            if (!pontoTuristicoRepository.existsById(reserva.getPontoTuristico().getId())) {
                throw new BusinessException(ITEM_INEXISTENTE);
            }
        } else { // Se não é ponto turístico, é evento
            if (!eventoRepository.existsById(reserva.getEvento().getId())) {
                throw new BusinessException(ITEM_INEXISTENTE);
            }
        }

        // Valida a data
        if (reserva.getDataReserva() == null || reserva.getDataReserva().isBefore(LocalDate.now())) {
            throw new BusinessException("A data da reserva deve ser futura.");
        }
    }

    // Verifica se já existe uma reserva para o mesmo usuário, no mesmo ponto turístico e na mesma data.
    private void validarConflitoDeReserva(Reserva reserva) {
        List<Reserva> reservasExistentes;

        if (reserva.getPontoTuristico() != null) {
            reservasExistentes = reservaRepository.findByUsuarioAndPontoTuristicoAndDataReserva(
                    reserva.getUsuario(),
                    reserva.getPontoTuristico(),
                    reserva.getDataReserva()
            );
        }
        else {
            reservasExistentes = reservaRepository.findByUsuarioAndEventoAndDataReserva(
                    reserva.getUsuario(),
                    reserva.getEvento(),
                    reserva.getDataReserva()
            );
        }

        if (!reservasExistentes.isEmpty() && !reservasExistentes.getFirst().getId().equals(reserva.getId())) {
            throw new BusinessException(USUARIO_JA_POSSUI_RESERVA);
        }
    }

    @Override
    @Transactional
    public void save(Reserva reserva) {
        log.info("Iniciando processo de salvamento de Reserva...");

        // --- ALTERAÇÃO 3: Usar o serviço para pegar o usuário ---
        Usuario usuarioSessao = usuarioSessionService.getUsuarioLogado();

        if (usuarioSessao == null) {
            throw new BusinessException(USUARIO_NAO_AUTENTICADO);
        }

        reserva.setUsuario(usuarioSessao);

        // Gera um UUID completo, que é garantidamente único
        String uuid = UUID.randomUUID().toString();
        String parteUnica = uuid.substring(0, 8);
        String tokenFinal = "SIGTM-" + parteUnica;

        reserva.setToken(tokenFinal);

        validarDadosReserva(reserva);
        validarConflitoDeReserva(reserva);

        log.info("Validações concluídas. Salvando Reserva...");
        reservaRepository.save(reserva);
    }

    @Override
    @Transactional
    public void delete(Reserva reserva){
        if (reserva == null || reserva.getId() == null) {
            throw new BusinessException(RESERVA_NULL);
        }

        Reserva existente = this.findById(reserva.getId());
        reservaRepository.delete(existente);
        log.info("Reserva desmarcada com sucesso!");
    }

    @Override
    public Reserva findById(Long id) {
        if (id == null) {
            throw new BusinessException("O ID para busca não pode ser nulo.");
        }

        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new BusinessException(RESERVA_NOT_FOUND));

        return reserva;
    }

    @Override
    public List<Reserva> findAll(){
        // --- ALTERAÇÃO 4: Usar o serviço para pegar o usuário ---
        Usuario usuario = usuarioSessionService.getUsuarioLogado();

        // Verifica se veio nulo ANTES de tentar acessar qualquer método
        if (usuario == null) {
            throw new BusinessException(USUARIO_NAO_AUTENTICADO);
        }

        String tipoUsuario = usuario.getTipo().getNome();

        // --- ALTERAÇÃO 5: Lógica para GESTOR e PARCEIRO ---
        // Se for GESTOR ou PARCEIRO, retorna todas (ou ajuste se parceiro ver só as dele)
        // Aqui assumi que você quer que o parceiro tenha privilégio similar ao buscar tudo,
        // mas se o parceiro tiver que ver só as dele, remova o "|| PARCEIRO.equals..."
        if (GESTOR.equals(tipoUsuario) || PARCEIRO.equals(tipoUsuario)) {
            log.info("Usuário {} buscando todas as reservas do sistema.", tipoUsuario);
            return reservaRepository.findAll();
        }

        // Se for um usuário comum, retorna apenas as suas próprias reservas
        log.info("Buscando todas as reservas para o usuário de ID: {}", usuario.getPessoa().getId());
        return reservaRepository.findByUsuario(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> findByUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não pode ser nulo.");
        }
        log.info("Buscando todas as reservas para o usuário de ID: {}", usuario.getPessoa().getId());
        return reservaRepository.findByUsuario(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> findByTokenContainingIgnoreCase(String token) {
        // --- ALTERAÇÃO 6: Usar o serviço para pegar o usuário ---
        Usuario usuario = usuarioSessionService.getUsuarioLogado();

        if (usuario == null) {
            throw new BusinessException(USUARIO_NAO_AUTENTICADO);
        }

        // Verifica se o usuário é um gestor OU parceiro
        boolean isPrivilegiado = GESTOR.equals(usuario.getTipo().getNome()) ||
                PARCEIRO.equals(usuario.getTipo().getNome());

        if (isPrivilegiado) {
            log.info("Usuário privilegiado buscando reserva pelo token: {}", token);
            return reservaRepository.findByTokenContainingIgnoreCase(token);
        } else {
            log.info("Usuário {} buscando própria reserva pelo token: {}", usuario.getPessoa().getId(), token);
            return reservaRepository.findByTokenContainingIgnoreCaseAndUsuario(token, usuario);
        }
    }
}