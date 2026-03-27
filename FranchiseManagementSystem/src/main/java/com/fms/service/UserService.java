package com.fms.service;

import com.fms.entity.Users;
import com.fms.entity.Roles;
import com.fms.entity.Companies;
import com.fms.entity.Branches;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.ejb.EJB;

import java.util.Date;
import java.util.List;

@Stateless
public class UserService implements UserServiceLocal {

    @PersistenceContext(unitName = "FranchisePU")
    private EntityManager em;

//    @EJB
//    private EmailServiceLocal emailService;
    
    @EJB
    private NotificationServiceLocal notificationService;

    // 1️⃣ Create User (Super Admin / Franchise Owner / Manager / Staff)
    @Override
    public void createUser(Users user, int roleId, int companyId, Integer branchId) {

        Roles role = em.find(Roles.class, roleId);
        Companies company = em.find(Companies.class, companyId);

        user.setRid(role);
        user.setCid(company);
        user.setCreatedDate(new Date());
        user.setStatus("ACTIVE");

        // If branch user (manager/staff)
        if (branchId != null) {
            Branches branch = em.find(Branches.class, branchId);
            user.setBid(branch);
        }

        em.persist(user);

//        // Send Email
//        emailService.sendEmail(
//                user.getEmail(),
//                "Account Created",
//                "Your account has been created.\nEmail: " + user.getEmail()
//        );

// Send Credentials Notification
        notificationService.sendCredentials(user.getEmail(), user.getPassword());
    }

    // 2️⃣ Get users by company
    @Override
    public List<Users> getUsersByCompany(int companyId) {

        Query q = em.createQuery(
                "SELECT u FROM Users u WHERE u.cid.cid = :cid"
        );

        q.setParameter("cid", companyId);

        return q.getResultList();
    }

    // 3️⃣ Get users by role
    @Override
    public List<Users> getUsersByRole(int roleId) {

        Query q = em.createQuery(
                "SELECT u FROM Users u WHERE u.rid.rid = :rid"
        );

        q.setParameter("rid", roleId);

        return q.getResultList();
    }

    // 4️⃣ Activate user
    @Override
    public void activateUser(int userId) {

        Users user = em.find(Users.class, userId);

        user.setStatus("ACTIVE");

        em.merge(user);
    }

    // 5️⃣ Deactivate user
    @Override
    public void deactivateUser(int userId) {

        Users user = em.find(Users.class, userId);

        user.setStatus("INACTIVE");

        em.merge(user);
    }

    // 6️⃣ Find user by email (Login ready)
    @Override
    public Users findUserByEmail(String email) {

        Query q = em.createNamedQuery("Users.findByEmail");

        q.setParameter("email", email);

        return (Users) q.getSingleResult();
    }

}