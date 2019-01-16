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
import tecnico.ulisboa.sirs.model.Hospital;

import tecnico.ulisboa.sirs.model.User;
import tecnico.ulisboa.sirs.service.ABAC;
import tecnico.ulisboa.sirs.service.UserService;

import java.util.Map;


@Controller
public class AddHospitalController {

    @Autowired
    private UserService userService;

    @Autowired
    private BasePdpEngine pdp;

    @RequestMapping(value = "/private/addHospital", method = RequestMethod.GET)
    public ModelAndView addHospital() {
        ModelAndView modelAndView = new ModelAndView();

        User userExists = userService.findUserByCardIdAndDecrypt(SecurityContextHolder.getContext().getAuthentication().getName());
        Map<String, Boolean> decisions = ABAC.hasAccessPermission(userExists.getRole(), pdp);
        modelAndView.addObject("decisions", decisions);

        if (!decisions.get("private/addHospital")) {
            return new ModelAndView(new RedirectView("/access-denied"));
        }

        Hospital hospital = new Hospital();
        modelAndView.addObject("hospital", hospital);
        modelAndView.setViewName("private/addHospital");
        return modelAndView;

    }


    @RequestMapping(value = "/private/addHospital", method = RequestMethod.POST)
    public ModelAndView addHospital(Hospital hospital, BindingResult bindingResult) {
        ModelAndView model = new ModelAndView();

        User userAuth = userService.findUserByCardIdAndDecrypt(SecurityContextHolder.getContext().getAuthentication().getName());
        Map<String, Boolean> decisions = ABAC.hasAccessPermission(userAuth.getRole(), pdp);
        model.addObject("decisions", decisions);

        if (!decisions.get("private/addHospital")) {
            return new ModelAndView(new RedirectView("/access-denied"));
        }

        Hospital hospitalExists = userService.findHospitalByNameCityCountry(hospital.getName(), hospital.getCity(), hospital.getCountry());
        if (hospitalExists != null) {
            bindingResult
                    .rejectValue("name", "error.hospital",
                            "There is already a Hospital with this name");
        } else {
            model.addObject("successMessage", userService.saveHospital(hospital));
            model.addObject("hospital", new Hospital());
            model.setViewName("private/addHospital");

        }
        return model;
    }

}

