package br.com.ifba.pessoa.service;


import br.com.ifba.infrastructure.exception.BusinessException;
import br.com.ifba.pessoa.entity.Pessoa;
import br.com.ifba.pessoa.repository.PessoaRepository;
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
public class PessoaService implements PessoaIService {

    private final PessoaRepository repo;

    @Override
    public boolean save(Pessoa pessoa) {
        validarPessoa(pessoa);
        try {
            repo.save(pessoa);
            return true;
        } catch (DataIntegrityViolationException e) {
            // Violação de constraints do banco (ex: unique ou not null)
            log.error("Violação de integridade ao salvar Pessoa: {}", pessoa.getNome(), e);
            throw new BusinessException("Já existe uma pessoa com esse email ou dados inválidos.", e);
        } catch (RuntimeException e) {
            // Falha inesperada
            log.error("Erro inesperado ao salvar pessoa.", e);
            throw new BusinessException("Erro ao salvar pessoa.");
        }
    }

    @Override
    public void delete(Long id) {
        if (id == null || id <= 0) {
            log.warn("Tentativa de excluir Pessoa com ID inválido: {}", id);
            throw new BusinessException("ID de Pessoa inválido.");
        }

        try {
            repo.deleteById(id);
            repo.flush();
        } catch (EmptyResultDataAccessException e) {
            // ID não encontrado no banco
            log.error("Tentativa de exclusão de Pessoa inexistente (ID: {}).", id, e);
            throw new BusinessException("Pessoa não encontrado para exclusão.", e);
        } catch (RuntimeException e) {
            log.error("Erro inesperado ao excluir Pessoa.", e);
            throw new BusinessException("Erro ao excluir Pessoa.");
        }
    }

    @Override
    public List<Pessoa> findAll() {
        try {
            return repo.findAll();
        } catch (RuntimeException e) {
            log.error("Erro ao buscar todos as pessoas.", e);
            throw new BusinessException("Erro ao buscar todos as Pessoas.");
        }
    }

    @Override
    public Pessoa findById(Long id) {
        if (id == null || id <= 0) {
            log.warn("ID inválido fornecido para busca: {}", id);
            throw new BusinessException("ID inválido para busca.");
        }

        try {
            return repo.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Pessoa não encontrado para ID: {}", id);
                        return new BusinessException("Pessoa não encontrado.");
                    });
        } catch (RuntimeException e) {
            log.error("Erro inesperado ao buscar Pessoa por ID.", e);
            throw new BusinessException("Erro ao buscar Pessoa por ID.");
        }
    }

    @Override
    public List<Pessoa> findByNomeContainingIgnoreCase(String nome) {
        if (!StringUtils.hasText(nome)) {
            return Collections.emptyList();
        }

        List<Pessoa> resultado = repo.findByNomeContainingIgnoreCase(nome);

        if (resultado.isEmpty()) {
            log.info("Nenhum pessoa encontrado para o termo: {}", nome);
        }

        return resultado;
    }
    private void validarPessoa(Pessoa pessoa) {
        if (pessoa == null) {
            log.warn("Pessoa recebido é nulo.");
            throw new BusinessException("Pessoa não pode ser nulo.");
        }

        if (StringUtil.isNullOrEmpty(pessoa.getNome())) {
            log.warn("Nome do Usuário é nulo ou vazio.");
            throw new BusinessException("O nome da pessoa é obrigatório.");
        }

        if (!StringUtil.hasValidLength(pessoa.getNome(), 3, 30)) {
            log.warn("Nome da Pessoa fora do tamanho permitido: '{}'", pessoa.getNome());
            throw new BusinessException("O nome da Pessoa deve ter entre 3 e 30 caracteres.");
        }

        if (!StringUtil.isValidTelefone(pessoa.getTelefone())) {
            log.warn("Telefone da Pessoa invãlido: '{}'", pessoa.getNome());
            throw new BusinessException("O telefone da Pessoa deve possuir apenas números, e seguir as normas nacionais/internacionais.");
        }
    }

}
