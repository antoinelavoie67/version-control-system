package gitlet;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Antoine Lavoie
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        SomeObj test = new SomeObj();
        if (args.length == 0) {
            System.out.println("Please enter a command.");
        } else if (args[0].equals("init")) {
            test.init();
        } else if (args[0].equals("add")) {
            test.add(args[1]);
        } else if (args[0].equals("commit")) {
            test.commit(args[1], null);
        } else if (args[0].equals("checkout")) {
            if (args.length == 2) {
                test.checkoutBranch(args);
            } else {
                test.checkout(args);
            }
        } else if (args[0].equals("log")) {
            test.log();
        } else if (args[0].equals("global-log")) {
            test.globalLog();
        } else if (args[0].equals("rm")) {
            test.rm(args[1]);
        } else if (args[0].equals("find")) {
            test.find(args[1]);
        } else if (args[0].equals("status")) {
            test.status();
        } else if (args[0].equals("branch")) {
            test.branch(args[1]);
        } else if (args[0].equals("rm-branch")) {
            test.rmBranch(args[1]);
        } else if (args[0].equals(("reset"))) {
            test.reset(args[1]);
        } else if (args[0].equals("merge")) {
            test.mainMerge(args);
        } else if (args[0] != null) {
            System.out.println("No command with that name exists.");
        } else {
            System.out.println("Unkown Command");
        }
    }
}
