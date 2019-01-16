package tecnico.ulisboa.sirs.controllers;

import org.ow2.authzforce.core.pdp.impl.BasePdpEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import tecnico.ulisboa.sirs.model.User;
import tecnico.ulisboa.sirs.service.ABAC;
import tecnico.ulisboa.sirs.service.UserService;


@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private BasePdpEngine pdp;

    @RequestMapping(value = "/private/home", method = RequestMethod.GET)
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByCardIdAndDecrypt(auth.getName());
        modelAndView.addObject("decisions", ABAC.hasAccessPermission(user.getRole(), pdp));
        modelAndView.addObject("userName", "Welcome " + user.getName() + " " +
                user.getLastName() + " (" + user.getEmail() + ") ID: " + user.getCard() + " ROLE: " + user.getRole().getRole() + " Hospital: " +
                user.getHospitals().get(user.getHospitals().size() - 1).getName());
        modelAndView.setViewName("private/home");
        return modelAndView;
    }

}

