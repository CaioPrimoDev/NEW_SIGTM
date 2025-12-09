package br.com.ifba.pessoa.other_users.usuariocomum.service;

import br.com.ifba.infrastructure.exception.BusinessException;
import br.com.ifba.pessoa.other_users.usuariocomum.entity.UsuarioComum;
import br.com.ifba.pessoa.other_users.usuariocomum.repository.UsuarioComumRepository;
import br.com.ifba.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UsuarioComumService implements UsuarioComumIService {

    private final UsuarioComumRepository repo;

    @Override
    public boolean save(UsuarioComum usuario) {
        validarUsuarioComum(usuario);

        try {
            repo.save(usuario);
            return true;

        } catch (DataIntegrityViolationException e) {
            log.error("Violação de integridade ao salvar UsuarioComum: CPF {}", usuario.getCpf(), e);
            throw new BusinessException("Já existe um usuário comum com esse CPF ou dados inválidos.", e);

        } catch (RuntimeException e) {
            log.error("Erro inesperado ao salvar UsuarioComum.", e);
            throw new BusinessException("Erro ao salvar Usuário Comum.");
        }
    }

    @Override
    public void delete(Long id) {
        if (id == null || id <= 0) {
            log.warn("Tentativa de excluir UsuarioComum com ID inválido: {}", id);
            throw new BusinessException("ID de Usuário Comum inválido.");
        }

        try {
            repo.deleteById(id);
            repo.flush();

        } catch (EmptyResultDataAccessException e) {
            log.error("Tentativa de exclusão de UsuarioComum inexistente (ID: {}).", id, e);
            throw new BusinessException("Usuário Comum não encontrado para exclusão.", e);

        } catch (RuntimeException e) {
            log.error("Erro inesperado ao excluir UsuarioComum.", e);
            throw new BusinessException("Erro ao excluir Usuário Comum.");
        }
    }

    @Override
    public List<UsuarioComum> findAll() {
        try {
            return repo.findAll();

        } catch (RuntimeException e) {
            log.error("Erro ao buscar todos os Usuários Comuns.", e);
            throw new BusinessException("Erro ao buscar todos os Usuários Comuns.");
        }
    }
    private void validarUsuarioComum(UsuarioComum usuario) {
        if (usuario == null) {
            log.warn("UsuarioComum recebido é nulo.");
            throw new BusinessException("Usuário Comum não pode ser nulo.");
        }

        // Validações herdadas de Pessoa (nome, telefone, etc.)
        if (StringUtil.isNullOrEmpty(usuario.getNome())) {
            log.warn("Nome do UsuarioComum é nulo ou vazio.");
            throw new BusinessException("O nome do Usuário Comum é obrigatório.");
        }

        if (!StringUtil.hasValidLength(usuario.getNome(), 3, 30)) {
            log.warn("Nome do UsuarioComum fora do tamanho permitido: '{}'", usuario.getNome());
            throw new BusinessException("O nome deve ter entre 3 e 30 caracteres.");
        }

        if (!StringUtil.isValidTelefone(usuario.getTelefone())) {
            log.warn("Telefone inválido para UsuarioComum: '{}'", usuario.getTelefone());
            throw new BusinessException("Telefone inválido.");
        }

        // Validação específica
        if (!StringUtil.isCpfOuCnpjValido(usuario.getCpf())) {
            log.warn("CPF inválido para UsuarioComum: '{}'", usuario.getCpf());
            throw new BusinessException("O CPF informado é inválido.");
        }
    }

    @Override
    public UsuarioComum findById(Long id) {
        if (id == null || id <= 0) {
            log.warn("ID inválido fornecido para busca de UsuarioComum: {}", id);
            throw new BusinessException("ID inválido para busca.");
        }

        try {
            return repo.findById(id)
                    .orElseThrow(() -> {
                        log.warn("UsuarioComum não encontrado para ID: {}", id);
                        return new BusinessException("Usuário Comum não encontrado.");
                    });

        } catch (RuntimeException e) {
            log.error("Erro inesperado ao buscar UsuarioComum por ID.", e);
            throw new BusinessException("Erro ao buscar Usuário Comum por ID.");
        }
    }

    @Override
    public List<UsuarioComum> findByNomeContainingIgnoreCase(String nome) {
        if (!StringUtils.hasText(nome)) {
            return Collections.emptyList();
        }

        List<UsuarioComum> resultado = repo.findByNomeContainingIgnoreCase(nome);

        if (resultado.isEmpty()) {
            log.info("Nenhum UsuarioComum encontrado para o termo: {}", nome);
        }

        return resultado;
    }
}
