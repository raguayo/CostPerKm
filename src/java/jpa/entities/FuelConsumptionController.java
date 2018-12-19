package jpa.entities;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;

import java.util.Arrays;
import java.util.List;
import java.util.Base64;
import java.util.ResourceBundle;

import java.io.Serializable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.HttpURLConnection;
import java.net.URL;

import jpa.entities.util.JsfUtil;
import jpa.entities.util.PaginationHelper;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

@Named("fuelConsumptionController")
@SessionScoped
public class FuelConsumptionController implements Serializable {

    private FuelConsumption current;
    private DataModel items = null;
    @EJB
    private jpa.entities.FuelConsumptionFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    
    //TODO: setup an assignable vars to enter in 
    //denominators for flowmeters calculation
    private final double FLOWMETER1_DENOMINATOR = 361.0;
    private final double FLOWMETER2_DENOMINATOR = 409.0;
    private final double NOZZLE_DENOMINATOR = 0.0003;

    public FuelConsumptionController() {
    }

    public FuelConsumption getSelected() {
        if (current == null) {
            current = new FuelConsumption();
            selectedItemIndex = -1;
        }
        return current;
    }

    private FuelConsumptionFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (FuelConsumption) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new FuelConsumption();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
//            System.out.println("current: " + current);
            String strDeviceId = current.getDeviceId().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/YYYY hh:mm:ss");
            String strFuelEntryDate = String.valueOf(sdf.format(current.getFuelEntryDt()));
            List lstSpaceSeparated = Arrays.asList(strFuelEntryDate.split(" "));
            String strDate = lstSpaceSeparated.get(0).toString();
            String strTime = lstSpaceSeparated.get(1).toString();
//            "2018-10-22T18:30:00Z" MM/DD/YYYY hh:mm:ss.0         
            List lstSlashSeparated = Arrays.asList(strDate.split("/"));
            String strMonth = lstSlashSeparated.get(0).toString();
            String strDay = lstSlashSeparated.get(1).toString();
            String strYear = lstSlashSeparated.get(2).toString();
            List lstColonSeparated = Arrays.asList(strTime.split(":"));
            String strHour = lstColonSeparated.get(0).toString();
            String strMinutes = lstColonSeparated.get(1).toString();
            String strSeconds = lstColonSeparated.get(2).toString();
            String strApiCurrFormattedTimestamp = strYear + "-" + strMonth + "-" +
                    strDay + "T" + strHour + ":" + strMinutes + ":" +
                    strSeconds +"Z";               
            
            // build out url with query parameters
            //TODO: query db to get last row with fuel entry date and last mileageKm
            Object[] objPrevFuelAndMileage = retrieveLastFuelEntryDateAndMileage();
            double dblPrevMileageKm;
            String strPrevFuelEntryDate;
            String strApiPrevFormattedTimestamp;
            if(objPrevFuelAndMileage == null) {
                dblPrevMileageKm = current.getLimeageKm();
                strApiPrevFormattedTimestamp = strApiCurrFormattedTimestamp;
            } else {
                dblPrevMileageKm = Double.parseDouble(String.valueOf(objPrevFuelAndMileage[1]));
                strPrevFuelEntryDate = String.valueOf(sdf.format(objPrevFuelAndMileage[0]));
                List lstSpaceSeparated2 = Arrays.asList(strPrevFuelEntryDate.split(" "));
                String strDate2 = lstSpaceSeparated2.get(0).toString();
                String strTime2 = lstSpaceSeparated2.get(1).toString();    
                List lstSlashSeparated2 = Arrays.asList(strDate2.split("/"));
                String strMonth2 = lstSlashSeparated2.get(0).toString();
                String strDay2 = lstSlashSeparated2.get(1).toString();
                String strYear2 = lstSlashSeparated2.get(2).toString();
                List lstColonSeparated2 = Arrays.asList(strTime2.split(":"));
                String strHour2 = lstColonSeparated2.get(0).toString();
                String strMinutes2 = lstColonSeparated2.get(1).toString();
                String strSeconds2 = lstColonSeparated2.get(2).toString();
                strApiPrevFormattedTimestamp = strYear2 + "-" + strMonth2 + "-" +
                        strDay2 + "T" + strHour2 + ":" + strMinutes2 + ":" +
                        strSeconds2 +"Z"; 
            }
         
            String strBaseUrl = "http://gps.nextop.vip/api/positions";
            String strUrlWithQueries = strBaseUrl + "?deviceId=" + strDeviceId
                    + "&from=" + strApiPrevFormattedTimestamp + "&to=" + 
                    strApiCurrFormattedTimestamp;
//                    System.out.println("strUrlWithQueries: " + strUrlWithQueries);
            String usernameColonPassword = "nextop:nextop123";
            String basicAuthPayload = "Basic " + Base64.getEncoder().encodeToString(usernameColonPassword.getBytes());

            BufferedReader httpResponseReader = null;
            // Connect to the web server endpoint
            URL serverUrl = new URL(strUrlWithQueries);
            HttpURLConnection urlConnection = (HttpURLConnection) serverUrl.openConnection();

            // Set HTTP method as GET
            urlConnection.setRequestMethod("GET");
            
            // set request accept type to JSON
            urlConnection.addRequestProperty("Accept", "application/json");

            // Include the HTTP Basic Authentication payload
            urlConnection.addRequestProperty("Authorization", basicAuthPayload);
            
            urlConnection.addRequestProperty("User-Agent", "Mozilla/4.0");

            // Read response from web server, which will trigger HTTP Basic Authentication request to be sent.
            httpResponseReader =
                    new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String lineRead;
            String strJson = "";
            while((lineRead = httpResponseReader.readLine()) != null) {
//                System.out.println(lineRead);
                strJson += lineRead;
            }
            System.out.println("strJson: " + strJson);
            //calculate  and set total cost
            current.setTotalCost(current.getGasRefillGals() * current.getUnitCost());
            
            ObjectMapper objMapper = new ObjectMapper();       
            JsonNode objJsonNode = objMapper.readTree(strJson);
            JsonNode objLastItem = objJsonNode.get(objJsonNode.size() - 1);
            for(Object objItem : objLastItem) {
                System.out.println(objItem);
            }
            // get attributes item and parse out io136 and io137
            double dblFuelVal1 = objLastItem.get("attributes").get("io136") != null ?
                    objLastItem.get("attributes").get("io136").asDouble() : -1.0;
            double dblFuelVal2 = objLastItem.get("attributes").get("io137") != null 
                    ? objLastItem.get("attributes").get("io137").asDouble() : -1.0;
            
            //calculate & set expected fuel consumption (currFillupKm - prevFillupKm) / 100)
            current.setExpectedFuelConsumption(current.getLimeageKm() - dblPrevMileageKm / 100.0);
                                     
            // calculate and set real fuel consumption by either nozzle 
            // crocodile or flowmeters
            if(dblFuelVal1 > 0.0 && dblFuelVal2 > 0.0) {
                // Flowmeters -> (io136/kfactor)-(io137/kfactor)
                current.setRealConsumption((dblFuelVal1 / FLOWMETER1_DENOMINATOR) - (dblFuelVal2 / FLOWMETER2_DENOMINATOR));
            } else { // only dblFuelCal1 has a value so use the nozzle denominator
                current.setRealConsumption(dblFuelVal1 * NOZZLE_DENOMINATOR);
            }
            
            // calculate and set difference in gallons
            current.setDiffGals(current.getExpectedFuelConsumption() - current.getRealConsumption());

            // calculate and set fuel consumption difference percent
            current.setDiffPercent(100.0 - ((current.getRealConsumption() *  100.0) / current.getExpectedFuelConsumption()));
                         
            // calculate and set fuel consumption difference in cash
            current.setDiffCash(current.getDiffGals() * current.getUnitCost());
            
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("FuelConsumptionCreated"));

            return prepareCreate();
        } catch (IOException e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        } 
    }
    
    private Object[] retrieveLastFuelEntryDateAndMileage() {
        Object [] arrObj = null;
        String strSql = "SELECT fuel_entry_dt, limeage_km FROM fuel_consumption ORDER BY fuel_entry_dt DESC FETCH FIRST 1 ROWS ONLY";
        EntityManagerFactory objEntityMgrFactory = Persistence.createEntityManagerFactory("CostPerKmPU");
        EntityManager objEntityMgr = objEntityMgrFactory.createEntityManager();
        Query objQuery = objEntityMgr.createNativeQuery(strSql);
        List <Object[]> lstFuelEntryDtAndMileage = objQuery.getResultList();
        
        if(lstFuelEntryDtAndMileage.size() > 0) {
            arrObj = lstFuelEntryDtAndMileage.get(0);
//            for(Object a : lstFuelEntryDtAndMileage) {
//                System.out.println("Object a: " + a);
//            }
        }
//        TypedQuery<FuelConsumption> objTypedQuery = objEntityMgr.createQuery(strSql, FuelConsumption.class);      
//        List lstFuelConsumption = objTypedQuery.getResultList();       
//        System.out.println("lstFuelConsumption [");
//        System.out.println("fuel_entry_dt: " + lstFuelConsumption.get(0));
//        System.out.println("limeage_km: " + lstFuelConsumption.get(1));
//        System.out.println("]");

        return arrObj;
    }

    public String prepareEdit() {
        current = (FuelConsumption) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("FuelConsumptionUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (FuelConsumption) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("FuelConsumptionDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public FuelConsumption getFuelConsumption(java.lang.Integer id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = FuelConsumption.class)
    public static class FuelConsumptionControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            FuelConsumptionController controller = (FuelConsumptionController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "fuelConsumptionController");
            return controller.getFuelConsumption(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof FuelConsumption) {
                FuelConsumption o = (FuelConsumption) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + FuelConsumption.class.getName());
            }
        }

    }

}
