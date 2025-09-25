package org.pahappa.systems.client.converters;

import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("departmentConverter")
public class DepartmentConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        
        try {
            DepartmentService departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
            return departmentService.getInstanceByID(value);
        } catch (Exception e) {
            System.err.println("DepartmentConverter: Error converting value '" + value + "' to Department object: " + e.getMessage());
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            System.out.println("DepartmentConverter: getAsString called with null object");
            return "";
        }
        
        if (value instanceof Department) {
            Department department = (Department) value;
            String id = department.getId();
            String departmentName = department.getName();
            System.out.println("DepartmentConverter: getAsString called with Department ID: " + id + ", Name: " + departmentName);
            return id;
        }
        
        System.out.println("DepartmentConverter: getAsString called with non-Department object: " + value.getClass().getSimpleName());
        return "";
    }
}

