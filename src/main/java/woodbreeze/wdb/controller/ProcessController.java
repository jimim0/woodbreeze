package woodbreeze.wdb.controller;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import woodbreeze.wdb.domain.Grade;
import woodbreeze.wdb.domain.Member;
import woodbreeze.wdb.domain.Process;
import woodbreeze.wdb.repository.ProcessRepository;
import woodbreeze.wdb.service.ProcessService;
import woodbreeze.wdb.service.ProductService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProcessController {

    private final ProcessService processService;

    // 공정 조회
    @GetMapping("/process")
    public String list(Model model, HttpServletRequest request){
        List<Process> process = processService.findProcess();
        model.addAttribute("process", process);

        HttpSession session = request.getSession();
        Member loginMember1 = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        // 세션에 로그인 멤버가 없거나 등급이 ADMIN이 아닌 경우에는 리다이렉트
        if (loginMember1 == null || loginMember1.getGrade() != Grade.ADMIN) {
            return "redirect:/";
        }

        Object loginMember = request.getSession().getAttribute(SessionConst.LOGIN_MEMBER);
        if (loginMember != null){
            model.addAttribute("member",loginMember);
            return "process/processList"; // 공정 목록을 보여주는 뷰 이름
        }
        return "redirect:/";

    }

    @GetMapping("/process1")
    public String list1(Model model, HttpServletRequest request){
        List<Process> process = processService.findProcess();
        model.addAttribute("process", process);

        Object loginMember = request.getSession().getAttribute(SessionConst.LOGIN_MEMBER);
        if (loginMember != null){
            model.addAttribute("member",loginMember);
            return "process/processList1"; // 공정 목록을 보여주는 뷰 이름
        }
        return "redirect:/";

    }
}
