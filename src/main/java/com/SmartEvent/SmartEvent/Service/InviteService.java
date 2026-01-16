package com.SmartEvent.SmartEvent.Service;

import com.SmartEvent.SmartEvent.Model.Invite;
import com.SmartEvent.SmartEvent.Repository.InviteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InviteService {
    InviteRepository inviteRepository;
    public InviteService(InviteRepository inviteRepository) {
        this.inviteRepository = inviteRepository;
    }
    public List<Invite> findAll() {
        return inviteRepository.findAll();
    }
    public List<Invite> findByEvnet(String id) {
        return inviteRepository.findByEventId(id);
    }

}
