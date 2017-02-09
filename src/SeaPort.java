import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

/**
 * file name: SeaPort.java
 *
 * @author Ryan Cohan
 *         date: 12/12/16
 *         purpose: This class holds docks, qued ships, ships and persons for simulation
 */
public class SeaPort extends Thing {
    ArrayList<Dock> docks = new ArrayList<Dock>();
    ArrayList<Ship> que = new ArrayList<Ship>(); // the list of ships waiting to dock
    ArrayList<Ship> ships = new ArrayList<Ship>(); // a list of all the ships at this port
    ArrayList<Person> persons = new ArrayList<Person>(); // people with skills at this port

    ArrayList<Job> requestList = new ArrayList<Job>();

    JPanel containerPanel = new JPanel();
    JLabel resourceLabel = new JLabel("", SwingConstants.LEFT);
    JLabel requestLabel = new JLabel("", SwingConstants.LEFT);
    JProgressBar resourceProgress;

    /**
     * Constructor for scanner
     *
     * @param sc
     */
    public SeaPort(Scanner sc) {
        super(sc);
    }

    /**
     * Mutator method to add a dock object to existing docks list
     *
     * @param dock
     */
    public void addDock(Dock dock) {
        docks.add(dock);
    }

    /**
     * Mutator method to add a ship object to existing ship list
     *
     * @param ship
     */
    public void addShip(Ship ship) {
        ships.add(ship);
    }

    /**
     * Mutator method to add a ship object to existing que list
     *
     * @param ship
     */
    public void addToQue(Ship ship) {
        que.add(ship);
    }

    /**
     * Mutator method to add a person object to existing people list
     *
     * @param person
     */
    public void addPerson(Person person) {
        persons.add(person);
    }

    /**
     * Sorts ships in que by weight
     */
    public void sortByWeight() {
        Collections.sort(que, new Comparator<Ship>() {
            @Override
            public int compare(Ship o1, Ship o2) {
                return (o1.weight < o2.weight ? -1 : (o1.weight == o2.weight ? 0 : 1));
            }
        });
    }

    /**
     * Sorts ships in que by length
     */
    public void sortByLength() {
        Collections.sort(que, new Comparator<Ship>() {
            @Override
            public int compare(Ship o1, Ship o2) {
                return (o1.length < o2.length ? -1 : (o1.length == o2.length ? 0 : 1));
            }
        });
    }

    /**
     * Sorts ships in que by width
     */
    public void sortByWidth() {
        Collections.sort(que, new Comparator<Ship>() {
            @Override
            public int compare(Ship o1, Ship o2) {
                return (o1.width < o2.width ? -1 : (o1.width == o2.width ? 0 : 1));
            }
        });
    }

    /**
     * Sorts ships in que by draft
     */
    public void sortByDraft() {
        Collections.sort(que, new Comparator<Ship>() {
            @Override
            public int compare(Ship o1, Ship o2) {
                return (o1.draft < o2.draft ? -1 : (o1.draft == o2.draft ? 0 : 1));
            }
        });
    }

    /**
     * Sorts all lists by name using super's compareTo method
     */
    public void sortAllListsByName() {
        Collections.sort(ships);
        Collections.sort(docks);
        Collections.sort(persons);
        Collections.sort(que);
        for (Ship ship : ships) {
            Collections.sort(ship.jobs);
        }
        for (Ship ship : que) {
            Collections.sort(ship.jobs);
        }
    }

    /**
     * Empty docks call this method to get a ship
     *
     * @return a ship from que
     */
    public Ship getShipFromQue() {
        synchronized (this) {
            if (que.size() > 0) {
                Ship ship = que.get(que.size() - 1);
                que.remove(ship);
                return ship;
            }
        }
        return null;
    }

    /**
     * Convert class fields to string
     * And run toString methods for it's lists
     *
     * @return String
     */
    public String toString() {
        String st = "\n\nSeaPort: " + super.toString();
        for (Dock md : docks) st += "\n" + md.toString();
        st += "\n\n --- List of all ships in que:";
        for (Ship ms : que) st += "\n   > " + ms.toString();
        st += "\n\n --- List of all ships:";
        for (Ship ms : ships) st += "\n   > " + ms.toString();
        st += "\n\n --- List of all persons:";
        for (Person mp : persons) st += "\n   > " + mp.toString();
        return st;
    }

    /**
     * Checks for empty docks
     */
    public synchronized void checkDocks() {
        for (Dock dock : docks) {
            dock.checkDock();
        }
    }

    /**
     * Checks for skilled personnel with respect to requirements
     *
     * @param requirements: required skills
     * @return true if there are enough numbers of personnel
     */
    public boolean askForPersonnel(ArrayList<String> requirements) {
        ArrayList<Person> personnelChecker = new ArrayList<Person>();
        for (String requirement : requirements) {
            for (Person person : persons) {

                if (person.hasSkill(requirement) && !personnelChecker.contains(person))
                    personnelChecker.add(person);

                if (personnelChecker.size() == requirements.size()) return true;
            }
        }
        return false;
    }

    /**
     * Returns list of people with required skills
     *
     * @param requirements: required skills
     * @return list of people
     */
    public ArrayList<Person> requestWorkers(ArrayList<String> requirements, Job job) {
        ArrayList<Person> requiredWorkers = new ArrayList<Person>();
        for (String requirement : requirements) {
            for (Person person : persons) {
                if (person.hasSkill(requirement))
                    synchronized (this) {
                        requiredWorkers.add(person.hire());
                    }
                if (requiredWorkers.size() == requirements.size()) {
                    broadcastUpdateOnResourcePool();
                    if (requestList.contains(job)) requestList.remove(job);
                    return requiredWorkers;
                }
            }
        }
        if (!requestList.contains(job)) {
            requestList.add(job);
        }
        if (requiredWorkers.size() > 0)
            releaseWorkers(requiredWorkers);
        return null;
    }

    /**
     * Releases workers that completed the job
     *
     * @param workers
     */
    public void releaseWorkers(ArrayList<Person> workers) {
        for (Person worker : workers) {
            if (worker != null)
                synchronized (this) {
                    worker.release();
                }
        }
        broadcastUpdateOnResourcePool();
    }


    /**
     * Updates information on resources and requests on hold
     */
    private void broadcastUpdateOnResourcePool() {
        int availableResources = getAvailableResources();
        resourceLabel.setText("Resources: " + availableResources);
        requestLabel.setText("Requests: " + requestList.size());
        if (resourceProgress != null) {
            resourceProgress.setValue(availableResources);
            if (availableResources < Math.round(persons.size() * 0.2)) {
                resourceProgress.setForeground(Color.red);
            } else if (availableResources < Math.round(persons.size() * 0.6)) {
                resourceProgress.setForeground(Color.yellow);
            } else {
                resourceProgress.setForeground(Color.blue);
            }
        }
    }


    /**
     * Get number of available resources
     *
     * @return number of available person
     */
    private int getAvailableResources() {
        int numOfAvailablePerson = 0;
        for (Person person : persons) {
            if (person.isAvailable)
                numOfAvailablePerson++;
        }
        return numOfAvailablePerson;
    }


    /**
     * Sets container panel and layout
     */
    private void setVisualElements() {

        containerPanel = new JPanel();
        requestLabel.setMaximumSize(new Dimension(100, 20));
        requestLabel.setMaximumSize(new Dimension(100, 20));
        resourceLabel.setMinimumSize(new Dimension(100, 20));
        resourceLabel.setMinimumSize(new Dimension(100, 20));
        GroupLayout groupLayout = new GroupLayout(containerPanel);
        containerPanel.setLayout(groupLayout);
        setLayout(groupLayout);

    }

    /**
     * Sets layout for container panel
     *
     * @param groupLayout Layout for components
     */
    private void setLayout(GroupLayout groupLayout) {
        groupLayout.setAutoCreateGaps(true);
        groupLayout.setAutoCreateContainerGaps(true);

        JLabel portLabel = new JLabel("Port: " + getName(), SwingConstants.LEFT);
        portLabel.setMaximumSize(new Dimension(120, 20));
        portLabel.setMinimumSize(new Dimension(120, 20));
        resourceProgress = new JProgressBar(0, persons.size());

        broadcastUpdateOnResourcePool();

        groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
                        .addComponent(portLabel)
                        .addComponent(resourceProgress)
                        .addComponent(resourceLabel)
                        .addComponent(requestLabel)
        );

        groupLayout.setVerticalGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(portLabel)
                        .addComponent(resourceProgress)
                        .addComponent(resourceLabel)
                        .addComponent(requestLabel)
        );
    }

    /**
     * Returns container panel to display resources
     *
     * @return containerPanel
     */
    public Component getContainerPanel() {
        setVisualElements();
        return containerPanel;
    }
}
