
package biblioteca.controller;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String tratarRecursoNaoEncontrado(
            IllegalArgumentException exception,
            Model model) {

        model.addAttribute(
                "mensagem",
                "O livro solicitado não foi encontrado. Ele pode ter sido removido ou o endereço pode estar incorreto."
        );

        return "erro";
    }
}
