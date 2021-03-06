
package com.soapboxrace.jaxb.http;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfInventoryItemTrans complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfInventoryItemTrans">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="InventoryItemTrans" type="{}InventoryItemTrans" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfInventoryItemTrans", propOrder = {
    "inventoryItemTrans"
})
public class ArrayOfInventoryItemTrans {

    @XmlElement(name = "InventoryItemTrans", nillable = true)
    protected List<InventoryItemTrans> inventoryItemTrans;

    /**
     * Gets the value of the inventoryItemTrans property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the inventoryItemTrans property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInventoryItemTrans().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InventoryItemTrans }
     * 
     * 
     */
    public List<InventoryItemTrans> getInventoryItemTrans() {
        if (inventoryItemTrans == null) {
            inventoryItemTrans = new ArrayList<InventoryItemTrans>();
        }
        return this.inventoryItemTrans;
    }

}
