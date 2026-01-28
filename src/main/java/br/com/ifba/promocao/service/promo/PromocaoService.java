package br.com.ifba.promocao.service.promo;

import br.com.ifba.infrastructure.exception.BusinessException;
import br.com.ifba.promocao.entity.Promocao;
import br.com.ifba.promocao.entity.TipoPromocao;
import br.com.ifba.promocao.repository.PromocaoRepository;
import br.com.ifba.promocao.repository.TipoPromocaoRepository;
import br.com.ifba.usuario.entity.Usuario;
import br.com.ifba.util.StringUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromocaoService implements PromocaoIService {

    private final PromocaoRepository promocaoRepository;
    private final TipoPromocaoRepository tipoPromocaoRepository;

    private static final Logger log = LoggerFactory.getLogger(PromocaoService.class);

    @Override
    public Promocao save(Promocao promocao) {
        log.info("Salvando promoção: {}", promocao.getTitulo());

        if (promocao == null) {
            throw new BusinessException("Dados da promoção não informados.");
        }

        Usuario usuarioPadrao = new Usuario();
        usuarioPadrao.setId(1L);

        promocao.setUsuarioCriador(usuarioPadrao);

        validatePromocao(promocao);
        return promocaoRepository.save(promocao);
    }

    @Override
    public Promocao update(Promocao promocao) {
        log.info("Atualizando promoção ID {}", promocao.getId());

        Promocao existente = findById(promocao.getId());

        // mantém o usuário criador original
        promocao.setUsuarioCriador(existente.getUsuarioCriador());

        validatePromocao(promocao);
        return promocaoRepository.save(promocao);
    }

    @Override
    public void delete(Promocao promocao) {
        promocaoRepository.delete(promocao);
    }

    @Override
    public List<Promocao> findAll() {
        return promocaoRepository.findAll();
    }

    @Override
    public Promocao findById(Long id) {
        return promocaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Promoção não encontrada."));
    }

    public List<Promocao> filtrarPromocoes(String termo, String tipo) {
        if ("TODOS".equalsIgnoreCase(tipo)) {
            return (termo == null || termo.isBlank())
                    ? promocaoRepository.findAll()
                    : promocaoRepository.findByTituloContainingIgnoreCase(termo);
        }

        List<TipoPromocao> tipos = tipoPromocaoRepository.findByTitulo(tipo);
        if (tipos.isEmpty()) {
            throw new EntityNotFoundException("Tipo de promoção não encontrado.");
        }

        TipoPromocao tipoPromocao = tipos.getFirst();

        return (termo == null || termo.isBlank())
                ? promocaoRepository.findByTipo(tipoPromocao)
                : promocaoRepository.findByTipoAndTituloContainingIgnoreCase(tipoPromocao, termo);
    }

    // =============================
    // VALIDAÇÕES
    // =============================
    private void validatePromocao(Promocao promocao) {
        validateTitulo(promocao.getTitulo());
        validateDescricao(promocao.getDescricao());
        validateDatas(promocao.getDataInicio(), promocao.getDataTermino());
        validateRegras(promocao.getRegras());
    }

    private void validateTitulo(String titulo) {
        if (StringUtil.isNullOrEmpty(titulo)) {
            throw new BusinessException("Título é obrigatório.");
        }
        if (!StringUtil.hasValidLength(titulo, 3, 50)) {
            throw new BusinessException("Título deve ter entre 3 e 50 caracteres.");
        }
    }

    private void validateDescricao(String descricao) {
        if (StringUtil.isNullOrEmpty(descricao)) {
            throw new BusinessException("Descrição é obrigatória.");
        }
        if (!StringUtil.hasValidLength(descricao, 10, 200)) {
            throw new BusinessException("Descrição deve ter entre 10 e 200 caracteres.");
        }
    }

    private void validateDatas(Date inicio, Date termino) {
        if (inicio == null || termino == null) {
            throw new BusinessException("Datas são obrigatórias.");
        }
        if (termino.before(inicio)) {
            throw new BusinessException("Data de término deve ser posterior à data de início.");
        }
    }

    private void validateRegras(String regras) {
        if (!StringUtil.isNullOrEmpty(regras) &&
            !StringUtil.hasValidLength(regras, 0, 200)) {
            throw new BusinessException("Regras não podem exceder 200 caracteres.");
        }
    }
}