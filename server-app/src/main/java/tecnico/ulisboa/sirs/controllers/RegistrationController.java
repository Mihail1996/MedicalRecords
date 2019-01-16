package tecnico.ulisboa.sirs.controllers;

import org.ow2.authzforce.core.pdp.impl.BasePdpEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import tecnico.ulisboa.sirs.model.Hospital;
import tecnico.ulisboa.sirs.service.ABAC;
import tecnico.ulisboa.sirs.model.User;
import tecnico.ulisboa.sirs.service.UserService;

import javax.validation.Valid;
import java.util.Map;

@Controller
public class RegistrationController {

    @Autowired
    private BasePdpEngine pdp;

    @Autowired
    private UserService userService;


    @RequestMapping(value = "/private/registration", method = RequestMethod.GET)
    public ModelAndView registration() {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userExists = userService.findUserByCardIdAndDecrypt(auth.getName());
        Map<String, Boolean> decisions = ABAC.hasAccessPermission(userExists.getRole(), pdp);

        if (!decisions.get("private/registration")) {
            return new ModelAndView(new RedirectView("/access-denied"));
        }

        User user = new User();
        Hospital hospital = new Hospital();
        user.getHospitals().add(hospital);
        modelAndView.addObject("decisions", decisions);
        modelAndView.addObject("user", user);
        modelAndView.addObject("allHospitals", userService.getAllHospitals());
        modelAndView.addObject("roles", userService.getAllRoles());
        modelAndView.setViewName("private/registration");

        return modelAndView;
    }

    @RequestMapping(value = "/private/registration", method = RequestMethod.POST)
    public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();

        User userAuth = userService.findUserByCardIdAndDecrypt(SecurityContextHolder.getContext().getAuthentication().getName());
        Map<String, Boolean> decisions = ABAC.hasAccessPermission(userAuth.getRole(), pdp);
        modelAndView.addObject("decisions", decisions);

        if (!decisions.get("private/registration")) {
            return new ModelAndView(new RedirectView("/access-denied"));
        }

        User userExists = userService.findUserByCardIdAndDecrypt(user.getCard());
        if (userExists != null) {
            bindingResult
                    .rejectValue("card", "error.user",
                            "There is already a user registered with the CardID provided");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("private/registration");
            modelAndView.addObject("allHospitals", userService.getAllHospitals());
            modelAndView.addObject("roles", userService.getAllRoles());
        } else {
            User newUser = new User();
            Hospital hospital = new Hospital();
            newUser.getHospitals().add(hospital);
            modelAndView.addObject("successMessage", userService.saveUser(user));
            modelAndView.addObject("user", newUser);
            modelAndView.addObject("allHospitals", userService.getAllHospitals());
            modelAndView.addObject("roles", userService.getAllRoles());
            modelAndView.setViewName("private/registration");

        }
        return modelAndView;
    }


}

