package br.com.ifba.pessoa.other_users.parceiro.service;

import br.com.ifba.infrastructure.exception.BusinessException;
import br.com.ifba.pessoa.entity.Pessoa;
import br.com.ifba.pessoa.other_users.parceiro.entity.Parceiro;
import br.com.ifba.pessoa.other_users.parceiro.repository.ParceiroRepository;
import br.com.ifba.pessoa.other_users.usuariocomum.entity.UsuarioComum;
import br.com.ifba.pessoa.other_users.usuariocomum.repository.UsuarioComumRepository;
import br.com.ifba.pessoa.service.PessoaIService;
import br.com.ifba.solicitacao.entity.Solicitacao;
import br.com.ifba.solicitacao.service.SolicitacaoIService;
import br.com.ifba.usuario.entity.TipoUsuario;
import br.com.ifba.usuario.entity.Usuario;
import br.com.ifba.usuario.service.tipo_user.TipoUsuarioIService;
import br.com.ifba.usuario.service.user.UsuarioIService;
import br.com.ifba.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author CaioP
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ParceiroService implements ParceiroIService {

    private final ParceiroRepository parceiroRepository;

    private final UsuarioIService usuarioService;

    private final SolicitacaoIService solicitacaoService;

    private final TipoUsuarioIService tipoUsuarioService;

    private final UsuarioComumRepository usuarioComumRepository;

    private final PessoaIService pessoaService;

    // ParceiroService não pode chamar ele mesmo, se não ocorre um loop beam
    //private final ParceiroIService parceiroService;

    @Override
    public boolean save(Parceiro user) {
        validarParceiro(user);
        try {
            parceiroRepository.save(user);
            return true;
        } catch (DataIntegrityViolationException e) {
            // Violação de constraints do banco (ex: unique ou not null)
            log.error("Violação de integridade ao salvar Parceiro: {}", user.getNome(), e);
            throw new BusinessException("Já existe um Parceiro com esse nome ou dados inválidos.", e);
        } catch (RuntimeException e) {
            // Falha inesperada
            log.error("Erro inesperado ao salvar Parceiro.", e);
            throw new BusinessException("Erro ao salvar Parceiro.");
        }
    }

    @Override
    public void delete(Long id) {
        if (id == null || id <= 0) {
            log.warn("Tentativa de excluir Parceiro com ID inválido: {}", id);
            throw new BusinessException("ID de Usuário inválido.");
        }

        try {
            parceiroRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            // ID não encontrado no banco
            log.error("Tentativa de exclusão de Parceiro inexistente (ID: {}).", id, e);
            throw new BusinessException("Parceiro não encontrado para exclusão.", e);
        } catch (RuntimeException e) {
            log.error("Erro inesperado ao excluir Parceiro.", e);
            throw new BusinessException("Erro ao excluir Parceiro.");
        }
    }

    @Override
    public List<Parceiro> findAll() {
        try {
            return parceiroRepository.findAll();
        } catch (RuntimeException e) {
            log.error("Erro ao buscar todos os Parceiro.", e);
            throw new BusinessException("Erro ao buscar todos os Parceiro.");
        }
    }

    @Override
    public Parceiro findById(Long id) {
        if (id == null || id <= 0) {
            log.warn("ID inválido fornecido para busca: {}", id);
            throw new BusinessException("ID inválido para busca.");
        }

        try {
            return parceiroRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Parceiro não encontrado para ID: {}", id);
                        return new BusinessException("Usuário não encontrado.");
                    });
        } catch (RuntimeException e) {
            log.error("Erro inesperado ao buscar Parceiro por ID.", e);
            throw new BusinessException("Erro ao buscar Parceiro por ID.");
        }
    }

    @Override
    public List<Parceiro> findByNomeContainingIgnoreCase(String nome) {
        if (!StringUtils.hasText(nome)) {
            return Collections.emptyList();
        }

        List<Parceiro> resultado = parceiroRepository.findByNomeContainingIgnoreCase(nome);

        if (resultado.isEmpty()) {
            log.info("Nenhum Parceiro encontrado para o termo: {}", nome);
        }

        return resultado;
    }

    @Override
    public Optional<Parceiro> findByCnpj(String cnpj) {
        log.info("Iniciando busca por Parceiro com CNPJ: {}", cnpj);
        // 1. Verificação de entrada
        if (cnpj == null || cnpj.trim().isEmpty()) {
            log.error("CNPJ fornecido para busca é nulo ou vazio.");
            throw new BusinessException("O CNPJ não pode ser nulo ou vazio para a busca.");
        }


        Optional<Parceiro> parceiro; // Inicializa com Optional vazio
        try {
            parceiro = parceiroRepository.findByCnpj(cnpj);

            if (parceiro.isPresent()) {
                log.info("Busca por CNPJ: {} concluída. Parceiro encontrado.", cnpj);
            } else {
                log.warn("Nenhum Parceiro encontrado para o CNPJ: {}", cnpj);
            }
        } catch (org.springframework.dao.DataAccessException e) {
            log.error("Erro de acesso a dados ao buscar Parceiro por CNPJ {}: {}", cnpj, e.getMessage(), e);
            throw new BusinessException("Erro ao buscar usuário pelo CNPJ. Tente novamente mais tarde.", e);
        } catch (Exception e) {
            log.error("Erro inesperado ao buscar Parceiro por CNPJ {}: {}", cnpj, e.getMessage(), e);
            throw new BusinessException("Ocorreu um erro interno ao buscar Parceiro pelo CNPJ.");
        }
        return parceiro;
    }

    @Override
    public void validarParceiro(Parceiro user) {
        if (user == null) {
            log.warn("Parceiro recebido é nulo.");
            throw new BusinessException("O Parceiro não pode ser nulo.");
        }

            /*if(!StringUtil.isCnpjValido(user.getCnpj()) || StringUtil.isNullOrEmpty(user.getCnpj())) {
                log.warn("CNPJ vazio ou inválido");
                throw new RegraNegocioException("Um CNPJ válido é obrigatório");
            }*/

        if(StringUtil.isNullOrEmpty(user.getNomeEmpresa())) {
            log.warn("O nome da empresa vazio ou inválido");
            throw new BusinessException("O nome da empresa é obrigatório");
        }
    }

    @Override
    @Transactional // IMPORTANTE: Tudo deve ocorrer na mesma transação
    public Parceiro tornarParceiro(Usuario usuario) {
        if (usuario == null || usuario.getPessoa() == null) {
            throw new BusinessException("Usuário inválido.");
        }

        // 1. Validar e Pegar Dados
        Solicitacao dadosSolicitacao = solicitacaoService.findByUsuario(usuario)
                .orElseThrow(() -> new BusinessException("Nenhuma solicitação encontrada."));

        Pessoa pessoaAntiga = usuario.getPessoa();
        if (!(pessoaAntiga instanceof UsuarioComum)) {
            throw new BusinessException("Usuário já é parceiro ou de outro tipo.");
        }
        UsuarioComum comumAntigo = (UsuarioComum) pessoaAntiga;

        // Guardar dados importantes antes de deletar (Memória)
        String nome = comumAntigo.getNome();
        String telefone = comumAntigo.getTelefone();
        // String cpf = comumAntigo.getCpf(); // Se tiver CPF

        // 2. DESVINCULAR (Passo Crítico para evitar erro de FK)
        usuario.setPessoa(null);
        usuarioService.saveAndFlush(usuario); // Força a atualização no banco agora

        // 3. DELETAR O USUÁRIO COMUM (Remove da tabela Pessoa e UsuarioComum)
        // Você precisa ter o repository do UsuarioComum injetado
        usuarioComumRepository.delete(comumAntigo);
        usuarioComumRepository.flush(); // Garante que o ID foi liberado (ou deletado)

        // 4. CRIAR O NOVO PARCEIRO (Geralmente terá um NOVO ID gerado automaticamente)
        Parceiro novoParceiro = new Parceiro();
        novoParceiro.setNome(nome);
        novoParceiro.setTelefone(telefone);
        novoParceiro.setCnpj(dadosSolicitacao.getCnpj());
        novoParceiro.setNomeEmpresa(dadosSolicitacao.getNomeEmpresa());
        // Se quiser tentar forçar o mesmo ID, seria complexo.
        // Deixe o banco gerar um novo ID para evitar conflitos.

        novoParceiro = parceiroRepository.save(novoParceiro); // Salva e gera ID

        // 5. REVINCULAR AO USUÁRIO
        TipoUsuario tipoParceiro = tipoUsuarioService.findByNome("PARCEIRO");

        usuario.setTipo(tipoParceiro);
        usuario.setPessoa(novoParceiro);

        // Limpeza final
        solicitacaoService.delete(dadosSolicitacao.getId());

        return (Parceiro) usuarioService.save(usuario).getPessoa();
    }


    @Override
    public Usuario removerParceiria(Parceiro parceiro) {
        if (parceiro == null) {
            throw new BusinessException("Parceiro inválido.");
        }

        // Buscar tipo de usuário comum
        TipoUsuario tipoComum = tipoUsuarioService.findByNome("USUARIO_COMUM");

        // Buscar o usuário associado ao Parceiro
        Usuario usuario = usuarioService.findByPessoaId(parceiro.getId());
        if (usuario == null) {
            throw new BusinessException("Usuário não encontrado para o parceiro informado.");
        }

        // Criar nova Pessoa
        Pessoa pessoaAntiga = new Pessoa();
        pessoaAntiga.setNome(parceiro.getNome());
        pessoaAntiga.setTelefone(parceiro.getTelefone());

        // Persistir nova Pessoa (não usamos retorno)
        boolean pessoaSalva = pessoaService.save(pessoaAntiga);
        if (!pessoaSalva) {
            throw new BusinessException("Erro ao salvar dados da pessoa.");
        }

        // Atualizar Usuario
        usuario.setTipo(tipoComum);
        usuario.setPessoa(pessoaAntiga);
        boolean usuarioSalvo = usuarioService.save(usuario).isAtivo();
        if (!usuarioSalvo) {
            throw new BusinessException("Erro ao atualizar usuário.");
        }

        // Remover solicitação se existir
        solicitacaoService.findByUsuario(usuario)
                .ifPresent(s -> solicitacaoService.delete(s.getId()));

        // Remover o Parceiro antigo
        this.delete(parceiro.getId());

        return usuario;
    }
}