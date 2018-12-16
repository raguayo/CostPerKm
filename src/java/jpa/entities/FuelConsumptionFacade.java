/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa.entities;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author rickaguayo
 */
@Stateless
public class FuelConsumptionFacade extends AbstractFacade<FuelConsumption> {

    @PersistenceContext(unitName = "CostPerKmPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FuelConsumptionFacade() {
        super(FuelConsumption.class);
    }
    
}
