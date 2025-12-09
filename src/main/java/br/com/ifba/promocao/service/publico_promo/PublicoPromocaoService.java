package br.com.ifba.promocao.service.publico_promo;

import br.com.ifba.infrastructure.exception.BusinessException;
import br.com.ifba.promocao.entity.PublicoPromocao;
import br.com.ifba.promocao.repository.PublicoPromocaoRepository;
import br.com.ifba.sessao.entity.UsuarioSession;
import br.com.ifba.usuario.entity.Usuario;
import br.com.ifba.usuario.service.user.UsuarioService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author Joice
 */
@Service
@RequiredArgsConstructor
public class PublicoPromocaoService implements PublicoPromocaoIService {

    private final PublicoPromocaoRepository publicoPromocaoRepository;

    private final UsuarioService usuarioService;

    private final UsuarioSession usuarioSession;

    private Usuario getUsuarioLogado() {
        Usuario usuarioLogado = usuarioSession.getUsuarioLogado();
        if (usuarioLogado == null) {
            throw new BusinessException("Usuário não autenticado");
        }
        return usuarioLogado;
    }

    private void validarPermissaoCadastro() {
        Usuario usuario = getUsuarioLogado();
        String tipoUsuario = usuarioService.findById(usuario.getPessoa().getId())
                .getTipo().getNome().toLowerCase();
        if (!tipoUsuario.equals("parceiro") && !tipoUsuario.equals("gestor")) {
            throw new BusinessException("Apenas parceiros e gestores podem cadastrar públicos");
        }
    }

    private void validarPermissaoVisualizacao() {
        getUsuarioLogado(); // apenas garante que o usuário está logado
    }

    @Override
    public PublicoPromocao save(PublicoPromocao publicoPromocao) {

        if (!usuarioSession.isLogado()) {
            throw new BusinessException("Você precisa estar logado para criar promoções.");
        }

        validarPermissaoCadastro();

        if (publicoPromocao.getNome() == null || publicoPromocao.getNome().trim().isEmpty()) {
            throw new BusinessException("Nome não pode ser vazio");
        }

        if (publicoPromocao.getDescricao() == null || publicoPromocao.getDescricao().trim().isEmpty()) {
            throw new BusinessException("Descrição não pode ser vazia");
        }

        if (publicoPromocao.getFaixaEtaria() == null || publicoPromocao.getFaixaEtaria().trim().isEmpty()) {
            throw new BusinessException("Faixa etária não pode ser vazia");
        }

        if (publicoPromocao.getInteresse() == null || publicoPromocao.getInteresse().trim().isEmpty()) {
            throw new BusinessException("Interesse não pode ser vazio");
        }

        publicoPromocao.setUsuarioCadastro(getUsuarioLogado());
        return publicoPromocaoRepository.save(publicoPromocao);
    }

    @Override
    public PublicoPromocao update(PublicoPromocao publicoPromocao) {
        validarPermissaoCadastro();

        if (!publicoPromocaoRepository.existsById(publicoPromocao.getId())) {
            throw new EntityNotFoundException("Público não encontrado");
        }

        PublicoPromocao existente = findById(publicoPromocao.getId());
        Usuario usuarioLogado = getUsuarioLogado();
        String tipoUsuario = usuarioLogado.getTipo().getNome().toLowerCase();
        boolean isGestor = tipoUsuario.equals("gestor");
        boolean isDono = existente.getUsuarioCadastro().getPessoa().getId().equals(usuarioLogado.getPessoa().getId());

        if (!isDono && !isGestor) {
            throw new BusinessException("Apenas o criador ou gestores podem editar este público");
        }

        publicoPromocao.setUsuarioCadastro(existente.getUsuarioCadastro()); // mantém o dono
        return publicoPromocaoRepository.save(publicoPromocao);
    }

    @Override
    public void delete(Long id) {
        validarPermissaoCadastro();

        PublicoPromocao publico = findById(id);
        Usuario usuarioLogado = getUsuarioLogado();
        String tipoUsuario = usuarioLogado.getTipo().getNome().toLowerCase();
        boolean isGestor = tipoUsuario.equals("gestor");
        boolean isDono = publico.getUsuarioCadastro().getPessoa().getId().equals(usuarioLogado.getPessoa().getId());

        if (!isDono && !isGestor) {
            throw new BusinessException("Apenas o criador ou gestores podem excluir este público");
        }

        publicoPromocaoRepository.delete(publico);
    }

    @Override
    public List<PublicoPromocao> findAll() {
        return publicoPromocaoRepository.findAll();
    }

    @Override
    public PublicoPromocao findById(Long id) {
        return publicoPromocaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Público não encontrado"));
    }
}
