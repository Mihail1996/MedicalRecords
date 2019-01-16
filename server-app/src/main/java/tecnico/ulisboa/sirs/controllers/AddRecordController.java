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
import tecnico.ulisboa.sirs.model.Record;
import tecnico.ulisboa.sirs.model.User;
import tecnico.ulisboa.sirs.service.ABAC;
import tecnico.ulisboa.sirs.service.UserService;

import java.util.Map;


@Controller
public class AddRecordController {

    @Autowired
    private UserService userService;

    @Autowired
    private BasePdpEngine pdp;

    @RequestMapping(value = "/private/addRecord", method = RequestMethod.GET)
    public ModelAndView getUser() {
        ModelAndView modelAndView = new ModelAndView();

        User userExists = userService.findUserByCardIdAndDecrypt(SecurityContextHolder.getContext().getAuthentication().getName());
        Map<String, Boolean> decisions = ABAC.hasAccessPermission(userExists.getRole(), pdp);
        modelAndView.addObject("decisions", decisions);

        if (!decisions.get("private/addRecord")) {
            return new ModelAndView(new RedirectView("/access-denied"));
        }

        User user = new User();
        modelAndView.addObject("user", user);
        Record record = new Record();
        modelAndView.addObject("record", record);

        modelAndView.setViewName("private/addRecord");
        return modelAndView;

    }


    @RequestMapping(value = "/private/addRecord", method = RequestMethod.POST)
    public ModelAndView getUser(Record record, User user, BindingResult bindingResult) {
        ModelAndView model = new ModelAndView();

        User userAuth = userService.findUserByCardIdAndDecrypt(SecurityContextHolder.getContext().getAuthentication().getName());
        Map<String, Boolean> decisions = ABAC.hasAccessPermission(userAuth.getRole(), pdp);
        model.addObject("decisions", decisions);

        if (!decisions.get("private/addRecord")) {
            return new ModelAndView(new RedirectView("/access-denied"));
        }
        User userExists = userService.findUserByCardIdAndDecrypt(user.getCard());
        if (userExists == null) {
            bindingResult
                    .rejectValue("card", "error.user",
                            "No User with such Card ID");

        } else if (!ABAC.hasAccessPermission(userExists.getHospitals().get(0).getName(), userAuth.getRole(), pdp)) {
            model.addObject("successMessage", "No Permission to view this Record");
            model.setViewName("private/addRecord");
            return model;
        } else {

            model.addObject("successMessage", userService.saveRecord(record, userExists, userAuth) + "");
            model.addObject("user", userExists);
            model.addObject("userExists", true);
            model.setViewName("private/addRecord");

        }
        return model;
    }

}

