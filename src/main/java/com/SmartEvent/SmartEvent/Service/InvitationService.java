package com.SmartEvent.SmartEvent.Service;

import com.SmartEvent.SmartEvent.Enums.InvitationStatus;
import com.SmartEvent.SmartEvent.Model.Event;
import com.SmartEvent.SmartEvent.Model.Invitation;
import com.SmartEvent.SmartEvent.Model.Invite;
import com.SmartEvent.SmartEvent.Model.User;
import com.SmartEvent.SmartEvent.Repository.EventRepository;
import com.SmartEvent.SmartEvent.Repository.InvitationRepository;
import com.SmartEvent.SmartEvent.Repository.InviteRepository;
import com.SmartEvent.SmartEvent.Repository.UserRepository;
import com.SmartEvent.SmartEvent.Utils.QrCodeGenerator;
import org.attoparser.ParsingProcessingInstructionUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.io.File;
import java.util.List;

@Service
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final EmailService emailService;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Value("${app.upload.qr-dir:uploads/qrcodes}")
    private String qrDir;

    public InvitationService(InvitationRepository invitationRepository, EmailService emailService, EventRepository eventRepository, UserRepository userRepository) {
        this.invitationRepository = invitationRepository;
        this.emailService = emailService;
        this.eventRepository = eventRepository;
        this.userRepository=userRepository;
    }

    public Invitation sendInvitation(String event_id,String invite_id, String recipientEmail) {
        // 1️⃣ Generate QR Code
        Event event= eventRepository.findById(event_id).orElseThrow(() -> new IllegalArgumentException("Event not found"));
        User invite= userRepository.findById(invite_id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        String qrFileName = "QR_" + invite_id + "_" + System.currentTimeMillis() + ".png";
        String qrPath = QrCodeGenerator.generateQRCode(
                "Invitation for event: " + event.getTitre() + " | User ID: " ,
                qrDir,
                qrFileName
        );

        // 2️⃣ Save the invitation
        Invitation invitation = new Invitation(event, invite, qrPath);
        invitationRepository.save(invitation);

        // 3️⃣ Prepare email content
        Context context = new Context();
        context.setVariable("eventTitle", event.getTitre());
        context.setVariable("location", event.getLocalisation());
        context.setVariable("date", event.getStartDate());
        context.setVariable("status", invitation.getStatus());
        context.setVariable("confirmationUrl", "http://localhost:8080/api/invitations/confirm/" + invitation.getId());
        // 4️⃣ Send the invitation email
        emailService.sendEmailWithTemplateAndQr(
                recipientEmail,
                "Invitation to " + event.getTitre(),
                "invitation-email",
                context,
                qrPath
        );

        return invitation;
    }

    public List<Invitation> getInvitationsByEvent(String eventId) {
        return invitationRepository.findByEventId(eventId);
    }

    /**
     * ✅ Update invitation status (Pending → Accepted, Declined, etc.)
     */
    public Invitation updateInvitationStatus(String invitationId, InvitationStatus status) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new IllegalArgumentException("Invitation not found with ID: " + invitationId));

        invitation.setStatus(status);
        return invitationRepository.save(invitation);
    }
    public List<Invitation> getAllInvitations() {
        return invitationRepository.findAll();
    }
}
