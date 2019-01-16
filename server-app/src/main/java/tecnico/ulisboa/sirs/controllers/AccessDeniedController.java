package tecnico.ulisboa.sirs.controllers;


import org.ow2.authzforce.core.pdp.impl.BasePdpEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import tecnico.ulisboa.sirs.model.User;
import tecnico.ulisboa.sirs.service.ABAC;
import tecnico.ulisboa.sirs.service.UserService;


@Controller
public class AccessDeniedController {

    @Autowired
    private UserService userService;

    @Autowired
    private BasePdpEngine pdp;

    @RequestMapping(value = {"/access-denied"}, method = RequestMethod.GET)
    public ModelAndView accessDenied() {
        ModelAndView modelAndView = new ModelAndView();

        User user = userService.findUserByCardIdAndDecrypt(SecurityContextHolder.getContext().getAuthentication().getName());

        modelAndView.addObject("decisions", ABAC.hasAccessPermission(user.getRole(), pdp));
        modelAndView.addObject("username", "ID: " + user.getCard());
        modelAndView.addObject("accessDeniedMessage", "You don't have permission to access this page");
        modelAndView.setViewName("access-denied");
        return modelAndView;
    }

}

