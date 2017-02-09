import java.util.HashMap;
import java.util.Scanner;

/**
 * file name: Thing.java
 *
 * @author Ryan Cohan
 *         date: 12/12/16
 *         purpose: This class is the super of all classes related with the project
 */
public class Thing implements Comparable<Thing> {
    private String name;
    private int index;
    private int parent;
    Thing parentThing;

    public Thing(){}

    public Thing(Scanner sc) {
        this.name = sc.next();
        this.index = sc.nextInt();
        this.parent = sc.nextInt();
    }

    public void setParentThing(HashMap parentHashMap){
        if(parentHashMap.get(parent) != null)
            parentThing = (Thing) parentHashMap.get(parent);
    }


    @Override
    public int compareTo(Thing o) {
        return name.compareTo(o.getName());
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public int getParent() {
        return parent;
    }

    synchronized public String toString() {
        return name + " " + index;
    }

}
