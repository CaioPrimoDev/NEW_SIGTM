package br.com.ifba.avaliacoes.service;

import br.com.ifba.avaliacoes.entity.Avaliacao;
import br.com.ifba.avaliacoes.repository.AvaliacaoRepository;
import br.com.ifba.infrastructure.exception.BusinessException;
import br.com.ifba.pontoturistico.entity.PontoTuristico;
import br.com.ifba.pontoturistico.repository.PontoTuristicoRepository;
import br.com.ifba.util.RegraNegocioException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AvaliacaoService implements AvaliacaoIService {

    private final AvaliacaoRepository repo;
    private final PontoTuristicoRepository pontoRepo;

    private static final int MELHORES_LIMITE = 4;
    private static final int PIORES_LIMITE = 2;

    // =================== BUSCAS ===================
    @Override
    public List<Avaliacao> findAllByPonto(Long pontoId) {
        validarIdPonto(pontoId);
        try {
            List<Avaliacao> avaliacoes = repo.findByPontoTuristicoIdOrderByEstrelasDesc(pontoId);
            if (avaliacoes.isEmpty()) {
                log.info("Nenhuma avaliação encontrada para o ponto turístico ID={}", pontoId);
            }
            return avaliacoes;
        } catch (RuntimeException e) {
            log.error("Erro ao buscar avaliações para ponto ID={}", pontoId, e);
            throw new BusinessException("Erro ao buscar avaliações para o ponto turístico.");
        }
    }

    @Override
    public List<Avaliacao> getMelhoresByPonto(Long pontoId) {
        validarIdPonto(pontoId);
        try {
            List<Avaliacao> avaliacoes = repo.findByPontoTuristicoIdAndEstrelasGreaterThanEqualOrderByEstrelasDesc(pontoId, MELHORES_LIMITE);
            if (avaliacoes.isEmpty()) {
                log.info("Nenhuma avaliação considerada 'melhor' encontrada para o ponto ID={}", pontoId);
            }
            return avaliacoes;
        } catch (RuntimeException e) {
            log.error("Erro ao buscar melhores avaliações para ponto ID={}", pontoId, e);
            throw new BusinessException("Erro ao buscar melhores avaliações.");
        }
    }

    @Override
    public List<Avaliacao> getPioresByPonto(Long pontoId) {
        validarIdPonto(pontoId);
        try {
            List<Avaliacao> avaliacoes = repo.findByPontoTuristicoIdAndEstrelasLessThanEqualOrderByEstrelasAsc(pontoId, PIORES_LIMITE);
            if (avaliacoes.isEmpty()) {
                log.info("Nenhuma avaliação considerada 'pior' encontrada para o ponto ID={}", pontoId);
            }
            return avaliacoes;
        } catch (RuntimeException e) {
            log.error("Erro ao buscar piores avaliações para ponto ID={}", pontoId, e);
            throw new BusinessException("Erro ao buscar piores avaliações.");
        }
    }

    @Override
    public List<Avaliacao> findByUsuarioId(Long usuarioId) {
        try {
            List<Avaliacao> avaliacoes = repo.findByUsuarioId(usuarioId);
            if (avaliacoes.isEmpty()) {
                log.info("Esse Usuario não possui comentários");
            }
            return avaliacoes;
        } catch (RuntimeException e) {
            log.error("Erro ao buscar avaliações do Usuario com ID={}", usuarioId, e);
            throw new BusinessException("Erro ao buscar avaliações.");
        }
    }

    @Override
    public boolean existsByUsuarioIdAndPontoTuristicoId(Long usuarioId, Long pontoId) {
        return repo.existsByUsuarioIdAndPontoTuristicoId(usuarioId, pontoId);
    }

    // =================== SAVE ===================
    @Override
    @Transactional
    public Avaliacao saveForPonto(Long pontoId, Avaliacao avaliacao) {
        validarIdPonto(pontoId);
        validarAvaliacao(avaliacao);

        try {
            PontoTuristico ponto = pontoRepo.findById(pontoId)
                    .orElseThrow(() -> new RegraNegocioException("Ponto turístico não encontrado"));
            avaliacao.setPontoTuristico(ponto);

            return repo.save(avaliacao);
        } catch (DataIntegrityViolationException e) {
            log.error("Violação de integridade ao salvar Avaliação para ponto ID={}", pontoId, e);
            throw new BusinessException("Dados da avaliação inválidos ou duplicados.", e);
        } catch (RuntimeException e) {
            log.error("Erro inesperado ao salvar Avaliação para ponto ID={}", pontoId, e);
            throw new BusinessException("Erro ao salvar avaliação.");
        }
    }

    // =================== DELETE ===================
    @Override
    @Transactional
    public void delete(Long id) {
        validarIdAvaliacao(id);
        try {
            repo.deleteById(id);
            repo.flush();
            log.info("Avaliação ID={} removida com sucesso.", id);
        } catch (EmptyResultDataAccessException e) {
            log.error("Tentativa de exclusão de Avaliação inexistente ID={}", id, e);
            throw new BusinessException("Avaliação não encontrada para exclusão.", e);
        } catch (RuntimeException e) {
            log.error("Erro inesperado ao excluir Avaliação ID={}", id, e);
            throw new BusinessException("Erro ao excluir avaliação.");
        }
    }

    // =================== UPDATE ===================
    @Override
    @Transactional
    public Avaliacao update(Long id, Avaliacao nova) {
        validarIdAvaliacao(id);
        validarAvaliacao(nova);

        try {
            return repo.findById(id).map(existing -> {
                existing.setEstrelas(nova.getEstrelas());
                existing.setDescricao(nova.getDescricao());
                existing.setNomeAutor(nova.getNomeAutor());
                validarAvaliacao(existing);
                log.info("Avaliação ID={} atualizada com sucesso.", id);
                return repo.save(existing);
            }).orElseThrow(() -> {
                log.warn("Avaliação não encontrada para atualização ID={}", id);
                return new BusinessException("Avaliação não encontrada");
            });
        } catch (DataIntegrityViolationException e) {
            log.error("Violação de integridade ao atualizar Avaliação ID={}", id, e);
            throw new BusinessException("Dados da avaliação inválidos.", e);
        } catch (RuntimeException e) {
            log.error("Erro inesperado ao atualizar Avaliação ID={}", id, e);
            throw new BusinessException("Erro ao atualizar avaliação.");
        }
    }

    // =================== FIND BY ID ===================
    @Override
    public Avaliacao findById(Long id) {
        validarIdAvaliacao(id);

        try {
            return repo.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Avaliação não encontrada para ID={}", id);
                        return new BusinessException("Avaliação não encontrada.");
                    });
        } catch (RuntimeException e) {
            log.error("Erro inesperado ao buscar Avaliação por ID={}", id, e);
            throw new BusinessException("Erro ao buscar avaliação por ID.");
        }
    }

    // =================== VALIDAÇÕES ===================
    private void validarAvaliacao(Avaliacao avaliacao) {
        if (avaliacao == null) {
            log.warn("Avaliação recebida é nula.");
            throw new BusinessException("A avaliação não pode ser nula.");
        }

        if (!StringUtils.hasText(avaliacao.getNomeAutor())) {
            log.warn("Nome do autor da avaliação está vazio.");
            throw new BusinessException("O nome do Autor da avaliação é obrigatório.");
        }

        if (avaliacao.getEstrelas() < 1 || avaliacao.getEstrelas() > 5) {
            log.warn("Número de estrelas inválido: {}", avaliacao.getEstrelas());
            throw new BusinessException("A avaliação deve ter entre 1 e 5 estrelas.");
        }

        if (!StringUtils.hasText(avaliacao.getDescricao())) {
            log.info("Descrição da avaliação está vazia. Permitido.");
        }
    }

    private void validarIdPonto(Long pontoId) {
        if (pontoId == null || pontoId <= 0) {
            log.warn("ID de ponto turístico inválido: {}", pontoId);
            throw new BusinessException("ID de ponto turístico inválido.");
        }
    }

    private void validarIdAvaliacao(Long id) {
        if (id == null || id <= 0) {
            log.warn("ID de Avaliação inválido: {}", id);
            throw new BusinessException("ID de avaliação inválido.");
        }
    }
}