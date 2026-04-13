/**
 * INTERACTIVE JOB APPLICATION SYSTEM
 * Design Pattern: Chain of Responsibility
 */

import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class JobApplication {
    private String candidateName;
    private String email;
    private int yearsOfExperience;
    private double communicationScore;
    private double technicalScore;
    private String programmingLanguages;
    private String appliedPosition;
    private String applicationStatus;
    private String rejectionReason;
    private List<String> filterHistory;
    private LocalDateTime appliedTime;

    public JobApplication() {
        this.applicationStatus = "PENDING";
        this.rejectionReason = "";
        this.filterHistory = new ArrayList<>();
        this.appliedTime = LocalDateTime.now();
    }

    public void setCandidateName(String name) { this.candidateName = name; }
    public void setEmail(String email) { this.email = email; }
    public void setYearsOfExperience(int years) { this.yearsOfExperience = years; }
    public void setCommunicationScore(double score) { this.communicationScore = score; }
    public void setTechnicalScore(double score) { this.technicalScore = score; }
    public void setProgrammingLanguages(String langs) { this.programmingLanguages = langs; }
    public void setAppliedPosition(String position) { this.appliedPosition = position; }

    public String getCandidateName() { return candidateName; }
    public String getEmail() { return email; }
    public int getYearsOfExperience() { return yearsOfExperience; }
    public double getCommunicationScore() { return communicationScore; }
    public double getTechnicalScore() { return technicalScore; }
    public String getProgrammingLanguages() { return programmingLanguages; }
    public String getAppliedPosition() { return appliedPosition; }
    public String getApplicationStatus() { return applicationStatus; }
    public String getRejectionReason() { return rejectionReason; }
    public List<String> getFilterHistory() { return filterHistory; }

    public void setApplicationStatus(String status) { this.applicationStatus = status; }
    public void setRejectionReason(String reason) { this.rejectionReason = reason; }
    public void addToFilterHistory(String filterName) { this.filterHistory.add(filterName); }

    public boolean isValid() {
        return candidateName != null && !candidateName.trim().isEmpty() &&
               email != null && !email.trim().isEmpty() &&
               yearsOfExperience >= 0 &&
               communicationScore >= 0 && communicationScore <= 10 &&
               technicalScore >= 0 && technicalScore <= 10 &&
               programmingLanguages != null && !programmingLanguages.trim().isEmpty() &&
               appliedPosition != null && !appliedPosition.trim().isEmpty();
    }
}

abstract class ApplicationHandler {
    protected ApplicationHandler nextHandler;

    public void setNextHandler(ApplicationHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    public final void handle(JobApplication application) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("[" + getHandlerName() + "]");
        System.out.println("=".repeat(70));
        
        if (canHandle(application)) {
            application.addToFilterHistory(this.getClass().getSimpleName());
            if (nextHandler != null) {
                System.out.println("[PASS] Moving to next filter...");
                nextHandler.handle(application);
            } else {
                System.out.println("[PASS] ALL CHECKS COMPLETE!");
            }
        } else {
            System.out.println("[REJECT] REJECTED AT THIS STAGE");
        }
    }

    protected abstract boolean canHandle(JobApplication application);
    public abstract String getHandlerName();
}

class HRFilter extends ApplicationHandler {
    private static final int MIN_EXPERIENCE = 2;
    private static final double MIN_COMMUNICATION_SCORE = 6.0;

    public String getHandlerName() { return "HR FILTER - Screening"; }

    protected boolean canHandle(JobApplication application) {
        System.out.println("\nCandidate: " + application.getCandidateName());
        
        System.out.println("\n[CHECK 1] Experience");
        System.out.println("  Required: " + MIN_EXPERIENCE + " years");
        System.out.println("  Candidate: " + application.getYearsOfExperience() + " years");
        
        if (application.getYearsOfExperience() < MIN_EXPERIENCE) {
            application.setApplicationStatus("REJECTED");
            application.setRejectionReason("Insufficient experience");
            System.out.println("  Result: FAILED");
            return false;
        }
        System.out.println("  Result: PASSED");

        System.out.println("\n[CHECK 2] Communication");
        System.out.println("  Required: " + MIN_COMMUNICATION_SCORE + "/10");
        System.out.println("  Score: " + application.getCommunicationScore() + "/10");
        
        if (application.getCommunicationScore() < MIN_COMMUNICATION_SCORE) {
            application.setApplicationStatus("REJECTED");
            application.setRejectionReason("Low communication skills");
            System.out.println("  Result: FAILED");
            return false;
        }
        System.out.println("  Result: PASSED");
        
        return true;
    }
}

class TechnicalFilter extends ApplicationHandler {
    private static final double MIN_TECHNICAL_SCORE = 7.0;
    private static final String[] REQUIRED_LANGUAGES = {"Java", "Python", "JavaScript"};

    public String getHandlerName() { return "TECHNICAL FILTER - Skills"; }

    protected boolean canHandle(JobApplication application) {
        System.out.println("\n[CHECK 1] Technical Score");
        System.out.println("  Required: " + MIN_TECHNICAL_SCORE + "/10");
        System.out.println("  Score: " + application.getTechnicalScore() + "/10");
        
        if (application.getTechnicalScore() < MIN_TECHNICAL_SCORE) {
            application.setApplicationStatus("REJECTED");
            application.setRejectionReason("Technical score below threshold");
            System.out.println("  Result: FAILED");
            return false;
        }
        System.out.println("  Result: PASSED");

        System.out.println("\n[CHECK 2] Languages");
        System.out.println("  Required: Java, Python, or JavaScript");
        System.out.println("  Have: " + application.getProgrammingLanguages());
        
        boolean hasRequired = false;
        for (String lang : REQUIRED_LANGUAGES) {
            if (application.getProgrammingLanguages().toUpperCase().contains(lang.toUpperCase())) {
                hasRequired = true;
                break;
            }
        }
        
        if (!hasRequired) {
            application.setApplicationStatus("REJECTED");
            application.setRejectionReason("Missing required languages");
            System.out.println("  Result: FAILED");
            return false;
        }
        System.out.println("  Result: PASSED");
        
        return true;
    }
}

class ManagerApprovalFilter extends ApplicationHandler {
    private static final double APPROVAL_THRESHOLD = 7.5;

    public String getHandlerName() { return "MANAGER APPROVAL - Final"; }

    protected boolean canHandle(JobApplication application) {
        double overallScore = (application.getTechnicalScore() * 0.6) + 
                             (application.getCommunicationScore() * 0.4);
        
        System.out.println("\n[Final Check] Overall Assessment");
        System.out.println("  Formula: (Tech*0.6) + (Comm*0.4)");
        System.out.println("  = (" + application.getTechnicalScore() + "*0.6) + " +
                          "(" + application.getCommunicationScore() + "*0.4)");
        System.out.println("  = " + String.format("%.2f", overallScore) + "/10");
        System.out.println("  Threshold: " + APPROVAL_THRESHOLD + "/10");
        
        if (overallScore >= APPROVAL_THRESHOLD) {
            application.setApplicationStatus("APPROVED");
            System.out.println("  Result: APPROVED!");
            return true;
        } else {
            application.setApplicationStatus("REJECTED");
            application.setRejectionReason("Score below threshold");
            System.out.println("  Result: REJECTED - Score too low");
            return false;
        }
    }
}

class JobApplicationProcessor {
    private ApplicationHandler chain;
    private List<JobApplication> allApplications;

    public JobApplicationProcessor() {
        HRFilter hrFilter = new HRFilter();
        TechnicalFilter technicalFilter = new TechnicalFilter();
        ManagerApprovalFilter managerFilter = new ManagerApprovalFilter();

        hrFilter.setNextHandler(technicalFilter);
        technicalFilter.setNextHandler(managerFilter);
        
        this.chain = hrFilter;
        this.allApplications = new ArrayList<>();
    }

    public void processApplication(JobApplication application) {
        System.out.println("\n" + "*".repeat(70));
        System.out.println("*  NEW APPLICATION");
        System.out.println("*".repeat(70));
        System.out.println("Submitted: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("Name: " + application.getCandidateName());
        System.out.println("Position: " + application.getAppliedPosition());
        
        chain.handle(application);
        allApplications.add(application);
        displayResult(application);
    }

    private void displayResult(JobApplication application) {
        System.out.println("\n" + "*".repeat(70));
        System.out.println("*  DECISION");
        System.out.println("*".repeat(70));
        
        System.out.println("Name: " + application.getCandidateName());
        System.out.println("Status: " + application.getApplicationStatus());
        
        if ("REJECTED".equals(application.getApplicationStatus())) {
            System.out.println("Reason: " + application.getRejectionReason());
        } else {
            System.out.println("Congratulations! Your application is APPROVED!");
        }
        
        System.out.println("Filters Applied: " + application.getFilterHistory());
        System.out.println("*".repeat(70) + "\n");
    }

    public void displayAllApplications() {
        System.out.println("\n" + "*".repeat(70));
        System.out.println("*  ALL APPLICATIONS (" + allApplications.size() + ")");
        System.out.println("*".repeat(70));
        
        if (allApplications.isEmpty()) {
            System.out.println("No applications.");
            return;
        }
        
        int approved = 0;
        for (int i = 0; i < allApplications.size(); i++) {
            JobApplication app = allApplications.get(i);
            System.out.println("\n" + (i+1) + ". " + app.getCandidateName() + " - " + app.getApplicationStatus());
            if ("REJECTED".equals(app.getApplicationStatus())) {
                System.out.println("   Reason: " + app.getRejectionReason());
            } else {
                approved++;
            }
        }
        
        int total = allApplications.size();
        System.out.println("\nApproved: " + approved + " | Rejected: " + (total-approved));
        System.out.println("*".repeat(70) + "\n");
    }
}

public class SmartJobApplicationSystem {
    private static Scanner scanner = new Scanner(System.in);
    private static JobApplicationProcessor processor = new JobApplicationProcessor();

    public static void main(String[] args) {
        System.out.println("\n" + "*".repeat(70));
        System.out.println("*  SMART JOB APPLICATION SYSTEM");
        System.out.println("*  Chain of Responsibility Pattern");
        System.out.println("*".repeat(70));
        System.out.println("\nFilters:");
        System.out.println("  1. HR - Experience & Communication");
        System.out.println("  2. Technical - Skills & Languages");
        System.out.println("  3. Manager - Final Approval");
        System.out.println("*".repeat(70));
        
        boolean running = true;
        while (running) {
            System.out.println("\n[MENU]");
            System.out.println("1. Submit Application");
            System.out.println("2. View All");
            System.out.println("3. Exit");
            
            String choice = getInput("Choice: ").trim();
            
            switch (choice) {
                case "1":
                    submitApplication();
                    break;
                case "2":
                    processor.displayAllApplications();
                    break;
                case "3":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid.");
            }
        }
        
        System.out.println("\nThank you!");
        scanner.close();
    }

    private static void submitApplication() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("JOB APPLICATION FORM");
        System.out.println("=".repeat(70));
        
        JobApplication app = new JobApplication();

        String name = getInput("\nName: ").trim();
        if (name.isEmpty()) { System.out.println("Required."); return; }
        app.setCandidateName(name);

        String email = getInput("Email: ").trim();
        if (!email.contains("@")) { System.out.println("Invalid."); return; }
        app.setEmail(email);

        String position = getInput("Position: ").trim();
        if (position.isEmpty()) { System.out.println("Required."); return; }
        app.setAppliedPosition(position);

        int exp = getIntInput("Years of Experience (0-50): ");
        if (exp < 0 || exp > 50) { System.out.println("Invalid."); return; }
        app.setYearsOfExperience(exp);

        double comm = getDoubleInput("Communication Score (0-10): ");
        if (comm < 0 || comm > 10) { System.out.println("Invalid."); return; }
        app.setCommunicationScore(comm);

        double tech = getDoubleInput("Technical Score (0-10): ");
        if (tech < 0 || tech > 10) { System.out.println("Invalid."); return; }
        app.setTechnicalScore(tech);

        String langs = getInput("Languages (comma-separated): ").trim();
        if (langs.isEmpty()) { System.out.println("Required."); return; }
        app.setProgrammingLanguages(langs);

        if (!app.isValid()) { System.out.println("Invalid data."); return; }

        System.out.println("\nProcessing...");
        processor.processApplication(app);
    }

    private static String getInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private static int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number.");
            }
        }
    }

    private static double getDoubleInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid decimal.");
            }
        }
    }
}
