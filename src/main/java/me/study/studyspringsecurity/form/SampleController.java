package me.study.studyspringsecurity.form;

import java.util.concurrent.Callable;
import me.study.studyspringsecurity.account.AccountContext;
import me.study.studyspringsecurity.account.AccountRepository;
import me.study.studyspringsecurity.account.domain.Account;
import me.study.studyspringsecurity.book.BookRepository;
import me.study.studyspringsecurity.common.CurrentUser;
import me.study.studyspringsecurity.common.SecurityLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SampleController {

    @Autowired
    private SampleService sampleService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BookRepository bookRepository;

    @GetMapping("/")
    public String index(Model model, @CurrentUser Account account) {
        if (account == null) {
            model.addAttribute("message", "Hello, Spring Security!");
        } else {
            model.addAttribute("message", "Hello, " + account.getUsername() + "!");
        }

        return "index";
    }

    @GetMapping("/info")
    public String info(Model model) {
        model.addAttribute("message", "Info");

        return "info";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("message", "Hello, " + principal.getName());
        AccountContext.setAccount(accountRepository.findByUsername(principal.getName()));
        sampleService.dashboard();

        return "dashboard";
    }

    @GetMapping("/admin")
    public String admin(Model model, Principal principal) {
        model.addAttribute("message", "Hello, Admin " + principal.getName());

        return "admin";
    }

    @GetMapping("/user")
    public String user(Model model, Principal principal) {
        model.addAttribute("message", "Hello, User " + principal.getName());
        model.addAttribute("books", bookRepository.findAllOfCurrentUser());

        return "user";
    }

    @GetMapping("/async-handler")
    @ResponseBody
    public Callable<String> asyncHandler() {
        SecurityLogger.log("MVC");

        // 별도의 스레드에서 처리하지만 WebAsyncManagerIntegrationFilter에 의해 동일한 Principal이 참조됨
        return () -> {
            SecurityLogger.log("Callable");
            return "Async Handler";
        };
    }

    @GetMapping("/async-service")
    @ResponseBody
    public String asyncService() {
        SecurityLogger.log("MVC, before async service");
        sampleService.asyncService();
        SecurityLogger.log("MVC, after async service");

        return "Async Service";
    }
}
