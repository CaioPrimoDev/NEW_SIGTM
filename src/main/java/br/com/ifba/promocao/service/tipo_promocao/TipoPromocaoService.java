package br.com.ifba.promocao.service.tipo_promocao;

import br.com.ifba.infrastructure.exception.BusinessException;
import br.com.ifba.promocao.entity.TipoPromocao;
import br.com.ifba.promocao.repository.TipoPromocaoRepository;
import br.com.ifba.usuario.entity.Usuario;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoPromocaoService implements TipoPromocaoIService {

    private final TipoPromocaoRepository tipoPromocaoRepository;

    @Override
    public TipoPromocao save(TipoPromocao tipoPromocao) {

        if (tipoPromocao == null) {
            throw new BusinessException("Dados do tipo de promoção não informados.");
        }

        if (tipoPromocao.getTitulo() == null || tipoPromocao.getTitulo().trim().isEmpty()) {
            throw new BusinessException("Título é obrigatório.");
        }

        if (tipoPromocao.getDescricao() == null || tipoPromocao.getDescricao().trim().isEmpty()) {
            throw new BusinessException("Descrição é obrigatória.");
        }

        Usuario usuarioPadrao = new Usuario();
        usuarioPadrao.setId(1L); 

        tipoPromocao.setUsuarioCadastro(usuarioPadrao);

        return tipoPromocaoRepository.save(tipoPromocao);
    }

    @Override
    public TipoPromocao update(TipoPromocao tipoPromocao) {

        if (tipoPromocao.getId() == null ||
            !tipoPromocaoRepository.existsById(tipoPromocao.getId())) {
            throw new EntityNotFoundException("Tipo de promoção não encontrado.");
        }

        TipoPromocao existente = tipoPromocaoRepository
                .findById(tipoPromocao.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de promoção não encontrado."));

        tipoPromocao.setUsuarioCadastro(existente.getUsuarioCadastro());

        return tipoPromocaoRepository.save(tipoPromocao);
    }

    @Override
    public void delete(Long id) {

        TipoPromocao existente = tipoPromocaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de promoção não encontrado."));

        tipoPromocaoRepository.delete(existente);
    }

    @Override
    public List<TipoPromocao> findAll() {
        return tipoPromocaoRepository.findAll();
    }

    @Override
    public TipoPromocao findById(Long id) {
        return tipoPromocaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de promoção não encontrado."));
    }
}