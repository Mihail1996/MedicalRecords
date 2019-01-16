package tecnico.ulisboa.sirs.controllers;

import org.ow2.authzforce.core.pdp.impl.BasePdpEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import tecnico.ulisboa.sirs.model.User;
import tecnico.ulisboa.sirs.service.ABAC;
import tecnico.ulisboa.sirs.service.PasswordHolder;
import tecnico.ulisboa.sirs.service.UserService;

import java.util.Map;

@Controller
public class ChangePasswordController {

    @Autowired
    private UserService userService;

    @Autowired
    private BasePdpEngine pdp;

    @RequestMapping(value = "/private/changeMyPassword", method = RequestMethod.GET)
    public ModelAndView changePassword() {
        ModelAndView modelAndView = new ModelAndView();

        User user = userService.findUserByCardIdAndDecrypt(SecurityContextHolder.getContext().getAuthentication().getName());
        Map<String, Boolean> decisions = ABAC.hasAccessPermission(user.getRole(), pdp);
        modelAndView.addObject("decisions", decisions);

        if (!decisions.get("private/changeMyPassword")) {
            return new ModelAndView(new RedirectView("/access-denied"));
        }

        PasswordHolder passwordHolder = new PasswordHolder();
        modelAndView.addObject("passwordHolder", passwordHolder);
        modelAndView.setViewName("private/changeMyPassword");

        return modelAndView;
    }


    @RequestMapping(value = "/private/changeMyPassword", method = RequestMethod.POST)
    public ModelAndView changePassword(PasswordHolder passwordHolder) {
        ModelAndView modelAndView = new ModelAndView();
        User userAuth = userService.findUserByCardIdAndDecrypt(SecurityContextHolder.getContext().getAuthentication().getName());
        Map<String, Boolean> decisions = ABAC.hasAccessPermission(userAuth.getRole(), pdp);
        if (!decisions.get("private/changeMyPassword")) {
            return new ModelAndView(new RedirectView("/access-denied"));
        }
        modelAndView.addObject("decisions", decisions);
        modelAndView.addObject("successMessage", userService.changePassword(userAuth, passwordHolder));
        modelAndView.setViewName("private/changeMyPassword");
        return modelAndView;
    }

}
