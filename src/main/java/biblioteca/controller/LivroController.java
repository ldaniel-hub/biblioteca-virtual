
package biblioteca.controller;

import biblioteca.exception.IsbnDuplicadoException;
import biblioteca.model.Livro;
import biblioteca.service.LivroService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/livros")
public class LivroController {

    private static final int TAMANHO_PAGINA = 5;

    private final LivroService service;

    public LivroController(LivroService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(
            @RequestParam(defaultValue = "") String busca,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        int paginaSegura = Math.max(page, 0);

        Page<Livro> pagina = service.pesquisar(
                busca,
                paginaSegura,
                TAMANHO_PAGINA
        );

        if (paginaSegura > 0 && paginaSegura >= pagina.getTotalPages()) {
            return "redirect:/livros";
        }

        model.addAttribute("pagina", pagina);
        model.addAttribute("livros", pagina.getContent());
        model.addAttribute("busca", busca);
        model.addAttribute("paginaAtual", paginaSegura);

        return "livros/lista";
    }

    @GetMapping("/novo")
    public String novo(
            @RequestParam(defaultValue = "") String busca,
            Model model) {

        model.addAttribute("livro", new Livro());
        model.addAttribute("busca", busca);

        return "livros/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(
            @Valid @ModelAttribute("livro") Livro livro,
            BindingResult resultado,
            @RequestParam(defaultValue = "") String busca,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (resultado.hasErrors()) {
            model.addAttribute("busca", busca);
            return "livros/formulario";
        }

        try {
            boolean novoLivro = livro.getId() == null;

            service.salvar(livro);

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    novoLivro
                            ? "Livro cadastrado com sucesso!"
                            : "Livro atualizado com sucesso!"
            );

            redirectAttributes.addAttribute("busca", busca);

            return "redirect:/livros";

        } catch (IsbnDuplicadoException e) {
            resultado.rejectValue("isbn", "isbn.duplicado", e.getMessage());

        } catch (IllegalArgumentException e) {
            resultado.rejectValue(
                    "anoPublicacao",
                    "ano.invalido",
                    e.getMessage()
            );
        }

        model.addAttribute("busca", busca);
        return "livros/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editar(
            @PathVariable Long id,
            @RequestParam(defaultValue = "") String busca,
            Model model) {

        model.addAttribute("livro", service.buscarPorId(id));
        model.addAttribute("busca", busca);

        return "livros/formulario";
    }

    @GetMapping("/detalhes/{id}")
    public String detalhes(
            @PathVariable Long id,
            @RequestParam(defaultValue = "") String busca,
            Model model) {

        model.addAttribute("livro", service.buscarPorId(id));
        model.addAttribute("busca", busca);

        return "livros/detalhes";
    }

    @PostMapping("/excluir/{id}")
    public String excluir(
            @PathVariable Long id,
            @RequestParam(defaultValue = "") String busca,
            RedirectAttributes redirectAttributes) {

        service.excluir(id);

        redirectAttributes.addFlashAttribute(
                "sucesso",
                "Livro excluído com sucesso!"
        );

        redirectAttributes.addAttribute("busca", busca);

        return "redirect:/livros";
    }
}
