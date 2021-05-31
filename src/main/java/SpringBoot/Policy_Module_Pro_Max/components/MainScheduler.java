package SpringBoot.Policy_Module_Pro_Max.components;

import SpringBoot.Policy_Module_Pro_Max.models.*;
import SpringBoot.Policy_Module_Pro_Max.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Set;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Component
public class MainScheduler {

    @Autowired
    private UserService userService;

    @Autowired
    private RiskService riskService;

    @Autowired
    private PolicyService policyService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private RemedyService remedyService;

    private static final Logger log = LoggerFactory.getLogger(MainScheduler.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRate = 60000L)
    public void performTask() {

        log.info("Main Scheduler Log Time : {}", dateFormat.format(new Date()));
        List<User> allUsers = userService.findAll();

        for(User currentUser : allUsers) {

            String currentUserFullName = currentUser.getFirstName() + " " + currentUser.getLastName();
            String currentUserEmail = currentUser.getEmail();
            String currentUserUsername = currentUser.getUsername();
            String currentUserDirectory = currentUser.getFileDirectory();

            log.info("User's Full Name : {}, Username : {} & Directory : {}", currentUserFullName, currentUserUsername, currentUserDirectory);

            // Fetch All User Policies From The DB
            List<Policy> currentUserPolicies = policyService.getAllPoliciesByCreator(currentUser);

            // Fetch All The User Risks For Each Policy From The DB
            List<Set<Risk>> currentUserPolicyRiskPairs = new ArrayList<>();
            for(Policy currentPolicy : currentUserPolicies) {
                Set<Risk> currentPolicyEnabledRisks = riskService.getAllRisksByCreatorAndStatusAndPolicy(currentUser, true, currentPolicy);
                currentUserPolicyRiskPairs.add(currentPolicyEnabledRisks);
            }

            File currentDir = new File(".");
            File userDirectoryPath = new File(currentDir + currentUserDirectory);
            File userFileList[] = userDirectoryPath.listFiles();

            // Now Iterate Over Each File From User's Directory And
            // Do All The Required Stuff In One Go !
            for(File currentFile : userFileList) {
                // Fetch File Details
                Path path = Paths.get("" + currentFile);
                BasicFileAttributes fileAttributes;
                try {
                    fileAttributes = Files.readAttributes(path, BasicFileAttributes.class);
                    Boolean isDirectory = fileAttributes.isDirectory();
                    if(!isDirectory) {
                        FileTime fileCreationTime = fileAttributes.creationTime();
                        FileTime fileLastModifiedTime = fileAttributes.lastModifiedTime();

                        Long currentTimeInMillis = Calendar.getInstance().getTimeInMillis();
                        Long fileCreationTimeInMillis = fileCreationTime.toMillis();
                        Long fileLastModifiedTimeInMillis = fileLastModifiedTime.toMillis();
                        Long timeInHoursFilesNeededBeChecked = 1l; // Hardcoded To 1 Hour Right Now, Can Be Given As An Option To User To Set
                        Long timeInMillisFilesNeededBeChecked = timeInHoursFilesNeededBeChecked * 3600000L;

                        // Condition For Eligible Files :
                        if ((currentTimeInMillis - fileCreationTimeInMillis <= timeInMillisFilesNeededBeChecked) ||
                                (currentTimeInMillis - fileLastModifiedTimeInMillis <= timeInMillisFilesNeededBeChecked)
                        ) {
                            log.info("Eligible : File Name -> {} & File Path -> {}", currentFile.getName(), currentFile.getAbsolutePath());
                            Long timeDiff = Math.min((currentTimeInMillis - fileCreationTimeInMillis), (currentTimeInMillis - fileLastModifiedTimeInMillis));
                            double timeDiffInMinutes = ((double) timeDiff / 60000);
                            log.info("Time Difference (In Minutes) -> {}", timeDiffInMinutes);

                            // For Each Eligible File Initially Consider That It Doesn't Violate Any User Policy
                            boolean overallResultForFile = true;

                            // For Each Eligible File Create A New Activity & Set Few Basic Details
                            Activity currentActivity = new Activity();
                            currentActivity.setActivityLogTime(LocalDateTime.now());
                            currentActivity.setUser(currentUser);
                            currentActivity.setFileName(currentFile.getName());
                            currentActivity.setFileCreationTime(LocalDateTime.ofInstant(fileCreationTime.toInstant(), ZoneId.systemDefault()));
                            currentActivity.setFileLastModifiedTime(LocalDateTime.ofInstant(fileLastModifiedTime.toInstant(), ZoneId.systemDefault()));

                            // List of Activity Details Involved With This Activity
                            List<ActivityDetail> activityDetailList = new ArrayList<>();

                            // Maintain Sets To Later Add Strings Into Current Activity
                            Set<Policy> setPoliciesCheckedAgainst = new HashSet<>();
                            Set<Policy> setPoliciesViolated = new HashSet<>();
                            Set<Policy> setPoliciesNotViolated = new HashSet<>();

                            StringBuilder strPoliciesCheckedAgainst = new StringBuilder();
                            StringBuilder strPoliciesViolated = new StringBuilder();
                            StringBuilder strPoliciesNotViolated = new StringBuilder();

                            // For Each Activity Initialize An Action
                            Remedy currentRemedy = new Remedy();
                            currentRemedy.setActivity(currentActivity);

                            // Initializing A HashMap For Each Eligible File For Keeping Track of Remedy
                            // String : Remedy Type
                            // Integer : Minimum Remedy Time For That Remedy Type
                            HashMap<String, Integer> allRemediesCurrentActivity = new HashMap<>();

                            // Now If File Is Eligible Fetch The Data From It
                            try {
                                Scanner fileScanner = new Scanner(currentFile);
                                String fileTextLine;
                                StringBuilder fileText = new StringBuilder();
                                while (fileScanner.hasNextLine()) {
                                    fileTextLine = fileScanner.nextLine();
                                    fileText.append(fileTextLine).append(" ");
                                }

                                String fileTextString = fileText.toString();
                                String[] fileTextTokens = fileTextString.split("[,;. \t\n\r]++");

                                HashMap<String, Integer> fileTextHashMap = new HashMap<>();

                                // Fetch All String Tokens And Put Them In A HashMap
                                for (String str : fileTextTokens) {
                                    if (!fileTextHashMap.containsKey(str)) {
                                        fileTextHashMap.put(str, 1);
                                    } else {
                                        fileTextHashMap.put(str, fileTextHashMap.get(str) + 1);
                                    }
                                }

                                // Now Check This File (Using fileTextHashMap) For Each Policy
                                Integer riskItr = 0;
                                for (Policy currentPolicy : currentUserPolicies) {

                                    // Add This Policy To Checked Against List For Current File Activity
                                    setPoliciesCheckedAgainst.add(currentPolicy);

                                    // Check if Current Policy Is Violated
                                    boolean isCurrentPolicyViolated = false;

                                    // Create A Activity Detail For Each Activity-Policy (File-Policy) Pair
                                    ActivityDetail currentActivityDetail = new ActivityDetail();
                                    currentActivityDetail.setActivity(currentActivity);
                                    currentActivityDetail.setPolicy(currentPolicy.getPolicyName());

                                    // Maintain Sets To Later Add Strings Into Current Activity Details
                                    Set<Risk> setRisksCheckedAgainst = new HashSet<>();
                                    Set<Risk> setRisksViolated = new HashSet<>();
                                    Set<Risk> setRisksNotViolated = new HashSet<>();

                                    StringBuilder strRisksCheckedAgainst = new StringBuilder();
                                    StringBuilder strRisksViolated = new StringBuilder();
                                    StringBuilder strRisksNotViolated = new StringBuilder();

                                    StringBuilder adminEmails = new StringBuilder();
                                    StringBuilder adminEmailSubjects = new StringBuilder();

                                    // Fetch All Risks For Current Policy From List<Set<Risk>> Prepared Earlier !
                                    Set<Risk> currentPolicyRisks = currentUserPolicyRiskPairs.get(riskItr++);

                                    for (Risk currentRisk : currentPolicyRisks) {

                                        // Add This Risk To Checked Against List For Current File-Policy Activity Detail
                                        setRisksCheckedAgainst.add(currentRisk);

                                        // Check if Current Risk Violates The Policy
                                        boolean isCurrentRiskViolated = false;

                                        // Check For Keyword Violation
                                        boolean isKeywordRisk = false;
                                        if (!currentRisk.getKeyWords().isEmpty()) {
                                            ArrayList<String> keyWordList = new ArrayList<>();
                                            if (currentRisk.getKeyWords() != null) {
                                                String[] tempKeyWords = currentRisk.getKeyWords().split("[,;. \t\n\r]++");
                                                for (String kw : tempKeyWords) {
                                                    if (kw.equals(" ")) continue;
                                                    else {
                                                        keyWordList.add(kw.trim());
                                                    }
                                                }
                                            }
                                            for (int kwItr = 0; kwItr < keyWordList.size(); kwItr++) {
                                                if (fileTextHashMap.containsKey(keyWordList.get(kwItr))) {
                                                    if (fileTextHashMap.get(keyWordList.get(kwItr)) >= currentRisk.getRiskMatchCount()) {
                                                        isKeywordRisk = true;
                                                    }
                                                }
                                            }
                                        }

                                        // Check For Regex Violation
                                        boolean isRegexRisk = false;
                                        if (!currentRisk.getRegex().isEmpty()) {
                                            Pattern pattern = Pattern.compile(currentRisk.getRegex());
                                            for (String str : fileTextTokens) {
                                                Matcher matcher = pattern.matcher(str);
                                                boolean matchFound = matcher.find();
                                                if (matchFound) {
                                                    isRegexRisk = true;
                                                }
                                            }
                                        }

                                        // If There Is Either Keyword Risk Or Regex Risk Or Both Then Risk Is Violated
                                        if (isKeywordRisk || isRegexRisk) {
                                            isCurrentRiskViolated = true;
                                            setRisksViolated.add(currentRisk);
                                        } else {
                                            setRisksNotViolated.add(currentRisk);
                                        }

                                        // Even If One Risk Is Violated Then The Policy Is Violated
                                        if (isCurrentRiskViolated) {
                                            isCurrentPolicyViolated = true;
                                        }
                                    } // Risk Loop Ends Here

                                    if (isCurrentPolicyViolated) {
                                        overallResultForFile = false;
                                        setPoliciesViolated.add(currentPolicy);
                                        currentActivityDetail.setPolicyResult("Violated");

                                        if(currentPolicy.getNotifyAdmin()) {
                                            adminEmails.append(currentPolicy.getAdminEmail()).append(" ");
                                            if(currentPolicy.getAdminEmailSubject().isEmpty()) {
                                                adminEmailSubjects.append("Policy Violation Alert For File ").append(currentFile.getName()).append(",");
                                            } else {
                                                adminEmailSubjects.append(currentPolicy.getAdminEmailSubject()).append(",");
                                            }
                                        }

                                    } else {
                                        setPoliciesNotViolated.add(currentPolicy);
                                        currentActivityDetail.setPolicyResult("Not Violated");
                                    }

                                    // Generate The Strings Using The Sets
                                    for(Risk risk : setRisksCheckedAgainst) {
                                        strRisksCheckedAgainst.append(risk.getTitle()).append(", ");
                                    }
                                    for(Risk risk : setRisksViolated) {
                                        strRisksViolated.append(risk.getTitle()).append(", ");
                                    }
                                    for(Risk risk : setRisksNotViolated) {
                                        strRisksNotViolated.append(risk.getTitle()).append(", ");
                                    }

                                    if(!setRisksCheckedAgainst.isEmpty()) currentActivityDetail.setRisksCheckedAgainst(strRisksCheckedAgainst.toString().substring(0, strRisksCheckedAgainst.toString().length() - 2));
                                    if(!setRisksViolated.isEmpty()) currentActivityDetail.setRisksViolated(strRisksViolated.toString().substring(0, strRisksViolated.toString().length() - 2));
                                    if(!setRisksNotViolated.isEmpty()) currentActivityDetail.setRisksNotViolated(strRisksNotViolated.toString().substring(0, strRisksNotViolated.toString().length() - 2));

                                    // Save Activity Detail For Each Activity-Policy (File-Policy Pair)
                                    // And Also Add It To The Activity
                                    // Can't Save A Child Before Saving It's Parent !!??
                                    // activityDetailService.saveActivityDetail(currentActivityDetail);
                                    // Can't Save One Activity Detail At A Time
                                    // currentActivity.addActivityDetail(currentActivityDetail); // Bidirectional Sync (To Rescue ! For Now !)
                                    activityDetailList.add(currentActivityDetail);

                                    // Manage Remedy (Action) For Current Policy
                                    String currentPolicyRemedyType = currentPolicy.getRemedyType().toLowerCase();
                                    Integer currentPolicyRemedyTime = currentPolicy.getRemedyTime();
                                    if (!allRemediesCurrentActivity.containsKey(currentPolicyRemedyType)) {
                                        allRemediesCurrentActivity.put(currentPolicyRemedyType, currentPolicyRemedyTime);
                                    } else {
                                        allRemediesCurrentActivity.put(currentPolicyRemedyType, Math.min(allRemediesCurrentActivity.get(currentPolicyRemedyType), currentPolicyRemedyTime));
                                    }

                                    // Adding Mailing Related Info To Remedy
                                    currentRemedy.setAdminEmails(adminEmails.toString());
                                    currentRemedy.setAdminEmailSubjects(adminEmailSubjects.toString());
                                    if(currentPolicy.getNotifyUser()) {
                                        currentRemedy.setUserEmail(currentUserEmail);
                                    }

                                } // Policy Loop Ends Here

                                // Generate The Strings Using The Sets
                                for(Policy policy : setPoliciesCheckedAgainst) {
                                    strPoliciesCheckedAgainst.append(policy.getPolicyName()).append(", ");
                                }
                                for(Policy policy : setPoliciesViolated) {
                                    strPoliciesViolated.append(policy.getPolicyName()).append(", ");
                                }
                                for(Policy policy : setPoliciesNotViolated) {
                                    strPoliciesNotViolated.append(policy.getPolicyName()).append(", ");
                                }
                                if(!setPoliciesCheckedAgainst.isEmpty()) currentActivity.setPoliciesCheckedAgainst(strPoliciesCheckedAgainst.toString().substring(0, strPoliciesCheckedAgainst.toString().length() - 2));
                                if(!setPoliciesViolated.isEmpty()) currentActivity.setPoliciesViolated(strPoliciesViolated.toString().substring(0, strPoliciesViolated.toString().length() - 2));
                                if(!setPoliciesNotViolated.isEmpty()) currentActivity.setPoliciesNotViolated(strPoliciesNotViolated.toString().substring(0, strPoliciesNotViolated.toString().length() - 2));

                                // Save Overall Result
                                if (!overallResultForFile) {
                                    currentActivity.setOverallResult("Violated");
                                } else {
                                    currentActivity.setOverallResult("Not Violated");
                                }

                                // Manage Remedy (Action) For The Entire Activity
                                // Remedy Type Priority
                                // 1.Delete, 2.Move, 3.Rename, 4.Notify(Email)
                                String actionType;
                                Integer actionTime;

                                if (allRemediesCurrentActivity.containsKey("delete")) {
                                    actionType = "delete";
                                    actionTime = allRemediesCurrentActivity.get("delete");
                                } else if (allRemediesCurrentActivity.containsKey("move")) {
                                    actionType = "move";
                                    actionTime = allRemediesCurrentActivity.get("move");
                                } else if (allRemediesCurrentActivity.containsKey("rename")) {
                                    actionType = "rename";
                                    actionTime = allRemediesCurrentActivity.get("rename");
                                } else if (allRemediesCurrentActivity.containsKey("notify")) {
                                    actionType = "notify";
                                    actionTime = allRemediesCurrentActivity.get("notify");
                                } else {
                                    actionType = "unknown";
                                    actionTime = 0;
                                }
                                // Set Current Action's Information
                                currentRemedy.setActionType(actionType);
                                currentRemedy.setActionTime(currentActivity.getActivityLogTime().plusMinutes(actionTime));
                                currentRemedy.setStatus(false);
                                currentRemedy.setFilePath("./files/" + currentUserUsername + "/remedy/" + currentFile.getName());

                                // Save Action
                                remedyService.saveRemedy(currentRemedy);

                                // Also Move This File To Remedy Sub-Folder
                                String src = "./files/" + currentUserUsername + "/" + currentFile.getName();
                                String dst = "./files/" + currentUserUsername + "/remedy/" + currentFile.getName();

                                Path isMoved = Files.move(Paths.get(src), Paths.get(dst), StandardCopyOption.REPLACE_EXISTING);
                                if(isMoved != null) {
                                    log.info(currentFile.getName() + " Moved To Remedy Sub-Folder");
                                } else {
                                    log.info("Unable To Move " + currentFile.getName() + " To Remedy Sub-Folder");
                                }

                                // Save Activity Details & Activity For Each File
                                currentActivity.addActivityDetails(activityDetailList); // Bidirectional Sync
                                activityService.saveActivity(currentActivity);

                            } catch (FileNotFoundException err) {
                                System.out.println("Some Error Fetching The File : " + err.getMessage());
                            }
                        } // Eligible File Condition Ends Here
                    } // If Is Directory
                } catch(IOException err) { System.out.println("Some Error Fetching File : " + err.getMessage()); }
            } // File Loop Ends Here
        } // User Loop Ends Here
    } // Scheduler Loop Ends Here
} // Component (Class) Loop Ends Here

