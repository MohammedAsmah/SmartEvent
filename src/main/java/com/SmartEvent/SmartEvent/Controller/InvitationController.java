package com.SmartEvent.SmartEvent.Controller;

import com.SmartEvent.SmartEvent.Enums.InvitationStatus;
import com.SmartEvent.SmartEvent.Model.Invitation;
import com.SmartEvent.SmartEvent.Enums.EventStatus;
import com.SmartEvent.SmartEvent.Service.InvitationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invitations")
@CrossOrigin(origins = "*")
public class InvitationController {

    private final InvitationService invitationService;

    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    // ✅ Send an invitation (will generate QR + send email)
    @PostMapping("/send")
    public ResponseEntity<String> sendInvitation(
            @RequestParam String eventId,
            @RequestParam String email,
            @RequestParam String inviteId) {
        invitationService.sendInvitation(inviteId,eventId, email);
        return ResponseEntity.ok("Invitation sent successfully to " + email);
    }

    // ✅ Get all invitations
    @GetMapping
    public ResponseEntity<List<Invitation>> getAllInvitations() {
        return ResponseEntity.ok(invitationService.getAllInvitations());
    }

    // ✅ Get invitations for a specific event
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Invitation>> getInvitationsByEvent(@PathVariable String eventId) {
        return ResponseEntity.ok(invitationService.getInvitationsByEvent(eventId));
    }

    // ✅ Update invitation status (Pending → Accepted, Declined, etc.)
    @PutMapping("/{invitationId}/status")
    public ResponseEntity<Invitation> updateStatus(
            @PathVariable String invitationId,
            @RequestParam InvitationStatus status) {
        Invitation updated = invitationService.updateInvitationStatus(invitationId,status);
        return ResponseEntity.ok(updated);
    }
}
