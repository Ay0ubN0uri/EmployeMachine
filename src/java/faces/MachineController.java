package faces;

import entities.Machine;
import faces.util.JsfUtil;
import faces.util.JsfUtil.PersistAction;
import beans.MachineFacade;
import entities.Employe;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartModel;
import org.primefaces.model.chart.ChartSeries;

@ManagedBean(name = "machineController")
//@ApplicationScoped
//@SessionScoped
@ViewScoped
public class MachineController implements Serializable {

    @EJB
    private beans.MachineFacade ejbFacade;
    private List<Machine> items = null;
    private Machine selected;

    public MachineController() {
    }

    public Machine getSelected() {
        return selected;
    }

    public void setSelected(Machine selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
        selected.setEmploye(new Employe());
    }

    private MachineFacade getFacade() {
        return ejbFacade;
    }

    public Machine prepareCreate() {
        selected = new Machine();
        selected.setEmploye(new Employe());
        initializeEmbeddableKey();
        System.out.println("==========================================");
        System.out.println(selected);
        return selected;
    }

    public Machine prepareEdit() {
        System.out.println("==========================================hello");
        if (selected.getEmploye() == null) {
            selected.setEmploye(new Employe());
        }
        return selected;
    }

    public void create() {
        System.out.println("==========================================");
        System.out.println(selected);
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle", FacesContext.getCurrentInstance().getViewRoot().getLocale()).getString("MachineCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle", FacesContext.getCurrentInstance().getViewRoot().getLocale()).getString("MachineUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle", FacesContext.getCurrentInstance().getViewRoot().getLocale()).getString("MachineDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Machine> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                System.out.println(ex.getMessage());
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle", FacesContext.getCurrentInstance().getViewRoot().getLocale()).getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle", FacesContext.getCurrentInstance().getViewRoot().getLocale()).getString("PersistenceErrorOccured"));
            }
        }
    }

    public List<Machine> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Machine> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Machine.class)
    public static class MachineControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MachineController controller = (MachineController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "machineController");
            return controller.getFacade().find(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Machine) {
                Machine o = (Machine) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Machine.class.getName()});
                return null;
            }
        }

    }

    
}
