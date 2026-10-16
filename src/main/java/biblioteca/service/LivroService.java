
package biblioteca.service;

import biblioteca.exception.IsbnDuplicadoException;
import biblioteca.model.Livro;
import biblioteca.repository.LivroRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;
import java.util.Locale;

@Service
public class LivroService {

    private final LivroRepository repository;

    public LivroService(LivroRepository repository) {
        this.repository = repository;
    }

    public List<Livro> listarTodos() {
        return repository.findAll();
    }

    public Page<Livro> pesquisar(
            String busca,
            int numeroPagina,
            int tamanhoPagina) {

        Pageable pageable = PageRequest.of(
                numeroPagina,
                tamanhoPagina,
                Sort.by(Sort.Direction.ASC, "titulo")
        );

        if (busca == null || busca.isBlank()) {
            return repository.findAll(pageable);
        }

        String termo = busca.trim();

        return repository
                .findByTituloContainingIgnoreCaseOrAutorContainingIgnoreCase(
                        termo,
                        termo,
                        pageable
                );
    }

    public Livro buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Livro não encontrado: " + id
                        ));
    }

    @Transactional
    public Livro salvar(Livro livro) {
        validarAno(livro);

        String isbnNormalizado = normalizarIsbn(livro.getIsbn());
        livro.setIsbn(isbnNormalizado);

        boolean isbnDuplicado;

        if (livro.getId() == null) {
            isbnDuplicado = repository.existsByIsbn(isbnNormalizado);
        } else {
            isbnDuplicado = repository.existsByIsbnAndIdNot(
                    isbnNormalizado,
                    livro.getId()
            );
        }

        if (isbnDuplicado) {
            throw new IsbnDuplicadoException(
                    "Já existe um livro cadastrado com esse ISBN."
            );
        }

        return repository.save(livro);
    }

    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException(
                    "Livro não encontrado: " + id
            );
        }

        repository.deleteById(id);
    }

    private String normalizarIsbn(String isbn) {
        if (isbn == null) {
            return null;
        }

        return isbn.replace("-", "")
                .trim()
                .toUpperCase(Locale.ROOT);
    }

    private void validarAno(Livro livro) {
        if (livro.getAnoPublicacao() != null
                && livro.getAnoPublicacao() > Year.now().getValue()) {

            throw new IllegalArgumentException(
                    "O ano de publicação não pode ser futuro."
            );
        }
    }
}
