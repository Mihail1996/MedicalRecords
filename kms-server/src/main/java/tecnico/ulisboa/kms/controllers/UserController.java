package tecnico.ulisboa.kms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tecnico.ulisboa.kms.service.UserService;

@RestController
@RequestMapping(value = "/users")
public class UserController {


    @Autowired
    private UserService userService;

    @RequestMapping(value = "/{cardId}", method = RequestMethod.GET)
    public String getUserByCard(@PathVariable final String cardId) {
        return userService.getKeyByCardId(cardId);
    }

    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void addUser(String card) {
        userService.addUser(card);
        }
    }

