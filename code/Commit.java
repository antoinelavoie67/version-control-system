package gitlet;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

public class Commit implements Serializable {
    /** */
    private String message2;
    /** */
    private String date2;
    /** */
    private String currentId12;
    /** */
    private String parentId12;
    /** */
    private String mergeId22;
    /** */
    private TreeMap blobs2;

    @SuppressWarnings("unchecked")
    public Commit(String message, String parentId1, String mergeId2,
                  TreeMap<String, String> blobs) {

        this.parentId12 = parentId1;
        this.mergeId22 = mergeId2;
        this.message2 = message;
        this.blobs2 = blobs;
        if (parentId1 == null) {
            date2 = "Thu Jan 1 00:00:00 1970 -0800";
        } else {
            SimpleDateFormat x =
                    new SimpleDateFormat("EEE MMM dd hh:mm:ss yyyy ZZZZZ");
            date2 = x.format(new Date());
        }
        this.currentId12 = findSha1();
    }
    @SuppressWarnings("unchecked")
    public String findSha1() {
        return Utils.sha1(Utils.serialize(this));
    }
    @SuppressWarnings("unchecked")
    public String getCurrentId() {
        return currentId12;
    }
    @SuppressWarnings("unchecked")
    public String getParentId1() {
        return parentId12;
    }
    @SuppressWarnings("unchecked")
    public String getMergeId2() {
        return mergeId22;
    }
    @SuppressWarnings("unchecked")
    public String getMessage() {
        return message2;
    }
    @SuppressWarnings("unchecked")
    public TreeMap getBlobs() {
        return blobs2;
    }
    @SuppressWarnings("unchecked")
    public String getDate() {
        return date2;
    }
    @SuppressWarnings("unchecked")
    public TreeMap returnBlob() {
        return blobs2;
    }

}
