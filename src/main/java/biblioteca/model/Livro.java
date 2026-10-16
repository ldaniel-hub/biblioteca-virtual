
package biblioteca.model;

import jakarta.persistence.Column;  
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O título é obrigatório.")
    @Size(max = 150, message = "O título deve ter no máximo 150 caracteres.")
    @Column(nullable = false, length = 150)
    private String titulo;

    @NotBlank(message = "O nome do autor é obrigatório.")
    @Size(max = 120, message = "O nome do autor deve ter no máximo 120 caracteres.")
    @Column(nullable = false, length = 120)
    private String autor;

    @NotBlank(message = "O ISBN é obrigatório.")
    @Pattern(
        regexp = "^[0-9Xx-]+$",
        message = "O ISBN deve conter apenas números, hífens ou X."
    )
    @Size(
        min = 10,
        max = 17,
        message = "O ISBN deve ter entre 10 e 17 caracteres, incluindo hífens."
    )
    @Column(nullable = false, unique = true, length = 17)
    private String isbn;

    @NotNull(message = "O ano de publicação é obrigatório.")
    @Min(value = 1, message = "O ano de publicação deve ser maior que zero.")
    @Max(value = 9999, message = "Informe um ano válido.")
    @Column(nullable = false)
    private Integer anoPublicacao;

    @NotNull(message = "A quantidade é obrigatória.")
    @Min(value = 0, message = "A quantidade não pode ser negativa.")
    @Column(nullable = false)
    private Integer quantidade;
}
