package com.SmartEvent.SmartEvent.Controller;

import com.SmartEvent.SmartEvent.Model.Invitation;
import com.SmartEvent.SmartEvent.Model.Invite;
import com.SmartEvent.SmartEvent.Service.InviteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/invite/")
public class InviteController {
    InviteService inviteService;
    public InviteController(InviteService inviteService) {
        this.inviteService = inviteService;
    }
    @GetMapping("event/{eventId}")
    public ResponseEntity<List<Invite>> getInviteByEvent(@PathVariable String eventId) {
        return ResponseEntity.ok(inviteService.findByEvnet(eventId));
    }
}
