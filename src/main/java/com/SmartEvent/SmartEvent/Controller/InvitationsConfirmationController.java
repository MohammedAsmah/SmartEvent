package com.SmartEvent.SmartEvent.Controller;


import com.SmartEvent.SmartEvent.Repository.InvitationRepository;
import com.SmartEvent.SmartEvent.Service.InvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/public/invitations/")
public class InvitationsConfirmationController {

    @Autowired
    private InvitationService invitationService;
    @GetMapping("confirm")
    public ModelAndView confirmInvitation(
            @RequestParam String token,
            @RequestParam(required = false) Boolean confirmed) {

        return invitationService.invitationConfirmer(token, confirmed);
    }

}
