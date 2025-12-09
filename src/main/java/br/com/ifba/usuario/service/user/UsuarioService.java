package br.com.ifba.usuario.service.user;


import br.com.ifba.infrastructure.exception.BusinessException;
import br.com.ifba.pessoa.other_users.usuariocomum.entity.UsuarioComum;
import br.com.ifba.pessoa.other_users.usuariocomum.repository.UsuarioComumRepository;
import br.com.ifba.solicitacao.entity.Solicitacao;
import br.com.ifba.usuario.dto.user.UsuarioCadastroDTO;
import br.com.ifba.usuario.entity.TipoUsuario;
import br.com.ifba.usuario.entity.Usuario;
import br.com.ifba.usuario.repository.TipoUsuarioRepository;
import br.com.ifba.usuario.repository.UsuarioRepository;
import br.com.ifba.util.StringUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author User
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class UsuarioService implements UsuarioIService {

    private final UsuarioRepository UserRepo;
    private final TipoUsuarioRepository TipoRepo;
    private final UsuarioComumRepository UsuarioComumRepo;

    @Override
    @Transactional
    public Usuario save(UsuarioCadastroDTO dto)     {
        // 1. Instancia e popula a PESSOA (UsuarioComum)
        UsuarioComum novaPessoa = new UsuarioComum();
        novaPessoa.setCpf(dto.getCpf());
        novaPessoa.setNome(dto.getNome());
        novaPessoa.setDataCadastro(LocalDateTime.now());
        novaPessoa.setTelefone(dto.getTelefone());
        UsuarioComumRepo.save(novaPessoa);
        // novaPessoa.set... outros dados de pessoa

        // 2. Instancia o USUÁRIO
        Usuario usuario = new Usuario();
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(dto.getSenha()); // Lembre-se de encodar a senha!
        usuario.setAtivo(true);
        usuario.setUltimoLogin(LocalDate.now());

        // 3. Busca o TIPO (que já existe)
        TipoUsuario tipo = TipoRepo.findById(dto.getTipoUsuarioId())
                .orElseThrow(() -> new BusinessException("Tipo não encontrado"));
        usuario.setTipo(tipo);

        // 4. VINCULA A PESSOA AO USUÁRIO
        usuario.setPessoa(novaPessoa);

        // 5. SALVA TUDO
        // Graças ao CascadeType.ALL na entidade Usuario,
        // isso vai salvar o UsuarioComum na tabela dele e o Usuario na tabela dele.
        return UserRepo.save(usuario);
    }

    @Override
    @Transactional
    public Usuario save(Usuario usuario) {
        // Se precisar de validação final, chame aqui:
        // validarUsuario(usuario);

        try {
            // Aqui acontece o INSERT real no banco
            return UserRepo.save(usuario);
        } catch (DataIntegrityViolationException e) {
            // Tratamento centralizado de erros de banco (ex: email duplicado)
            log.error("Violação de integridade ao salvar Usuário: {}", usuario.getEmail(), e);
            throw new BusinessException("Erro de integridade: Dados duplicados ou inválidos.", e);
        } catch (Exception e) {
            log.error("Erro inesperado ao salvar Usuário.", e);
            throw new BusinessException("Erro ao processar o salvamento do usuário.");
        }
    }

    @Override
    public void delete(Long id) {
        if (id == null || id <= 0) {
            log.warn("Tentativa de excluir Usuário com ID inválido: {}", id);
            throw new BusinessException("ID de Usuário inválido.");
        }

        try {
            UserRepo.deleteById(id);
            UserRepo.flush();
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
    public List<Usuario> findAll() {
        try {
            return UserRepo.findAll();
        } catch (RuntimeException e) {
            log.error("Erro ao buscar todos os Usuário.", e);
            throw new BusinessException("Erro ao buscar todos os Usuário.");
        }
    }

    @Override
    public Usuario findById(Long id) {
        if (id == null || id <= 0) {
            log.warn("ID inválido fornecido para busca: {}", id);
            throw new BusinessException("ID inválido para busca.");
        }

        try {
            return UserRepo.findById(id)
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
    public void validarUsuario(Usuario user) {
        if (user == null) {
            log.warn("Usuário recebido é nulo.");
            throw new BusinessException("O Usuário não pode ser nulo.");
        }

        if (StringUtil.isNullOrEmpty(user.getPessoa().getNome())) {
            log.warn("Nome do Usuário é nulo ou vazio.");
            throw new BusinessException("O nome do Usuário é obrigatório.");
        }

        if (!StringUtil.hasValidLength(user.getPessoa().getNome(), 3, 30)) {
            log.warn("Nome do Usuário fora do tamanho permitido: '{}'", user.getPessoa().getNome());
            throw new BusinessException("O nome do Usuário deve ter entre 3 e 30 caracteres.");
        }
    }

    @Override
    public List<Solicitacao> findSolicitacoesByUsuarioId(Long usuarioId) {
        log.info("Buscando solicitações do usuário com ID: {}", usuarioId);

        try {
            if (usuarioId == null || usuarioId <= 0) {
                log.warn("ID inválido informado: {}", usuarioId);
                throw new BusinessException("ID do usuário inválido.");
            }

            List<Solicitacao> solicitacoes = UserRepo.findSolicitacoesByUsuarioId(usuarioId);

            if (solicitacoes == null || solicitacoes.isEmpty()) {
                log.warn("Nenhuma solicitação encontrada para o usuário ID: {}", usuarioId);
                throw new BusinessException("Nenhuma solicitação encontrada para este usuário.");
            }

            log.info("Solicitações encontradas: {} itens", solicitacoes.size());
            return solicitacoes;

        } catch (BusinessException e) {
            log.error("Erro de regra de negócio ao buscar solicitações: {}", e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("Erro inesperado ao buscar solicitações do usuário ID {}: {}", usuarioId, e.getMessage(), e);
            throw new BusinessException("Erro ao buscar solicitações. Tente novamente.");
        }
    }

    @Override
    public Usuario findByPessoaId(Long pessoaId) {
        return UserRepo.findByPessoaId(pessoaId);
    }

}
