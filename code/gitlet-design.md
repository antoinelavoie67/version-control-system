# Gitlet Design Document
author: Antoine Lavoie

## Design Document Guidelines

Please use the following format for your Gitlet design document. Your design
document should be written in markdown, a language that allows you to nicely 
format and style a text file. Organize your design document in a way that 
will make it easy for you or a course-staff member to read.  

## 1. Classes and Data Structures

Include here any class definitions. For each class list the instance
variables and static variables (if any). Include a ***brief description***
of each variable and its purpose in the class. Your explanations in
this section should be as concise as possible. Leave the full
explanation to the following sections. You may cut this section short
if you find your document is too wordy.

### Main Class --
* This is where I will figure out what the user is telling my gitlet to do depending 
on the arguments passed in.

### Commit Class --
* This is where I will represent my commit
#### Instance Variables:
* Message - contains the message of a commit
* Date - time at which a commit was created. Assigned by the constructor
* ParentID - the sha1 ID of the parent commit
* Blob - the treeMap containing blobs associated with the state we are trying to commit
* CurrentID - the sha1 ID of the current commit

###Repo Class --
* This class will deal with all of the potential arguments 
that are passed in and delegated by the main (i.e. log, commit, add, etc)
####Instance Variables:
* String Head: the sha1 id of the current head commit
* String Master: the sha1 id of the current master branch
* Staging Area: the current staging area
* File (Current Working Directory): the current directory that we are working in 

###Staging Area Class --
* This helps delegate some work and allows us to call methods specifically on the current staging area
####Instance Variables:
* TreeMap filesAdded: Keeps track of which files we are adding for our commit
* TreeMap filesRemoved: Keeps track of which files we have removed and want to commit


## 2. Algorithms

This is where you tell us how your code works. For each class, include
a high-level description of the methods in that class. That is, do not
include a line-by-line breakdown of your code, but something you would
write in a javadoc comment above a method, ***including any edge cases
you are accounting for***. We have read the project spec too, so make
sure you do not repeat or rephrase what is stated there.  This should
be a description of how your code accomplishes what is stated in the
spec.


The length of this section depends on the complexity of the task and
the complexity of your design. However, simple explanations are
preferred. Here are some formatting tips:

* For complex tasks, like determining merge conflicts, we recommend
  that you split the task into parts. Describe your algorithm for each
  part in a separate section. Start with the simplest component and
  build up your design, one piece at a time. For example, your
  algorithms section for Merge Conflicts could have sections for:

   * Checking if a merge is necessary.
   * Determining which files (if any) have a conflict.
   * Representing the conflict in the file.
  
* Try to clearly mark titles or names of classes with white space or
  some other symbols.


### Main Class --
####Methods:
* Main(): This will be the only method that we have and it will essentially sort through the arguments that are passed in
by the users. It will first make the new repo object. 
Then there will be many if statements that if met they will call a method inside of the Repo class.

### Commit Class --
####Methods:
* getMessage(): This will get the message by returning the instance variable Message.
* getID(): This will get the sha1 ID by calling onto sha1 and
  passing in the string that is turned into a byte.
* getCurrentID(): This will return the instance variable of the currentID
* getParentID(): This will return the instance variable of parentID.
* getBlobTree(): This will return the instance variable of blobs.

###Repo Class --
####Methods:
* Init(): 
  * First we will create a .gitlet file and then make directories for the stage area, commits, branchses, and log (might not need this one).
  For staging area we will make a file, then make a staging area object, then save that object to the file.
  For commit directory, we will make new file, make a new initial commit and then save this commit to the file.
  For the branch directory we need a file for Head and also the Master. 
* Add(): 
  * First we will get the file from the directory by name and if it doesn't exist we will print a message. If it is identical to a file in the add directory we will not add it. 
  If a file with the same name in the staging area we will remove it and then continue on. We will make a new file, write the contents of blobs into the new file. Ad the file to the staging area. Store the staging area in a file in the staging area directory.
* Commit(): 
  * If there are no files staged throw a message. If the message arg is empty (blank) throw a message. Else we will get the current commit, make a copy of it. Then we will add to the treeMap the files that are staged for addition and put them in the hash map.
  We will also go through the files staged for removal and remove them from the TreeMap After this we will add the commit to a file and change the head pointer to this commit.
* Log(): 
  * For log, we will start at the head (current commit) and print whatever is necessary for that commit. Then we will go back one commit by using the parents hash and do the same until there are no more commits left. 
* GlobalLog()
  * Call on utils method. Iterae through the list that the method creates. For each element in the list simply print same things as in log.
* Checkout(): 
  * Case1: In this case where only the file name is provided we will find the current head commit and the tree map associated with it. If the file,name exist we want to write the contents of it in a new file and then delete it. 
  Case3: In this case with the [commitID] and the [file name] we will do exactly the same as the first case.
* RM():
  * First check if the file is in filesAdded tree of Stage Object. If it is then remove. If not do nothing.
  * Then check if file is in current commit. If it is then check if file is in filesRemoved. If not then add it.
  * Remove the file from working directory and save eveything to the respective files (commit and stage Area)
* find()
  * Get a list of all of the ids in the commit class. Iterate through each commit using these id's to find the commit and see if the messages match
  * If they do print their ID on a line, if not print the error message.
###Staging Area Class --
####Methods:
* getAddTree(): return the treeMap for add
* getRemoveTree(): return the TreeMap for remove
* addToStage(): will take in a file name and its ID so that it can add to AddTree
* removeToStage(): will take in a file name and its ID so it can be added to RemoveTree
* clear(): Empty both the AddTree and the RemoveTree

## 3. Persistence

Describe your strategy for ensuring that you don’t lose the state of your program
across multiple runs. Here are some tips for writing this section:

* This section should be structured as a list of all the times you
  will need to record the state of the program or files. For each
  case, you must prove that your design ensures correct behavior. For
  example, explain how you intend to make sure that after we call
       `java gitlet.Main add wug.txt`,
  on the next execution of
       `java gitlet.Main commit -m “modify wug.txt”`, 
  the correct commit will be made.
  
* A good strategy for reasoning about persistence is to identify which
  pieces of data are needed across multiple calls to Gitlet. Then,
  prove that the data remains consistent for all future calls.
  
* This section should also include a description of your .gitlet
  directory and any files or subdirectories you intend on including
  there.

###Description of .gitlet dir:
* The .gitlet dir will have these directories: repo, staging area, commits, and branches (for post-checkpoint)
* repo: This will be the current working directory
* staging area: We can add the staging area object to files to the staging area directory as an intermediary step before committing. Getting access to this will allow us to see what files need to be added or removed. 
* commits: we can save commit objects to the commit directory to keep track of all the commits that have been made so that we may access them when trying to do operations like log. 
* branches: the branch directory will serve as a holder for the pointers to the Head and the Master which we will update as we go along. 
###General Idea:
* After a commit is made the staging area directory should end up empty will the commit directory will hold files of the most recent commit and all past commits. 
* To get from add to commit we will add to the blobs directore, and then the staging directory in the form of files and the sha1 ID of these files.
Then once we git commit we will pull from the staging area and add a file to the commit that contains the old and the new. After doing such we may erase the blobs and the staging area.

## 4. Design Diagram

Attach a picture of your design diagram illustrating the structure of your
classes and data structures. The design diagram should make it easy to 
visualize the structure and workflow of your program.

