
package com.soapboxrace.jaxb.http;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfSkillModPartTrans complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfSkillModPartTrans">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SkillModPartTrans" type="{}SkillModPartTrans" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfSkillModPartTrans", propOrder = {
    "skillModPartTrans"
})
public class ArrayOfSkillModPartTrans {

    @XmlElement(name = "SkillModPartTrans", nillable = true)
    protected List<SkillModPartTrans> skillModPartTrans;

    /**
     * Gets the value of the skillModPartTrans property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the skillModPartTrans property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSkillModPartTrans().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SkillModPartTrans }
     * 
     * 
     */
    public List<SkillModPartTrans> getSkillModPartTrans() {
        if (skillModPartTrans == null) {
            skillModPartTrans = new ArrayList<SkillModPartTrans>();
        }
        return this.skillModPartTrans;
    }

}
