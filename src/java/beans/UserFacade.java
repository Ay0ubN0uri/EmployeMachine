/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import entities.User;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author ay0ub
 */
@Stateless
public class UserFacade extends AbstractFacade<User> {
    @PersistenceContext(unitName = "EmployeMachinePU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UserFacade() {
        super(User.class);
    }
    
    
    public User userExist(String username, String password) {
        try {
            User user = em.createQuery("from User u where u.username=:username and u.password=:password", User.class)
                    .setParameter("username", username)
                    .setParameter("password", password).getSingleResult();
            return user;
        } catch (Exception e) {
            return null;
        }
    }
    
}