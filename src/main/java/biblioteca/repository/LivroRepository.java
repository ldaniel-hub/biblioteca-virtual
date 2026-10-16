
package biblioteca.repository;

import biblioteca.model.Livro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LivroRepository extends JpaRepository<Livro, Long> {

    Page<Livro> findByTituloContainingIgnoreCaseOrAutorContainingIgnoreCase(
            String titulo,
            String autor,
            Pageable pageable
    );

    boolean existsByIsbn(String isbn);

    boolean existsByIsbnAndIdNot(String isbn, Long id);
}
