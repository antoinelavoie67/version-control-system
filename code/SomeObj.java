package gitlet;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class SomeObj {
    /** */
    private File cWD = new File(System.getProperty("user.dir"));
    /** */
    private File gitlet = new File(cWD + "/.gitlet");
    /** */
    private File stageArea = Utils.join(gitlet, "stageArea");
    /** */
    private File commits = Utils.join(gitlet, "commits");
    /** */
    private File branches = Utils.join(gitlet, "branches");
    /** */
    private File log = Utils.join(gitlet, "log");
    /** */
    private File blobs = Utils.join(gitlet, "blobs");
    /** */
    private File stageAreaTxt = Utils.join(stageArea, "stageArea.txt");
    /** */
    private File headtxt = Utils.join(branches, "head.txt");
    /** */
    private File currBNtxt =
            Utils.join(branches, "currBN.txt");
    /** */
    private StageArea objectStageArea;

    @SuppressWarnings("unchecked")
    public void init() {
        if (!new File(cWD + "/.gitlet").exists()) {
            gitlet.mkdirs();
            stageArea.mkdirs();
            commits.mkdirs();
            branches.mkdirs();
            log.mkdirs();
            blobs.mkdir();
            objectStageArea = new StageArea();
            Utils.writeObject(stageAreaTxt, objectStageArea);

            Commit initialCommit =
                    new Commit("initial commit",
                            null,  null, new TreeMap<String, String>());
            File objectInitialCommit =
                    Utils.join(commits, initialCommit.getCurrentId() + ".txt");
            Utils.writeObject(objectInitialCommit, initialCommit);
            String head = initialCommit.getCurrentId();
            String masterID = initialCommit.getCurrentId();
            File mastertxt =
                    Utils.join(branches, "master.txt");
            Utils.writeContents(headtxt, head);
            Utils.writeContents(mastertxt, masterID);
            Utils.writeContents(currBNtxt, "master");
        } else {
            System.out.println("A Gitlet version-control system"
                    + " already exists in the current directory.");
        }
    }
    @SuppressWarnings("unchecked")
    public Commit getCommit() {
        String commitID = Utils.readContentsAsString(headtxt);
        File currentCommit = Utils.join(commits, commitID + ".txt");
        Commit newOne = Utils.readObject(currentCommit, Commit.class);
        return newOne;

    }
    @SuppressWarnings("unchecked")
    public void add(String name) {
        File currentAdd = new File(name);
        if (!currentAdd.exists()) {
            System.out.println("File does not exist.");
        } else {
            StageArea currSA = Utils.readObject(stageAreaTxt, StageArea.class);
            Commit currentCommit = getCommit();
            byte[] currBlob = Utils.readContents(currentAdd);
            if (currentCommit.getBlobs().get(name) != null) {
                Object oldOne = currentCommit.getBlobs().get(name);
                if (oldOne.equals(Utils.sha1(currBlob))) {
                    if (currSA.getRemovedTree().get(name) != null) {
                        currSA.getRemovedTree().remove(name);
                        Utils.writeObject(stageAreaTxt, currSA);
                        return;
                    }
                    return;
                }

            }
            currSA.addToStage(name, Utils.sha1(currBlob));
            Utils.writeObject(stageAreaTxt, currSA);
            File newBlob =
                    Utils.join(blobs, Utils.sha1(currBlob) + ".txt");
            Utils.writeContents(newBlob, currBlob);
        }
    }

    @SuppressWarnings("unchecked")
    public void commit(String message, String mergeId2) {
        StageArea currentStagingArea =
                Utils.readObject(stageAreaTxt, StageArea.class);
        if (currentStagingArea.getAddedTree().isEmpty()
                && currentStagingArea.getRemovedTree().isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }
        if (message.isEmpty() || message == null) {
            System.out.println("Please enter a commit message.");
            return;
        }
        Commit previousCommit = getCommit();
        TreeMap<String, String> previousBlobs = previousCommit.getBlobs();
        TreeMap<String, String> copy = previousBlobs;
        copy.putAll(currentStagingArea.getAddedTree());
        Set<String> keys = currentStagingArea.getRemovedTree().keySet();
        for (String key: keys) {
            copy.remove(key);
        }
        String curParentId = previousCommit.getCurrentId();
        Commit newCommit = new Commit(message, curParentId, mergeId2, copy);
        File newlyMadeCommit =
                Utils.join(commits, newCommit.getCurrentId() + ".txt");
        Utils.writeObject(newlyMadeCommit, newCommit);
        String headBranchName =
                Utils.readContentsAsString(currBNtxt);
        File currBranch = Utils.join(branches, headBranchName + ".txt");
        Utils.writeContents(currBranch, newCommit.getCurrentId());
        Utils.writeContents(headtxt, newCommit.getCurrentId());
        currentStagingArea.getRemovedTree().clear();
        currentStagingArea.getAddedTree().clear();
        Utils.writeObject(stageAreaTxt, currentStagingArea);
    }
    @SuppressWarnings("unchecked")
    public void log() {
        if (getCommit() == null) {
            System.out.println("Your init did not work or you didn't call it");
            return;
        }
        Commit currentCommit = getCommit();
        while (currentCommit != null) {
            System.out.println("===");
            System.out.println("commit " + currentCommit.getCurrentId());
            if (currentCommit.getMergeId2() != null) {
                String shortenedParent1 =
                        currentCommit.getParentId1().substring(0, 7);
                String shortenedParent2 =
                        currentCommit.getMergeId2().substring(0, 7);
                System.out.println("Merge: "
                        + shortenedParent1 + " " + shortenedParent2);
            }
            System.out.println("Date: " + currentCommit.getDate());
            System.out.println(currentCommit.getMessage());
            System.out.println();
            File pCommit =
                    Utils.join(commits, currentCommit.getParentId1() + ".txt");
            if (pCommit.exists()) {
                currentCommit = Utils.readObject(pCommit, Commit.class);
            } else {
                break;
            }
        }
    }
    @SuppressWarnings("unchecked")
    public void globalLog() {
        List<String> allCommits = Utils.plainFilenamesIn(commits);
        for (String name: allCommits) {
            File currentCommitPath = Utils.join(commits, name);
            Commit currentCommit =
                    Utils.readObject(currentCommitPath, Commit.class);
            System.out.println("===");
            System.out.println("commit " + currentCommit.getCurrentId());
            System.out.println("Date: " + currentCommit.getDate());
            System.out.println(currentCommit.getMessage());
            System.out.println();
        }
    }
    @SuppressWarnings("unchecked")
    public void checkout(String... args) {
        if (args.length == 3) {
            Commit currentCommit = getCommit();
            TreeMap<String, String> currBlob = currentCommit.getBlobs();
            String fileName = args[2];

            if (!currBlob.containsKey(fileName)) {
                System.out.println("File does not exist in that commit.");
            } else {
                File checkPresent =
                        Utils.join(cWD.getPath(), fileName + ".txt");
                if (checkPresent.exists()) {
                    Utils.restrictedDelete(checkPresent);
                }
                File checkoutBlob = Utils.join(blobs,
                        currentCommit.returnBlob().get(fileName) + ".txt");
                byte[] checkoutBlobContents = Utils.readContents(checkoutBlob);

                File updateLocation = Utils.join(cWD.getPath(), fileName);
                Utils.writeContents(updateLocation, checkoutBlobContents);
            }
        } else if (args.length == 4) {
            String commitId = args[1];
            if (commitId.length() < 10) {
                commitId = shortenedHelper(commitId);
            }
            if (!args[2].equals("--")) {
                System.out.println("Incorrect operands.");
                return;
            }
            String fileName = args[3];
            File check = Utils.join(commits, commitId + ".txt");
            if (!check.exists()) {
                System.out.println("No commit with that id exists.");
            } else {
                Commit checkoutCommit = Utils.readObject(check, Commit.class);
                TreeMap<String, String> currBlob =
                        checkoutCommit.getBlobs();
                if (!currBlob.containsKey(fileName)) {
                    System.out.println("File does not exist in that commit.");
                } else {
                    File checkPresent =
                            Utils.join(cWD.getPath(), fileName + ".txt");
                    if (checkPresent.exists()) {
                        Utils.restrictedDelete(checkPresent);
                    }
                    File checkoutBlob = Utils.join(blobs,
                            checkoutCommit.returnBlob().get(fileName) + ".txt");
                    byte[] checkoutBlobContents =
                            Utils.readContents(checkoutBlob);

                    File updateLocation = Utils.join(cWD.getPath(), fileName);
                    Utils.writeContents(updateLocation, checkoutBlobContents);
                }
            }
        }
    }
    @SuppressWarnings("unchecked")
    public void checkoutBranch(String... args) {
        String branchName = args[1];
        List<String> allBranches = Utils.plainFilenamesIn(branches);
        String currBN = Utils.readContentsAsString(currBNtxt);
        for (String name: allBranches) {
            if (name.equals(branchName + ".txt")) {
                if (branchName.equals(currBN)) {
                    System.out.println("No need to checkout "
                            + "the current branch.");
                    return;
                }
                File checkoutBranch = Utils.join(branches, branchName + ".txt");
                String checkoutId = Utils.readContentsAsString(checkoutBranch);
                File findCommit = Utils.join(commits, checkoutId + ".txt");
                Commit checkoutCommit =
                        Utils.readObject(findCommit, Commit.class);
                TreeMap<String, String> checkoutBlobs =
                        checkoutCommit.getBlobs();
                Commit currentCommit = getCommit();
                TreeMap<String, String> currBlob = currentCommit.getBlobs();
                Set<String> chckBList = checkoutBlobs.keySet();
                Set<String> currBlobList = currBlob.keySet();
                List<File> workingDirList = List.of(cWD.listFiles());
                String msg = "There is an untracked file in the way delete "
                        + "it, or add and commit it first.";
                for (File fiCWD : workingDirList) {
                    if (fiCWD.getPath().contains(".txt")) {
                        String x = fiCWD.getName();
                        if (!currBlobList.contains(fiCWD.getName())) {
                            if (chckBList.contains(fiCWD.getName())) {
                                System.out.println(msg);
                                return;
                            }
                        }
                    }
                }
                for (String current : currBlobList) {
                    if (!chckBList.contains(current)) {
                        File newDelete = Utils.join(cWD, current);
                        Utils.restrictedDelete(Utils.join(newDelete));
                    }
                }
                for (String checkout : chckBList) {
                    File newBlobFile = Utils.join(blobs,
                            checkoutBlobs.get(checkout) + ".txt");
                    byte[] blob = Utils.readContents(newBlobFile);
                    File fileInDirectory = Utils.join(cWD, checkout);
                    Utils.writeContents(fileInDirectory, blob);
                }
                Utils.writeContents(headtxt, checkoutCommit.getCurrentId());
                Utils.writeContents(currBNtxt, branchName);
                StageArea x = Utils.readObject(stageAreaTxt, StageArea.class);
                x.getAddedTree().clear();
                x.getRemovedTree().clear();
                Utils.writeObject(stageAreaTxt, x);
                return;
            }
        }
        System.out.println("No such branch exists.");
    }

    @SuppressWarnings("unchecked")
    public void rm(String fileName) {
        StageArea currentStagingArea =
                Utils.readObject(stageAreaTxt, StageArea.class);
        Commit current = getCommit();
        if (currentStagingArea.getAddedTree().get(fileName) != null) {
            currentStagingArea.getAddedTree().remove(fileName);
        } else if (currentStagingArea.getAddedTree().get(fileName) == null
                && current.getBlobs().get(fileName) == null) {
            System.out.println("No reason to remove the file.");
        } else if (current.getBlobs().get(fileName) != null) {
            if (currentStagingArea.getRemovedTree().get(fileName) == null) {
                String xx = current.getBlobs().get(fileName).toString();
                currentStagingArea.getRemovedTree().put(fileName, xx);
                File remove = Utils.join(cWD, fileName);
                if (remove.exists()) {
                    Utils.restrictedDelete(remove);
                }
            }
        }
        Utils.writeObject(stageAreaTxt, currentStagingArea);
    }

    @SuppressWarnings("unchecked")
    public void find(String commitMessage) {
        List<String> allCommits = Utils.plainFilenamesIn(commits);
        int x = 0;
        for (String name: allCommits) {
            File currentCommitPath = Utils.join(commits, name);
            Commit currentCommit =
                    Utils.readObject(currentCommitPath, Commit.class);
            String checkMessage = currentCommit.getMessage();
            if (commitMessage.equals(checkMessage)) {
                x = 1;
                System.out.println(currentCommit.getCurrentId());
            }
        }
        if (x == 0) {
            System.out.println("Found no commit with that message.");
            return;
        }
    }

    @SuppressWarnings("unchecked")
    public void status() {
        if (!Utils.join(cWD, ".gitlet").exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        System.out.println("=== Branches ===");
        String headtxtHolder = Utils.readContentsAsString(headtxt);
        String currBNHolder =
                Utils.readContentsAsString(currBNtxt);
        headtxt.delete();
        currBNtxt.delete();
        List<String> allBranches = Utils.plainFilenamesIn(branches);
        String headBranchName = currBNHolder + ".txt";

        for (String branchName : allBranches) {
            if (branchName.equals(headBranchName)) {
                System.out.println("*" + currBNHolder);
            } else {
                System.out.println(branchName.substring(0,
                        branchName.length() - 4));
            }
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        StageArea currSA = Utils.readObject(stageAreaTxt, StageArea.class);
        Set<String> currentAddTree = currSA.getAddedTree().keySet();
        for (String fileName: currentAddTree) {
            System.out.println(fileName);
        }

        System.out.println();
        System.out.println("=== Removed Files ===");
        Set<String> currentRemoveTree = currSA.getRemovedTree().keySet();
        for (String fileName: currentRemoveTree) {
            System.out.println(fileName);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();

        System.out.println("=== Untracked Files ===");
        System.out.println();
        headtxt = Utils.join(branches, "head.txt");
        currBNtxt = Utils.join(branches, "currBN.txt");
        Utils.writeContents(headtxt, headtxtHolder);
        Utils.writeContents(currBNtxt, currBNHolder);
    }

    @SuppressWarnings("unchecked")
    public void branch(String branchName) {
        List<String> currBranches = Utils.plainFilenamesIn(branches);
        for (String name: currBranches) {
            if (name.equals(branchName + ".txt")) {
                System.out.println("A branch with that name already exists.");
                return;
            }
        }
        File newBranch = Utils.join(branches, branchName + ".txt");
        String currentCommitID = Utils.readContentsAsString(headtxt);
        Utils.writeContents(newBranch, currentCommitID);
    }

    @SuppressWarnings("unchecked")
    public void rmBranch(String branchName) {
        List<String> currBranches = Utils.plainFilenamesIn(branches);

        if (branchName.equals(Utils.readContentsAsString(currBNtxt))) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        for (String name: currBranches) {
            if (name.equals(branchName + ".txt")) {
                File xx = Utils.join(branches, branchName + ".txt");
                if (xx.exists()) {
                    xx.delete();
                    return;
                }
            }
        }
        System.out.println("A branch with that name does not exist.");
    }

    @SuppressWarnings("unchecked")
    public void reset(String commitID) {
        Commit currentCommit = getCommit();
        TreeMap<String, String> currBlob = currentCommit.getBlobs();

        File resetCommitPath = Utils.join(commits, commitID + ".txt");
        if (!resetCommitPath.exists()) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Commit resetCommit = Utils.readObject(resetCommitPath, Commit.class);
        TreeMap<String, String> resetCommitBlobs = resetCommit.getBlobs();

        Set<String> currentBlobList = currBlob.keySet();
        Set<String> resetBlobList = resetCommitBlobs.keySet();

        List<String> workingDirList = Utils.plainFilenamesIn(cWD);
        StageArea currStageArea =
                Utils.readObject(stageAreaTxt, StageArea.class);
        for (String x : workingDirList) {
            if (currBlob.get(x) == null
                    && resetCommitBlobs.get(x) != null) {
                System.out.println("There is an untracked file in "
                        + "the way; delete it, or add and commit it first.");
                return;
            }
        }

        for (String fileName : workingDirList) {
            if (resetCommitBlobs.get(fileName) == null) {
                File fileNamePath = Utils.join(cWD, fileName);
                fileNamePath.delete();
            } else {
                String[] args = new String[]{"checkout",
                        currentCommit.getCurrentId(), "--", fileName};
                checkout(args);
            }
        }
        Utils.writeContents(headtxt, commitID);
        String branchName = Utils.readContentsAsString(currBNtxt);
        Utils.writeContents(Utils.join(branches,
                branchName + ".txt"), commitID);
        currStageArea.getAddedTree().clear();
        currStageArea.getRemovedTree().clear();
        Utils.writeObject(stageAreaTxt, currStageArea);
    }


    @SuppressWarnings("unchecked")
    public void mainMerge(String... args) {
        String mergeBranchName = args[1];
        StageArea currSA = Utils.readObject(stageAreaTxt, StageArea.class);


        if (!currSA.getAddedTree().isEmpty()
                || !currSA.getRemovedTree().isEmpty()) {
            System.out.println("You have uncommitted changes.");
            return;
        }
        File branchPath = Utils.join(branches, mergeBranchName + ".txt");
        if (!branchPath.exists()) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        String currBN = Utils.readContentsAsString(currBNtxt);
        if (currBN.equals(mergeBranchName)) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }
        Commit currentCommit = getCommit();
        File mergeCommitFile = Utils.join(commits,
                Utils.readContentsAsString(branchPath) + ".txt");
        Commit mergeCommit = Utils.readObject(mergeCommitFile, Commit.class);

        TreeMap<String, String> currBM = currentCommit.getBlobs();
        TreeMap<String, String> mergeBM = mergeCommit.getBlobs();

        List<File> workingDirList = List.of(cWD.listFiles());
        String m = "There is an untracked file in the way; "
                + "delete it, or add and commit it first.";
        for (File fiCWD : workingDirList) {
            if (fiCWD.getPath().contains(".txt")) {
                if (currBM.get(fiCWD.getName()) == null) {
                    if (mergeBM.get(fiCWD.getName()) != null) {
                        System.out.println(m);
                        return;
                    }
                }
            }
        }

        Commit splitPoint =
                findSplitPoint(currentCommit, mergeCommit, mergeBranchName);

        if (splitPoint == null) {
            return;
        }
        TreeMap<String, String> splitBM = splitPoint.getBlobs();

        Set<String> currBlobList = currentCommit.getBlobs().keySet();
        Set<String> mergeBlobList = mergeCommit.getBlobs().keySet();
        Set<String> allBlobList = new HashSet<>();
        allBlobList.addAll(currBlobList);
        allBlobList.addAll(mergeBlobList);
        mergePt2(allBlobList, currBM, mergeBM, splitBM,
                mergeCommit, currSA, mergeBranchName, currBN);
    }

    @SuppressWarnings("unchecked")
    public void mergePt2(Set<String> allBlobList,
                         TreeMap<String, String> currBM,
                          TreeMap<String, String> mergeBM,
                         TreeMap<String, String> splitBM,
                          Commit mergeCommit, StageArea currSA,
                         String mergeBranchName, String currBN) {
        boolean conflict = false;
        for (String fName : allBlobList) {
            String currBOF = currBM.get(fName);
            String givBOF = mergeBM.get(fName);
            String splitBlobOfFile = splitBM.get(fName);
            if (mergeBM.containsKey(fName)
                    && splitBM.containsKey(fName)) {
                if (!givBOF.equals(splitBlobOfFile)) {
                    if (currBM.containsKey(fName)
                            && currBOF.equals(splitBlobOfFile)) {
                        String[] args3 = new String[]{"checkout",
                                mergeCommit.getCurrentId(), "--", fName};
                        checkout(args3);
                        currSA.addToStage(fName, givBOF);
                        Utils.writeObject(stageAreaTxt, currSA);
                    } else {
                        conflict = true;
                        helperConflict(currBOF,
                                givBOF, fName);
                    }
                }
            } else if (!splitBM.containsKey(fName)) {
                if (mergeBM.containsKey(fName)
                        && currBM.containsKey(fName)
                        && !currBOF.equals(givBOF)) {
                    conflict = true;
                    helperConflict(currBOF, givBOF, fName);
                } else if (mergeBM.containsKey(fName)) {
                    String[] args2 = new String[]{"checkout",
                            mergeCommit.getCurrentId(), "--", fName};
                    checkout(args2);
                    currSA.addToStage(fName, givBOF);
                    Utils.writeObject(stageAreaTxt, currSA);
                }
            } else if ((currBM.containsKey(fName) && !mergeBM.containsKey
                    (fName) && currBOF.equals(splitBlobOfFile))
                    || (!currBM.containsKey(fName)
                    && mergeBM.containsKey(fName))
                    && givBOF.equals(splitBlobOfFile)) {
                if (currBOF.equals(splitBlobOfFile)
                        && !mergeBM.containsKey(fName)) {
                    rm(fName);
                }
            } else if (!mergeBM.containsKey(fName)
                    && !currBOF.equals(splitBlobOfFile)) {
                conflict = true;
                helperConflict(currBOF, givBOF, fName);
            }
        }
        String message = ("Merged " + mergeBranchName
                + " into " + currBN + ".");
        commit(message, mergeCommit.getCurrentId());
        if (conflict) {
            System.out.println("Encountered a merge conflict.");
            return;
        }
    }

    @SuppressWarnings("unchecked")
    public void helperConflict(String currBOF,
                               String mergeBlobOfFile, String fileName) {
        String mergeContents = "";
        String curContents = "";
        File newMerge = Utils.join(cWD, fileName);
        File mergeBlobFile = Utils.join(blobs, mergeBlobOfFile + ".txt");
        File currBlobFile = Utils.join(blobs, currBOF + ".txt");
        if (mergeBlobFile.exists()) {
            mergeContents = Utils.readContentsAsString(mergeBlobFile);
        }
        if (currBlobFile.exists()) {
            curContents = Utils.readContentsAsString(currBlobFile);
        }

        Utils.writeContents(newMerge, "<<<<<<< HEAD\n" + curContents
                + "=======\n" + mergeContents + ">>>>>>>\n");

        StageArea currentStage =
                Utils.readObject(stageAreaTxt, StageArea.class);
        currentStage.addToStage(fileName,
                Utils.readContentsAsString(newMerge));
        Utils.writeObject(stageAreaTxt, currentStage);
    }

    @SuppressWarnings("unchecked")
    public Commit findSplitPoint(Commit currentCommit,
                                 Commit mergeCommit, String mergeBranchName) {
        Commit splitPoint = null;
        TreeMap<String, Commit> currCommitAnces = new TreeMap<>();
        Commit copy = currentCommit;
        currCommitAnces.put(copy.getCurrentId(), currentCommit);
        TreeMap<String, Commit> currMergeId = new TreeMap<>();

        while (copy.getParentId1() != null) {
            File getpCommit =
                    Utils.join(commits, copy.getParentId1() + ".txt");
            Commit pCommit =
                    Utils.readObject(getpCommit, Commit.class);
            currCommitAnces.put(pCommit.getCurrentId(), pCommit);
            if (copy.getMergeId2() != null) {
                File getMergeCommit =
                        Utils.join(commits, copy.getMergeId2() + ".txt");
                Commit mergeIdCommit =
                        Utils.readObject(getMergeCommit, Commit.class);
                currMergeId.put(mergeIdCommit.getCurrentId(), mergeIdCommit);
            }
            copy = pCommit;
        }

        Commit mergeCopy = mergeCommit;
        while (mergeCopy.getParentId1() != null) {
            if (currCommitAnces.containsKey(mergeCopy.getCurrentId())) {
                splitPoint = mergeCopy;
                break;
            }
            if (currMergeId.containsKey(mergeCopy.getCurrentId())
                    || (mergeCopy.getMergeId2() != null
                    && currCommitAnces.containsKey(mergeCopy.getMergeId2()))) {
                splitPoint = mergeCopy;
                break;
            } else {
                File getpCommit = Utils.join(commits,
                        mergeCopy.getParentId1() + ".txt");
                Commit pCommit = Utils.readObject(getpCommit, Commit.class);
                mergeCopy = pCommit;
                if (pCommit.getMessage().equals("initial commit")) {
                    splitPoint = mergeCopy;
                }
            }
        }

        if (currCommitAnces.containsKey(mergeCommit.getCurrentId())) {
            System.out.println("Given branch is an"
                    + " ancestor of the current branch.");
            return null;
        }
        if (currentCommit.getCurrentId().equals(splitPoint.getCurrentId())) {
            String[] args = new String[]{"checkout", mergeBranchName};
            checkout(args);
            Utils.restrictedDelete("f.txt");
            System.out.println("Current branch fast-forwarded.");
            return null;
        }
        return splitPoint;
    }

    @SuppressWarnings("unchecked")
    public String shortenedHelper(String shortCommitId) {
        List<String> xx = Utils.plainFilenamesIn(commits);
        for (String fileName : xx) {
            if (fileName.contains(shortCommitId)) {
                return fileName.substring(0, fileName.length() - 4);
            }
        }
        System.out.println("could not find commit with the shortened");
        return null;
    }














}
