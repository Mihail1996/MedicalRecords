package tecnico.ulisboa.sirs.controllers;


import org.ow2.authzforce.core.pdp.impl.BasePdpEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import tecnico.ulisboa.sirs.model.User;
import tecnico.ulisboa.sirs.service.ABAC;
import tecnico.ulisboa.sirs.service.UserService;

import java.util.Map;

@Controller
public class MyRecordController {

    @Autowired
    private UserService userService;

    @Autowired
    private BasePdpEngine pdp;

    @RequestMapping(value = "/private/myRecord", method = RequestMethod.GET)
    public ModelAndView myRecord() {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByCardIdAndDecrypt(auth.getName());
        Map<String, Boolean> decisions = ABAC.hasAccessPermission(user.getRole(), pdp);
        modelAndView.addObject("decisions", decisions);

        if (!decisions.get("private/myRecord")) {
            return new ModelAndView(new RedirectView("/access-denied"));
        }
        modelAndView.addObject("user", user);
        modelAndView.addObject("records", userService.getLast5Records(user));
        modelAndView.setViewName("private/myRecord");

        return modelAndView;
    }

}

