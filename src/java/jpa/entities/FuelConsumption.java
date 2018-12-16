/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
//import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rickaguayo
 */
@Entity
@Table(name = "FUEL_CONSUMPTION")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FuelConsumption.findAll", query = "SELECT f FROM FuelConsumption f")
    , @NamedQuery(name = "FuelConsumption.findById", query = "SELECT f FROM FuelConsumption f WHERE f.id = :id")
    , @NamedQuery(name = "FuelConsumption.findByTicketNo", query = "SELECT f FROM FuelConsumption f WHERE f.ticketNo = :ticketNo")
    , @NamedQuery(name = "FuelConsumption.findByDeviceId", query = "SELECT f FROM FuelConsumption f WHERE f.deviceId = :deviceId")
    , @NamedQuery(name = "FuelConsumption.findByLimeageKm", query = "SELECT f FROM FuelConsumption f WHERE f.limeageKm = :limeageKm")
    , @NamedQuery(name = "FuelConsumption.findByFuelEntryDt", query = "SELECT f FROM FuelConsumption f WHERE f.fuelEntryDt = :fuelEntryDt")
    , @NamedQuery(name = "FuelConsumption.findByGasRefillGals", query = "SELECT f FROM FuelConsumption f WHERE f.gasRefillGals = :gasRefillGals")
    , @NamedQuery(name = "FuelConsumption.findByUnitCost", query = "SELECT f FROM FuelConsumption f WHERE f.unitCost = :unitCost")
    , @NamedQuery(name = "FuelConsumption.findByTotalCost", query = "SELECT f FROM FuelConsumption f WHERE f.totalCost = :totalCost")
    , @NamedQuery(name = "FuelConsumption.findByExpectedFuelConsumption", query = "SELECT f FROM FuelConsumption f WHERE f.expectedFuelConsumption = :expectedFuelConsumption")
    , @NamedQuery(name = "FuelConsumption.findByRealConsumption", query = "SELECT f FROM FuelConsumption f WHERE f.realConsumption = :realConsumption")
    , @NamedQuery(name = "FuelConsumption.findByDiffGals", query = "SELECT f FROM FuelConsumption f WHERE f.diffGals = :diffGals")
    , @NamedQuery(name = "FuelConsumption.findByDiffPercent", query = "SELECT f FROM FuelConsumption f WHERE f.diffPercent = :diffPercent")
    , @NamedQuery(name = "FuelConsumption.findByDiffCash", query = "SELECT f FROM FuelConsumption f WHERE f.diffCash = :diffCash")})
public class FuelConsumption implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "TICKET_NO")
    private Integer ticketNo;
    @Column(name = "DEVICE_ID")
    private Integer deviceId;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "LIMEAGE_KM")
    private Double limeageKm;
    @Column(name = "FUEL_ENTRY_DT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fuelEntryDt;
    @Column(name = "GAS_REFILL_GALS")
    private Double gasRefillGals;
    @Column(name = "UNIT_COST")
    private Double unitCost;
    @Column(name = "TOTAL_COST")
    private Double totalCost;
    @Column(name = "EXPECTED_FUEL_CONSUMPTION")
    private Double expectedFuelConsumption;
    @Column(name = "REAL_CONSUMPTION")
    private Double realConsumption;
    @Lob
    @Column(name = "DRIVER", length = 32700)
    private String driver;
    @Column(name = "DIFF_GALS")
    private Double diffGals;
    @Column(name = "DIFF_PERCENT")
    private Double diffPercent;
    @Column(name = "DIFF_CASH")
    private Double diffCash;

    public FuelConsumption() {
    }

    public FuelConsumption(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(Integer ticketNo) {
        this.ticketNo = ticketNo;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public Double getLimeageKm() {
        return limeageKm;
    }

    public void setLimeageKm(Double limeageKm) {
        this.limeageKm = limeageKm;
    }

    public Date getFuelEntryDt() {
        return fuelEntryDt;
    }

    public void setFuelEntryDt(Date fuelEntryDt) {
        this.fuelEntryDt = fuelEntryDt;
    }

    public Double getGasRefillGals() {
        return gasRefillGals;
    }

    public void setGasRefillGals(Double gasRefillGals) {
        this.gasRefillGals = gasRefillGals;
    }

    public Double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(Double unitCost) {
        this.unitCost = unitCost;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public Double getExpectedFuelConsumption() {
        return expectedFuelConsumption;
    }

    public void setExpectedFuelConsumption(Double expectedFuelConsumption) {
        this.expectedFuelConsumption = expectedFuelConsumption;
    }

    public Double getRealConsumption() {
        return realConsumption;
    }

    public void setRealConsumption(Double realConsumption) {
        this.realConsumption = realConsumption;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public Double getDiffGals() {
        return diffGals;
    }

    public void setDiffGals(Double diffGals) {
        this.diffGals = diffGals;
    }

    public Double getDiffPercent() {
        return diffPercent;
    }

    public void setDiffPercent(Double diffPercent) {
        this.diffPercent = diffPercent;
    }

    public Double getDiffCash() {
        return diffCash;
    }

    public void setDiffCash(Double diffCash) {
        this.diffCash = diffCash;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FuelConsumption)) {
            return false;
        }
        FuelConsumption other = (FuelConsumption) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jpa.entities.FuelConsumption[ id=" + id + " ]";
    }
    
}
