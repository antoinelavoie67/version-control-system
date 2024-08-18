package gitlet;

import java.io.Serializable;
import java.util.TreeMap;

public class StageArea implements Serializable {
    /** */
    private TreeMap<String, String> filesAdded;
    /** */
    private TreeMap<String, String> filesRemoved;

    public StageArea() {
        filesAdded = new TreeMap<>();
        filesRemoved = new TreeMap<>();
    }
    public TreeMap<String, String> getAddedTree() {
        return filesAdded;
    }
    public TreeMap<String, String> getRemovedTree() {
        return filesRemoved;
    }
    public void addToStage(String name, String id) {
        filesAdded.put(name, id);
    }
}
