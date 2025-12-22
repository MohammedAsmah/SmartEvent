package com.SmartEvent.SmartEvent.Controller;

import com.SmartEvent.SmartEvent.Dto.InvitationRequest;
import com.SmartEvent.SmartEvent.Enums.InvitationStatus;
import com.SmartEvent.SmartEvent.Model.Invitation;
import com.SmartEvent.SmartEvent.Enums.EventStatus;
import com.SmartEvent.SmartEvent.Service.InvitationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invitations")
@CrossOrigin(
        origins = {"http://localhost:4200", "http://127.0.0.1:4200"},
        allowCredentials = "true"
)
public class InvitationController {

    private final InvitationService invitationService;

    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    // ✅ Send an invitation (will generate QR + send email)
    @PostMapping("/send")
    public ResponseEntity<Map> sendInvitation(
            @RequestBody InvitationRequest invitationRequest) {
        invitationService.sendInvitation(invitationRequest);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Invitation sent successfully to " + invitationRequest.getEmail());
        return ResponseEntity.ok(response);
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

    @DeleteMapping("/{invitationId}")
    public ResponseEntity<Map> deletInvitation(@PathVariable String invitationId) {
        return invitationService.deleteInvitation(invitationId);
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
