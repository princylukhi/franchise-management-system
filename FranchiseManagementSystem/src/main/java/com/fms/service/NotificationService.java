package com.fms.service;

import com.fms.entity.Notifications;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.ejb.EJB;

import java.util.Date;
import java.util.List;

@Stateless
public class NotificationService implements NotificationServiceLocal {

    @PersistenceContext(unitName = "FranchisePU")
    private EntityManager em;

    @EJB
    private EmailServiceLocal emailService;

    // 1️⃣ Core method: Save + Send Email
    @Override
    public void sendNotification(String email, String subject, String message, String type) {

        // Save notification in DB
        Notifications n = new Notifications();

        n.setRecipientEmail(email);
        n.setSubject(subject);
        n.setMessage(message);
        n.setNotificationType(type);
        n.setSentDate(new Date());

        em.persist(n);

        // Send Email
        emailService.sendEmail(email, subject, message);
    }

    // 2️⃣ Company Approved
    @Override
    public void sendCompanyApproval(String email) {

        sendNotification(
                email,
                "Company Approved",
                "Your company registration has been approved.",
                "COMPANY_APPROVAL"
        );
    }

    // 3️⃣ Company Rejected
    @Override
    public void sendCompanyRejection(String email) {

        sendNotification(
                email,
                "Company Rejected",
                "Your company registration request has been rejected.",
                "COMPANY_REJECTION"
        );
    }

    // 4️⃣ Franchise Approved
    @Override
    public void sendFranchiseApproval(String email) {

        sendNotification(
                email,
                "Franchise Approved",
                "Your franchise request has been approved.",
                "FRANCHISE_APPROVAL"
        );
    }

    // 5️⃣ Send Credentials
    @Override
    public void sendCredentials(String email, String password) {

        sendNotification(
                email,
                "Login Credentials",
                "Your account has been created.\nEmail: " + email + "\nPassword: " + password,
                "CREDENTIALS"
        );
    }

    // 6️⃣ Get all notifications (for admin panel)
    @Override
    public List<Notifications> getAllNotifications() {

        Query q = em.createNamedQuery("Notifications.findAll");

        return q.getResultList();
    }
}