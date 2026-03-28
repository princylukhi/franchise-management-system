package com.fms.bean;

import com.fms.entity.Users;
import com.fms.service.UserServiceLocal;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.HttpSession;
import jakarta.faces.application.FacesMessage;

import java.io.Serializable;

@Named
@SessionScoped
public class LoginBean implements Serializable {

    private String email;
    private String password;
    private Users loggedUser;

    @EJB
    private UserServiceLocal userService;

    // LOGIN METHOD
    public String login() {

        Users user = userService.login(email, password);

//        if (user != null) {
            if (user != null && user.getRid() != null) {

            this.loggedUser = user;

            // Create session
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                    .getExternalContext().getSession(true);

            session.setAttribute("user", user);
            session.setAttribute("role", user.getRid().getRoleName());
            session.setAttribute("companyId", user.getCid().getCid());

            // Redirect based on role
            String role = user.getRid().getRoleName();

            switch (role) {
                case "SYSTEM_ADMIN":
                    return "admin_dashboard.xhtml?faces-redirect=true";
                case "SUPER_ADMIN":
                    return "company_dashboard.xhtml?faces-redirect=true";
                case "FRANCHISE_OWNER":
                    return "franchise_dashboard.xhtml?faces-redirect=true";
                case "BRANCH_MANAGER":
                    return "branch_dashboard.xhtml?faces-redirect=true";
                case "STAFF":
                    return "billing.xhtml?faces-redirect=true";
                default:
                    return null;
            }
        }

        // Show error message
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Login Failed", "Invalid email or password"));

        return null;
    }

    // LOGOUT METHOD
    public String logout() {

        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);

        if (session != null) {
            session.invalidate();
        }

        return "login.xhtml?faces-redirect=true";
    }

    // GETTERS & SETTERS

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Users getLoggedUser() { return loggedUser; }
}