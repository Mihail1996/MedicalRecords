package tecnico.ulisboa.sirs.controllers;


import org.ow2.authzforce.core.pdp.impl.BasePdpEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import tecnico.ulisboa.sirs.model.User;
import tecnico.ulisboa.sirs.service.ABAC;
import tecnico.ulisboa.sirs.service.UserService;

import java.util.Map;

@Controller

public class WatchRecordController {

    @Autowired
    private UserService userService;

    @Autowired
    private BasePdpEngine pdp;

    @RequestMapping(value = "/private/watchRecord", method = RequestMethod.GET)
    public ModelAndView watchRecord() {
        ModelAndView modelAndView = new ModelAndView();

        User userTmp = userService.findUserByCardIdAndDecrypt(SecurityContextHolder.getContext().getAuthentication().getName());
        Map<String, Boolean> decisions = ABAC.hasAccessPermission(userTmp.getRole(), pdp);
        modelAndView.addObject("decisions", decisions);

        if (!decisions.get("private/watchRecord")) {
            return new ModelAndView(new RedirectView("/access-denied"));
        }

        User user = new User();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("private/watchRecord");
        return modelAndView;
    }

    @RequestMapping(value = "/private/watchRecord", method = RequestMethod.POST)
    public ModelAndView watchRecord(User user, BindingResult bindingResult) {
        ModelAndView model = new ModelAndView();

        User userAuth = userService.findUserByCardIdAndDecrypt(SecurityContextHolder.getContext().getAuthentication().getName());
        Map<String, Boolean> decisions = ABAC.hasAccessPermission(userAuth.getRole(), pdp);
        model.addObject("decisions", decisions);

        if (!decisions.get("private/watchRecord")) {
            return new ModelAndView(new RedirectView("/access-denied"));
        }


        User userExists = userService.findUserByCardIdAndDecrypt(user.getCard());
        if (userExists == null) {
            bindingResult
                    .rejectValue("card", "error.user",
                            "No User with such Card ID");
        } else if (!ABAC.hasAccessPermission(userExists.getHospitals().get(0).getName(), userAuth.getRole(), pdp)) {
            model.addObject("successMessage", "No Permission to view this Record");
            model.setViewName("private/watchRecord");
            return model;
        } else {

            model.addObject("successMessage", "User Found");
            model.addObject("records", userService.getLast5Records(userExists));
            model.addObject("user", userExists);
            model.addObject("userExists", true);
            model.setViewName("private/watchRecord");
        }
        return model;
    }
}