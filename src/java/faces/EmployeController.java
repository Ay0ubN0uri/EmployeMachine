package faces;

import entities.Employe;
import faces.util.JsfUtil;
import faces.util.JsfUtil.PersistAction;
import beans.EmployeFacade;
import entities.Machine;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartModel;
import org.primefaces.model.chart.ChartSeries;

@ManagedBean(name = "employeController")
@SessionScoped
public class EmployeController implements Serializable {

    @EJB
    private beans.EmployeFacade ejbFacade;
    @EJB
    private beans.MachineFacade ejbFacadeMachine;
    private List<Employe> items = null;
    private Employe selected;

    public EmployeController() {
    }

    public Employe getSelected() {
        return selected;
    }

    public void setSelected(Employe selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private EmployeFacade getFacade() {
        return ejbFacade;
    }

    public Employe prepareCreate() {
        selected = new Employe();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle",FacesContext.getCurrentInstance().getViewRoot().getLocale()).getString("EmployeCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle",FacesContext.getCurrentInstance().getViewRoot().getLocale()).getString("EmployeUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle",FacesContext.getCurrentInstance().getViewRoot().getLocale()).getString("EmployeDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Employe> getItems() {
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
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle",FacesContext.getCurrentInstance().getViewRoot().getLocale()).getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle",FacesContext.getCurrentInstance().getViewRoot().getLocale()).getString("PersistenceErrorOccured"));
            }
        }
    }

    public List<Employe> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Employe> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Employe.class)
    public static class EmployeControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            EmployeController controller = (EmployeController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "employeController");
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
            if (object instanceof Employe) {
                Employe o = (Employe) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Employe.class.getName()});
                return null;
            }
        }

    }
    
    public ChartModel initBarModel(){
        
        calculateMachinesCountPerYear().forEach((i,j)->{
            System.out.println(i + "\t" + j);
        });
        System.out.println("=================");
        
        Map<Integer, Integer> machinesPerYear = new HashMap<>();

        List<Employe> employes = getFacade().findAll();

        for (Employe employe : employes) {
            for (Machine machine : employe.getMachines()) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(machine.getDateAchat());
                int year = calendar.get(Calendar.YEAR);

                machinesPerYear.merge(year, 1, Integer::sum);
            }
        }
        
        
        
        CartesianChartModel model = new CartesianChartModel();
        ChartSeries machinesSeries = new ChartSeries();
        machinesSeries.setLabel("Machines Acquired");

        for (Employe employe : employes) {
            for (Machine machine : employe.getMachines()) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(machine.getDateAchat());
                int year = calendar.get(Calendar.YEAR);

                machinesSeries.set(year, machinesSeries.getData().getOrDefault(year, 0).intValue() + 1);
            }
        }
        model.addSeries(machinesSeries);
        return model;
    }
    
    public BarChartModel createBarModel() {
        calculateMachinesCountPerYear().forEach((i,j)->{
            System.out.println(i + "\t" + j);
        });
        System.out.println("=================");
        BarChartModel barModel = new BarChartModel();
        barModel.setTitle("Machines Acquired per Year");
        barModel.setLegendPosition("ne");

        Map<Integer, Long> machinesCountPerYear = calculateMachinesCountPerYear();

        ChartSeries series = new ChartSeries();
        for (Map.Entry<Integer, Long> entry : machinesCountPerYear.entrySet()) {
            System.out.println(entry.getKey());
            series.setLabel(String.valueOf(entry.getKey()));
            series.set(entry.getKey(), entry.getValue());
        }
        barModel.addSeries(series);

        barModel.setShowPointLabels(true);
        barModel.getAxes().put(AxisType.X, new CategoryAxis("Year"));
        Axis yAxis = barModel.getAxis(AxisType.Y);
        yAxis.setLabel("Number of Machines");
        yAxis.setMin(0);
        yAxis.setMax(findMaxCount(machinesCountPerYear));
        return barModel;
    }
    private int findMaxCount(Map<Integer, Long> machinesCountPerYear) {
        return machinesCountPerYear.values().stream()
                .mapToInt(Long::intValue)
                .max()
                .orElse(0);
    }
    
    
    private Map<Integer, Long> calculateMachinesCountPerYear() {
        Map<Integer, Long> machinesCountPerYear = new HashMap<>();

        List<Machine> allMachines = ejbFacadeMachine.findAll();

        for (Machine machine : allMachines) {
            int year = machine.getDateAchat().getYear() + 1900; // Add 1900 to get the actual year
            machinesCountPerYear.merge(year, 1L, Long::sum);
        }

        return machinesCountPerYear;
    }

}
