package org.pahappa.systems.client.converters;

import org.pahappa.systems.kpiTracker.core.services.TeamService;
import org.pahappa.systems.kpiTracker.models.team.Team;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("teamConverter")
public class TeamConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        
        try {
            TeamService teamService = ApplicationContextProvider.getBean(TeamService.class);
            return teamService.getInstanceByID(value);
        } catch (Exception e) {
            System.err.println("TeamConverter: Error converting value '" + value + "' to Team object: " + e.getMessage());
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            System.out.println("TeamConverter: getAsString called with null object");
            return "";
        }
        
        if (value instanceof Team) {
            Team team = (Team) value;
            String id = team.getId();
            String teamName = team.getTeamName();
            System.out.println("TeamConverter: getAsString called with Team ID: " + id + ", Name: " + teamName);
            return id;
        }
        
        System.out.println("TeamConverter: getAsString called with non-Team object: " + value.getClass().getSimpleName());
        return "";
    }
}
