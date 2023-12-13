/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package faces;

import entities.User;
import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import utils.SessionUtils;

/**
 *
 * @author ay0ub
 */
@ManagedBean
@SessionScoped
public class LoginController {

    @EJB
    private beans.UserFacade ejbFacade;

    private User user;

    @PostConstruct
    public void init() {
        user = new User();
    }

    public void login() throws IOException {
        System.out.println(user);
        User u = this.ejbFacade.userExist(user.getUsername(), user.getPassword());
        if (u != null) {
            HttpSession session = SessionUtils.getSession();
            session.setAttribute("user", u);
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            externalContext.redirect(externalContext.getRequestContextPath());
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Incorrect Username and Passowrd", "Please enter correct username and Password"));
        }
    }

    public void logout() throws IOException {
        HttpSession session = SessionUtils.getSession();
        session.invalidate();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        externalContext.redirect(externalContext.getRequestContextPath() + "/faces/login.xhtml");
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
