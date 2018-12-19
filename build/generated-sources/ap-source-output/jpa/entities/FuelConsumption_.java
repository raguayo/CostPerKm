package jpa.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2018-12-19T00:27:22")
@StaticMetamodel(FuelConsumption.class)
public class FuelConsumption_ { 

    public static volatile SingularAttribute<FuelConsumption, Date> fuelEntryDt;
    public static volatile SingularAttribute<FuelConsumption, Integer> deviceId;
    public static volatile SingularAttribute<FuelConsumption, Double> limeageKm;
    public static volatile SingularAttribute<FuelConsumption, Double> diffPercent;
    public static volatile SingularAttribute<FuelConsumption, Double> expectedFuelConsumption;
    public static volatile SingularAttribute<FuelConsumption, Integer> ticketNo;
    public static volatile SingularAttribute<FuelConsumption, String> driver;
    public static volatile SingularAttribute<FuelConsumption, Double> realConsumption;
    public static volatile SingularAttribute<FuelConsumption, Double> unitCost;
    public static volatile SingularAttribute<FuelConsumption, Double> diffGals;
    public static volatile SingularAttribute<FuelConsumption, Double> diffCash;
    public static volatile SingularAttribute<FuelConsumption, Integer> id;
    public static volatile SingularAttribute<FuelConsumption, Double> gasRefillGals;
    public static volatile SingularAttribute<FuelConsumption, Double> totalCost;

}