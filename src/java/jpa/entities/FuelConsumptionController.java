package jpa.entities;

import jpa.entities.util.JsfUtil;
import jpa.entities.util.PaginationHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

@Named("fuelConsumptionController")
@SessionScoped
public class FuelConsumptionController implements Serializable {

    private FuelConsumption current;
    private DataModel items = null;
    @EJB
    private jpa.entities.FuelConsumptionFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

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
            System.out.println("current: " + current);
            String strDeviceId = current.getDeviceId().toString();
            String strFuelEntryDate = String.valueOf(current.getFuelEntryDt());
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
            List lstPeriodSeparated = Arrays.asList(String.valueOf(lstColonSeparated.get(2)).split("."));
            String strSeconds = lstPeriodSeparated.get(0).toString();
            String strApiFormattedTimestamp = strYear + "-" + strMonth + "-" +
                    strDay + "T" + strHour + ":" + strMinutes + ":" +
                    strSeconds +"Z";
            //TODO: figure out where to get previous fuel entry date (db or api) - hard coding for now                  
            // build out url with query parameters
            String strBaseUrl = "http://gps.nextop.vip/api/positions";
            String strUrlWithQueries = strBaseUrl + "?deviceId=" + strDeviceId
                    + "&amp;from=" + "2018-12-8T18:30:00Z" + "&amp;to=" + 
                    strApiFormattedTimestamp;
                    System.out.println("strUrlWithQueries: " + strUrlWithQueries);
//                    
//                    $.ajax({
//                        type: "GET",
//                        url: strUrlWithQueries,
//                        success: function(jsonData) {
////                            console.log("jsonData = " + jsonData);
//
//                            let dblGasRefillGals = Double.parseDoube(
//                                    document.getElementById(
//                                    'frmFuelEntry:gasRefillGals').value);
//                            let dblUnitCost = Double.parseDouble(
//                                    document.getElementById(
//                                    'frmFuelEntry:unitCost').value);
//                            let dblTotalCost = dblGasRefillGals * dblUnitCost;
//                            document.getElementById('frmFuelEntry:totalCost')
//                                    .innerHTML(String.valueOf(dblTotalCost));
//
//                            let objLastItem = jsonData.pop();
//                            let strFuelVal1;
//                            let strFuelVal2;
//                            let strPrevFillupMileage; // TODO: need to query db or other api for this value
//                            
//                            // get attributes item and parse out io136 and io137
//                            for(let property in objLastItem) {
//                                if(objLastItem.hasOwnProperty(property)) {
//                                    if(property === "attributes") {
//                                        let objAttributes = objLastItem[property];
//                                        for(let attribute in objAttributes) {
//                                            if(attribute === "io136") {
//                                                strFuelVal1 = property[attribute];
//                                            }
//                                            if(attribute === "io137") {
//                                                strFuelVal2 = property[attribute];
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                            //TODO: setup an assignable vars to enter in 
//                            //denominators for flowmeters calculation
//                            const FLOWMETER1_DENOMINATOR = 361.0;
//                            const FLOWMETER2_DENOMINATOR = 409.0;
//                            
//                            //calculate expected fuel consumption
//                            //TODO: think about how to call previous row from db for previous fillup mileage or if value is another api call
//                            // /api/reports/trips?deviceId=10&amp;from=2018-10-22T18:30:00Z&amp;to=2018-12-11T18:30:00Z
//                            // formula should be (currFillupKm - prevFillupKm) / 100
//                            let dblCurrentMileageKm = Double.parseDouble(document.getElementById('frmFuelEntryId:limeageKm').value);
//                            let dblExpectedFuelConsumption = dblCurrentMileageKm / 100.0;                    
//                            document.getElementById('frmFuelEntry:expectedFuelConsumption').innerHTML = String.valueOf(dblExpectedFuelConsumption);
//                            
//                            // calculate real fuel consumption by either nozzle 
//                            // crocodile or flowmeters
//                            //TODO: add if else branch for nozzle and flowmeters
//                            
//                            // (io136/kfactor)-(io137/kfactor)
//                            let dblRealFuelConsumption = 
//                                    (Double.parseDouble(strFuelVal1) / 
//                                    FLOWMETER1_DENOMINATOR) - (Doube.parsedouble(strFuelVal2));
//                            document.getElementById('frmFuelEntry:realConsumption').innerHTML = 
//                                    String.valueOf(realFuelConsumption);
//                            
//                            // calculate difference in gallons
//                            let dblDiffGallons = dblExpectedFuelConsumption - dblRealFuelConsumption;
//                            document.getElementById('frmFuelEntry:diffGals').innerHTML = String.valueOf(dblDiffGallons);
//                            
//                            // calculate fuel consumption difference in gallans
//                            let dblDiffPercent = 100 - ((dblRealFuelConsumption * 100) / dblExpectedFuelConsumption);
//                            document.getElementById('frmFuelEntry:diffPercent').innerHTML = String.valueOf(dblDiffPercent);
//                            
//                            // calculate fuel consumption difference in cash
//                            let dblFuelDiffCash = dblDiffGallons * dblUnitCost;
//                            document.getElementById('frmFuelEntry:diffCash').innerHTML = String.valueOf(dblFuelDiffCash);              
//                        },
//                        error: function() {
//                            console.log("&lt;An error occured with API call&ht;");
//                        }
//                    });
//                }
//            </script>
            
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("FuelConsumptionCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
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
