package org.pahappa.systems.kpiTracker.models.security;

public final class PermissionConstants {
    private PermissionConstants() {
    }

    @SystemPermission(name = "Api user", description = "Has role for api users", category = "System")
    public static final String PERM_API_USER = "Api User";

    @SystemPermission(name = "Web Access", description = "A general web access permission required when migrating roles.", category = "System")
    public static final String PERM_WEB_ACCESS = "Web Access";

    // ====================== User Management Permissions ======================
    @SystemPermission(name = "Create Staff", description = "Allows creating new Staff. Department Leads with this permission can only create users within their own department.", category = "Staff Management")
    public static final String PERM_CREATE_STAFF = "Create User";

    @SystemPermission(name = "Read/View Staff", description = "Allows viewing staff.", category = "Staff Management")
    public static final String PERM_READ_VIEW_STAFF = "Read/View Users";

    @SystemPermission(name = "Update staff", description = "Allows modifying Staff attributes.", category = "Staff Management")
    public static final String PERM_UPDATE_STAFF = "Update User";

    @SystemPermission(name = "Deactivate/Reactivate Staff", description = "Allows deactivating or reactivating user accounts. A user cannot be permanently deleted, only deactivated.", category = "Staff Management")
    public static final String PERM_DEACTIVATE_REACTIVATE_STAFF = "Deactivate/Reactivate User";

    @SystemPermission(name = "Bulk Import Staff", description = "Allows importing multiple users via a CSV template.", category = "Staff Management")
    public static final String PERM_BULK_IMPORT_STAFF = "Bulk Import Users";

    // ====================== Role and Permission Management Permissions ======================
    @SystemPermission(name = "Create Role", description = "Allows creating a new role.", category = "Role Management")
    public static final String PERM_CREATE_ROLE = "Create Role";

    @SystemPermission(name = "Read/View Roles", description = "Allows reading/viewing roles.", category = "Role Management")
    public static final String PERM_READ_VIEW_ROLES = "Read/View Roles";

    @SystemPermission(name = "Update Role", description = "Allows modifying an existing role.", category = "Role Management")
    public static final String PERM_UPDATE_ROLE = "Update Role";

    @SystemPermission(name = "Delete Role", description = "Allows deleting a role.", category = "Role Management")
    public static final String PERM_DELETE_ROLE = "Delete Role";

    @SystemPermission(name = "Assign Roles to User", description = "Allows assigning roles to users.", category = "Role Management")
    public static final String PERM_ASSIGN_ROLES_TO_USER = "Assign Roles to User";

    // ====================== Department Management Permissions ======================
    @SystemPermission(name = "Create Department", description = "Allows creating new departments.", category = "Department Management")
    public static final String PERM_CREATE_DEPARTMENT = "Create Department";

    @SystemPermission(name = "View Departments", description = "Allows reading/viewing department information.", category = "Department Management")
    public static final String PERM_VIEW_DEPARTMENTS = "View Departments";

    @SystemPermission(name = "Update Department", description = "Allows modifying department details.", category = "Department Management")
    public static final String PERM_UPDATE_DEPARTMENT = "Update Department";

    @SystemPermission(name = "Delete Department", description = "Allows soft deleting a department by setting its IsActive status to false.", category = "Department Management")
    public static final String PERM_DELETE_DEPARTMENT = "Delete Department";

    // ====================== Team Management Permissions ======================
    @SystemPermission(name = "Create Team", description = "Allows creating new organizational teams.", category = "Team Management")
    public static final String PERM_CREATE_TEAM = "Create Team";

    @SystemPermission(name = "Read/View Team", description = "Allows viewing team information.", category = "Team Management")
    public static final String PERM_READ_VIEW_TEAM = "Read/View Team";

    @SystemPermission(name = "Update Team", description = "Allows updating team details such as name, description, lead, and active status.", category = "Team Management")
    public static final String PERM_UPDATE_TEAM = "Update Team";

    @SystemPermission(name = "Deactivate Team", description = "Allows marking teams as inactive (IsActive = FALSE).", category = "Team Management")
    public static final String PERM_DEACTIVATE_TEAM = "Deactivate Team";

    @SystemPermission(name = "Delete Team", description = "Allows deleting teams, only if no active employees, goals, or KPIs are linked.", category = "Team Management")
    public static final String PERM_DELETE_TEAM = "Delete Team";

    @SystemPermission(name = "View Teams Page", description = "Specifically allows viewing the teams page.", category = "Team Management")
    public static final String PERM_VIEW_TEAMS_PAGE = "View Teams Page";

    // ====================== Goal Management Permissions ======================
    @SystemPermission(name = "Add Goal", description = "Allows defining new organizational goals.", category = "Goal Management")
    public static final String PERM_ADD_GOAL = "Add Goal";

    @SystemPermission(name = "View Goal", description = "Allows viewing goal details.", category = "Goal Management")
    public static final String PERM_VIEW_GOAL = "View Goal";

    @SystemPermission(name = "Update Goal", description = "Allows modifying existing goals.", category = "Goal Management")
    public static final String PERM_UPDATE_GOAL = "Update Goal";

    @SystemPermission(name = "Delete Goal", description = "Allows deleting goals (soft delete by setting isActive to false). Deletion may be restricted if a goal has linked child goals.", category = "Goal Management")
    public static final String PERM_DELETE_GOAL = "Delete Goal";

    @SystemPermission(name = "Approve Goal", description = "Allows users to approve a goal.", category = "Goal Management")
    public static final String PERM_APPROVE_GOAL = "Approve Goal";

    @SystemPermission(name = "Request Changes for Goal", description = "Allows users to request changes for a goal.", category = "Goal Management")
    public static final String PERM_REQUEST_CHANGES_FOR_GOAL = "Request Changes for Goal";

    @SystemPermission(name = "Reject Goal", description = "Allows users to reject a goal.", category = "Goal Management")
    public static final String PERM_REJECT_GOAL = "Reject Goal";

    // ====================== Key Performance Indicator (KPI) Management Permissions ======================
    @SystemPermission(name = "Create KPIs", description = "Allows creating KPIs for goals.", category = "KPI Management")
    public static final String PERM_CREATE_KPIS = "Create KPIs";

    @SystemPermission(name = "View KPIs", description = "Allows viewing KPIs.", category = "KPI Management")
    public static final String PERM_VIEW_KPIS = "View KPIs";

    @SystemPermission(name = "Update KPIs", description = "Allows updating KPIs, including their progress.", category = "KPI Management")
    public static final String PERM_UPDATE_KPIS = "Update KPIs";

    @SystemPermission(name = "Delete KPIs", description = "Allows deleting KPIs.", category = "KPI Management")
    public static final String PERM_DELETE_KPIS = "Delete KPIs";

    // ====================== Goal Activity Management Permissions ======================
    @SystemPermission(name = "Attach New Activities", description = "Allows attaching new activities to a goal.", category = "Goal Activity Management")
    public static final String PERM_ATTACH_NEW_ACTIVITIES = "Attach New Activities";

    @SystemPermission(name = "Edit Existing Activities", description = "Allows editing existing activities.", category = "Goal Activity Management")
    public static final String PERM_EDIT_EXISTING_ACTIVITIES = "Edit Existing Activities";

    @SystemPermission(name = "Delete Existing Activities", description = "Allows deleting existing activities.", category = "Goal Activity Management")
    public static final String PERM_DELETE_EXISTING_ACTIVITIES = "Delete Existing Activities";

    // ====================== Organization Fit Survey Management Permissions ======================
    @SystemPermission(name = "Create Survey Category", description = "Allows users (e.g., HR_MANAGER, ADMIN roles) to create new survey categories.", category = "Survey Management")
    public static final String PERM_CREATE_SURVEY_CATEGORY = "Create Survey Category";

    @SystemPermission(name = "Create Survey Question", description = "Allows users to create new questions within categories.", category = "Survey Management")
    public static final String PERM_CREATE_SURVEY_QUESTION = "Create Survey Question";

    @SystemPermission(name = "View Survey", description = "Allows users to view categories and questions within the survey.", category = "Survey Management")
    public static final String PERM_VIEW_SURVEY = "View Survey";

    @SystemPermission(name = "Update Survey Category", description = "Allows updating existing survey categories.", category = "Survey Management")
    public static final String PERM_UPDATE_SURVEY_CATEGORY = "Update Survey Category";

    @SystemPermission(name = "Update Survey Question", description = "Allows updating existing survey questions.", category = "Survey Management")
    public static final String PERM_UPDATE_SURVEY_QUESTION = "Update Survey Question";

    @SystemPermission(name = "Delete Survey Question", description = "Allows soft deleting survey questions.", category = "Survey Management")
    public static final String PERM_DELETE_SURVEY_QUESTION = "Delete Survey Question";

    @SystemPermission(name = "Review OrgFit Category", description = "Required for users assigned to perform a review of an OrgFit Category.", category = "Survey Management")
    public static final String PERM_REVIEW_ORGFIT_CATEGORY = "Review OrgFit Category";

    // ====================== Performance Tracking and Reporting Permissions ======================
    @SystemPermission(name = "View All Performance Scores", description = "Allows HR/Admins to view all performance scores across all levels (User, Team, Department, Organization).", category = "Performance Tracking")
    public static final String PERM_VIEW_ALL_PERFORMANCE_SCORES = "View All Performance Scores";

    @SystemPermission(name = "View Manager Performance Scores", description = "Allows Managers to view scores for teams/departments they oversee.", category = "Performance Tracking")
    public static final String PERM_VIEW_MANAGER_PERFORMANCE_SCORES = "View Manager Performance Scores";

    @SystemPermission(name = "View Self Performance Score", description = "Allows individual users to view their own performance scores.", category = "Performance Tracking")
    public static final String PERM_VIEW_SELF_PERFORMANCE_SCORE = "Performance Tracking";

    // ====================== Reward Management Permissions ======================
    @SystemPermission(name = "Create Reward", description = "Allows creating rewards for employees.", category = "Reward Management")
    public static final String PERM_CREATE_REWARD = "Create Reward";

    @SystemPermission(name = "Read/View Reward", description = "Allows viewing rewards.", category = "Reward Management")
    public static final String PERM_READ_VIEW_REWARD = "Read/View Reward";

    @SystemPermission(name = "Update Reward", description = "Allows updating reward details.", category = "Reward Management")
    public static final String PERM_UPDATE_REWARD = "Update Reward";

    @SystemPermission(name = "Delete Reward", description = "Allows soft deleting rewards.", category = "Reward Management")
    public static final String PERM_DELETE_REWARD = "Delete Reward";

    @SystemPermission(name = "View Self Rewards", description = "Employees can view their own rewards.", category = "Reward Management")
    public static final String PERM_VIEW_SELF_REWARDS = "View Self Rewards";

    @SystemPermission(name = "View Manager Rewards", description = "Supervisors/Leads can view rewards for their direct reports/teams/departments.", category = "Reward Management")
    public static final String PERM_VIEW_MANAGER_REWARDS = "View Manager Rewards";

    // ====================== PIP (Performance Improvement Plan) Management Permissions ======================
    @SystemPermission(name = "Create PIP", description = "Allows initiating a Performance Improvement Plan.", category = "PIP Management")
    public static final String PERM_CREATE_PIP = "Create PIP";

    @SystemPermission(name = "Read/View PIP", description = "Allows viewing PIPs.", category = "PIP Management")
    public static final String PERM_READ_VIEW_PIP = "Read/View PIP";

    @SystemPermission(name = "Update PIP", description = "Allows updating PIP progress or status.", category = "PIP Management")
    public static final String PERM_UPDATE_PIP = "Update PIP";

    @SystemPermission(name = "Delete PIP", description = "Allows deleting a PIP.", category = "PIP Management")
    public static final String PERM_DELETE_PIP = "Delete PIP";

}
