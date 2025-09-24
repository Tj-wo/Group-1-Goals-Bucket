package org.pahappa.systems.kpiTracker.security;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.security.Principal;

/**
 * Manages user login with Spring Security and handles authentication feedback.
 */
@Setter
@Getter
@ManagedBean(name = "loginController")
@ViewScoped
public class LoginController implements PhaseListener {

	private static final long serialVersionUID = 1L;
	private static final String SPRING_SECURITY_CHECK_ACTION = "/j_spring_security_check";
	// Getter and Setter for rememberMe
	private boolean rememberMe; // Property for Remember Me checkbox

	@PostConstruct
	public void init() {
		// Check authentication status when the bean is created
		checkAuthenticationStatus();
	}

	/**
	 * Redirect the login request to Spring Security check
	 */
	public String doLogin() throws ServletException, IOException {
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		ServletRequest request = (ServletRequest) context.getRequest();

		// Create a wrapper request that includes the remember-me parameter
		ServletRequest wrappedRequest = new javax.servlet.http.HttpServletRequestWrapper(
				(javax.servlet.http.HttpServletRequest) request) {
			@Override
			public String getParameter(String name) {
				if ("remember-me_input".equals(name)) {
					return rememberMe ? "true" : "false";
				}
				return super.getParameter(name);
			}
		};

		RequestDispatcher dispatcher = request.getRequestDispatcher(SPRING_SECURITY_CHECK_ACTION);
		dispatcher.forward(wrappedRequest, (ServletResponse) context.getResponse());
		FacesContext.getCurrentInstance().responseComplete();
		return null;
	}

	/**
	 * Check for authentication errors and display appropriate messages
	 */
	public void checkAuthenticationStatus() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// Check for authentication error parameter
		String error = externalContext.getRequestParameterMap().get("error");
		String logout = externalContext.getRequestParameterMap().get("logout");
		String expired = externalContext.getRequestParameterMap().get("expired");

		if (error != null && !error.isEmpty()) {
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Login Failed", "Invalid email or password. Please check your credentials and try again."));
		} else if (logout != null && !logout.isEmpty()) {
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Logout Successful", "You have been successfully logged out. Thank you for using KPI Tracker!"));
		} else if (expired != null && !expired.isEmpty()) {
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
					"Session Expired", "Your session has expired. Please log in again to continue."));
		} else {
			Principal userPrincipal = externalContext.getUserPrincipal();
			if (userPrincipal != null) {
				facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Already Logged In", "You are already logged in. Redirecting to dashboard..."));
			}
		}
	}

	/**
	 * Add a success message for successful login
	 */
	public void addSuccessMessage(String username) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Login Successful", "Welcome back, " + username + "! You have been successfully logged in."));
	}

	/**
	 * Add an error message for failed login
	 */
	public void addErrorMessage(String reason) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
				"Login Failed", reason));
	}

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}

	@Override
	public void afterPhase(PhaseEvent event) {
		// No action needed
	}

	@Override
	public void beforePhase(PhaseEvent event) {
		checkAuthenticationStatus();
	}

	// Getter and Setter for rememberMe
	public boolean isRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}
}