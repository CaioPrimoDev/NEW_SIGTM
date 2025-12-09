package br.com.ifba.pessoa.other_users.gestor.service;

import br.com.ifba.infrastructure.exception.BusinessException;
import br.com.ifba.pessoa.other_users.gestor.entity.Gestor;
import br.com.ifba.pessoa.other_users.gestor.repository.GestorRepository;
import br.com.ifba.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author CaioP
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GestorService implements GestorIService {

    private final GestorRepository repo;

    @Override
    public boolean save(Gestor user) {
        validarGestor(user);
        try {
            repo.save(user);
            return true;
        } catch (DataIntegrityViolationException e) {
            // Violação de constraints do banco (ex: unique ou not null)
            log.error("Violação de integridade ao salvar Usuário: {}", user.getNome(), e);
            throw new BusinessException("Já existe um usuário com esse nome ou dados inválidos.", e);
        } catch (RuntimeException e) {
            // Falha inesperada
            log.error("Erro inesperado ao salvar Usuário.", e);
            throw new BusinessException("Erro ao salvar Usuário.");
        }
    }

    @Override
    public void delete(Long id) {
        if (id == null || id <= 0) {
            log.warn("Tentativa de excluir Usuário com ID inválido: {}", id);
            throw new BusinessException("ID de Usuário inválido.");
        }

        try {
            repo.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            // ID não encontrado no banco
            log.error("Tentativa de exclusão de Usuário inexistente (ID: {}).", id, e);
            throw new BusinessException("Usuário não encontrado para exclusão.", e);
        } catch (RuntimeException e) {
            log.error("Erro inesperado ao excluir Usuário.", e);
            throw new BusinessException("Erro ao excluir Usuário.");
        }
    }

    @Override
    public List<Gestor> findAll() {
        try {
            return repo.findAll();
        } catch (RuntimeException e) {
            log.error("Erro ao buscar todos os Usuário.", e);
            throw new BusinessException("Erro ao buscar todos os Usuário.");
        }
    }

    @Override
    public Gestor findById(Long id) {
        if (id == null || id <= 0) {
            log.warn("ID inválido fornecido para busca: {}", id);
            throw new BusinessException("ID inválido para busca.");
        }

        try {
            return repo.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Usuário não encontrado para ID: {}", id);
                        return new BusinessException("Usuário não encontrado.");
                    });
        } catch (RuntimeException e) {
            log.error("Erro inesperado ao buscar Usuário por ID.", e);
            throw new BusinessException("Erro ao buscar Usuário por ID.");
        }
    }

    @Override
    public List<Gestor> findByNomeContainingIgnoreCase(String nome) {
        if (!StringUtils.hasText(nome)) {
            return Collections.emptyList();
        }

        List<Gestor> resultado = repo.findByNomeContainingIgnoreCase(nome);

        if (resultado.isEmpty()) {
            log.info("Nenhum usuário encontrado para o termo: {}", nome);
        }

        return resultado;
    }

    @Override
    public void validarGestor(Gestor user) {
        if (user == null) {
            log.warn("Usuário recebido é nulo.");
            throw new BusinessException("O Usuário não pode ser nulo.");
        }

        if (StringUtil.isNullOrEmpty(user.getNome())) {
            log.warn("Nome do Usuário é nulo ou vazio.");
            throw new BusinessException("O nome do Usuário é obrigatório.");
        }

        if (!StringUtil.hasValidLength(user.getNome(), 3, 30)) {
            log.warn("Nome do Usuário fora do tamanho permitido: '{}'", user.getNome());
            throw new BusinessException("O nome do Usuário deve ter entre 3 e 30 caracteres.");
        }

        if (StringUtil.isNullOrEmpty(user.getMatricula())) {
            throw new BusinessException("Cargo do Gestor é obrigatória.");
        }

        if (StringUtil.isNullOrEmpty(user.getCargo())) {
            throw new BusinessException("Cargo do Gestor é obrigatória.");
        }

    }
}
