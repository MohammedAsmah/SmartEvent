package com.SmartEvent.SmartEvent.Service;

import com.SmartEvent.SmartEvent.Dto.InvitationRequest;
import com.SmartEvent.SmartEvent.Enums.InvitationStatus;
import com.SmartEvent.SmartEvent.Model.*;
import com.SmartEvent.SmartEvent.Repository.*;
import com.SmartEvent.SmartEvent.Utils.QrCodeGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.attoparser.ParsingProcessingInstructionUtil;
import org.bson.json.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.context.Context;

import java.io.File;
import java.util.*;

@Service
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final EmailService emailService;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final GeustRepository  geustRepository;
    private  final PdfService pdfService;
    private  final InviteRepository inviteRepository;

    @Value("${app.upload.qr-dir:uploads/qrcodes}")
    private String qrDir;

    public InvitationService(InvitationRepository invitationRepository, EmailService emailService, EventRepository eventRepository, UserRepository userRepository, GeustRepository geustRepository, PdfService pdfService, InviteRepository inviteRepository) {
        this.invitationRepository = invitationRepository;
        this.emailService = emailService;
        this.eventRepository = eventRepository;
        this.userRepository=userRepository;
        this.geustRepository=geustRepository;
        this.pdfService=pdfService;
        this.inviteRepository = inviteRepository;

    }

    @Transactional
    public Invitation sendInvitation(InvitationRequest  invitationRequest) {
        // 1️⃣ Generate QR Code
        Event event= eventRepository.findById(invitationRequest.getEventId()).orElseThrow(() -> new IllegalArgumentException("Event not found"));
        System.out.println(event);
        Optional<User> userOpt = userRepository.findByEmail(invitationRequest.getEmail());
        Person invite;
        if (userOpt.isPresent()) {
            invite = userOpt.get();      // User → Person (allowed)
        } else {
            invite=geustRepository.findByEmail(invitationRequest.getEmail());
            if(invite==null){
                invite = geustRepository.save( new Geust(
                        invitationRequest.getFirstName(),
                        invitationRequest.getLastName(),
                        invitationRequest.getEmail()
                ));
            }
        }

        Optional<Invite> existingInvite=inviteRepository.findByEventIdAndPersonId(event.getId(), invite.get_id());
        if(existingInvite.isPresent()){
            throw new IllegalArgumentException("This person has already been invited to this event");
        }


        String uuid = UUID.randomUUID().toString();
        // 2️⃣ Save the invitation
        Invitation invitation = new Invitation(event, invite, uuid);
        invitationRepository.save(invitation);
        // Check if this person is already invited to this event


        if (!existingInvite.isPresent()) {
            // Create a new invite if one doesn't exist
            inviteRepository.save(new Invite(event.getId(), invite.get_id(), invitation, false));
        }



        // 3️⃣ Prepare email content
        Context context = new Context();
        String localisation = event.getLocalisation();

        double lat;
        double lng;

        String[] parts = localisation.split(",");

        lat = Double.parseDouble(parts[0]);
        lng = Double.parseDouble(parts[1]);

        context.setVariable("locationLat", lat);
        context.setVariable("locationLng", lng);
        context.setVariable("inviteName", invite.getFirstName()+" "+invite.getLastName());
        context.setVariable("eventTitle", event.getTitre());
        context.setVariable("date", event.getStartDate());
        context.setVariable("type", event.getType());
        context.setVariable("description", event.getDescription());
        context.setVariable("confirmationUrl", "http://localhost:8080/public/invitations/confirm?token=" + invitation.getValidationToken()+"&confirmed=true");
        context.setVariable("disconfermationUrl", "http://localhost:8080/public/invitations/confirm?token=" + invitation.getValidationToken()+"&confirmed=false");
        // 4️⃣ Send the invitation email
        emailService.sendEmailWithTemplateAndQr(
                invite.getEmail(),
                "Invitation to " + event.getTitre(),
                "invitation-email",
                context,
                event.getLogo()
        );

        return invitation;
    }

    public ResponseEntity<Map> deleteInvitation(String invitationId) {
        try {
            invitationRepository.deleteById(invitationId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Invitation dleted successfully");
            return ResponseEntity.ok(response);
        }catch (Exception e){
            Map<String, String> response = new HashMap<>();
            response.put("message", "Invitation delete failed");
            return ResponseEntity.ok(response);
        }

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
    public ModelAndView invitationConfirmer(String validationToken, Boolean confirmed) {
        ModelAndView mav = new ModelAndView();
        Invitation invitation = invitationRepository.findByValidationToken(validationToken)
                .orElse(null);

        if (invitation == null) {
            mav.setViewName("error-template");
            mav.addObject("errorMessage", "The link is not valid anymore");
            return mav;
        }

        if (Boolean.TRUE.equals(confirmed)) {
            try {
                Invite invite = inviteRepository.findByEventIdAndPersonId(
                        invitation.getEvent().getId(),
                        invitation.getInvite().get_id()
                ).orElseThrow(() -> new IllegalArgumentException("Invite not found"));

                // Generate QR code and PDF
                String qrFileName = "QR_" + invite.getPersonId() + "_" + System.currentTimeMillis() + ".png";
                String qrPath = QrCodeGenerator.generateQRCode(
                        "Invitation for event: " + invitation.getEvent().getTitre() +
                                " | Invite ID: " + invite.getPersonId(),
                        qrDir,
                        qrFileName
                );

                String logoPath = invitation.getEvent().getLogo();
                String inviteFullName = invitation.getInvite().getFirstName() + " " +
                        invitation.getInvite().getLastName();

                // Generate PDF and convert to Base64 for embedding
                byte[] qrCodePdf = pdfService.generateInvitationPdf(
                        invitation.getEvent().getTitre(),
                        inviteFullName,
                        logoPath,
                        qrPath
                );
                String base64Pdf = Base64.getEncoder().encodeToString(qrCodePdf);

                // Update invitation status
                invitation.setStatus(InvitationStatus.ACCEPTED);
                invitation.setValidationToken(null);
                invitation.setQrCode(qrPath);
                invitationRepository.save(invitation);

                invite.setConfirmed(true);
                inviteRepository.save(invite);

                // Set up the success view
                mav.setViewName("invitation-confirmed");
                mav.addObject("eventTitle", invitation.getEvent().getTitre());
                mav.addObject("inviteName", inviteFullName);
                mav.addObject("eventDate", invitation.getEvent().getStartDate());
                mav.addObject("pdfBase64", base64Pdf);
                mav.addObject("pdfFileName", "invitation_" + invite.getPersonId() + ".pdf");

            } catch (Exception e) {
                mav.setViewName("error-template");
                mav.addObject("errorMessage", "Error processing your confirmation: " + e.getMessage());
            }
        } else {
            // Handle declined invitation
            invitation.setStatus(InvitationStatus.DECLINED);
            invitation.setValidationToken(null);
            invitationRepository.save(invitation);

            mav.setViewName("invitation-declined");
            mav.addObject("eventTitle", invitation.getEvent().getTitre());
            mav.addObject("inviteName",
                    invitation.getInvite().getFirstName() + " " + invitation.getInvite().getLastName());
        }

        return mav;
    }
}
