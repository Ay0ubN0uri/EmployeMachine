/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import entities.User;

/**
 *
 * @author ay0ub
 */
public class SessionUtils {
    public static HttpSession getSession() {
        return (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);
    }

    public static HttpServletRequest getRequest() {
        return (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();
    }

    public static String getUserName() {
        HttpSession session = getSession();
        if (session != null) {
            return ((User) session.getAttribute("user")).getUsername();
        } else {
            return null;
        }
    }

    public static String getUserId() {
        HttpSession session = getSession();
        if (session != null) {
            return ((User) session.getAttribute("user")).getId() + "";
        } else {
            return null;
        }
    }
}
