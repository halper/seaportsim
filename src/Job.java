import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * file name: Job.java
 *
 * @author Ryan Cohan
 *         date: 12/12/16
 *         purpose: This class simulates a running job
 */
public class Job extends Thing implements Runnable {
    private ArrayList<String> requirements = new ArrayList<String>();
    JPanel containerPanel;

    long duration;
    JProgressBar pm = new JProgressBar();
    boolean goFlag = true, noKillFlag = true;
    JButton jbGo = new JButton("Stop");
    JButton jbKill = new JButton("Cancel");
    Status status = Status.WAITING;

    Job toBeRemoved;

    Ship parentShip;


    enum Status {RUNNING, SUSPENDED, WAITING, DONE}

    public Job(Scanner sc) {
        super(sc);
        duration = (long) sc.nextDouble();
        while (sc.hasNext()) {
            String requirement = sc.next();
            if (requirement != null && requirement.length() > 0)
                requirements.add(requirement);
        }
    }

    /**
     * Sets parentThing as Ship object
     *
     * @param parentThing
     */
    public void setParentThing(HashMap parentThing) {

        if (parentThing.get(getParent()) != null) {
            this.parentThing = (Thing) parentThing.get(getParent());
            parentShip = (Ship) this.parentThing;
            setVisualElements();
            toBeRemoved = this;
            new Thread(this).start();
        }
    }

    /**
     * Sets container panel and layout
     */
    private void setVisualElements() {

        containerPanel = new JPanel();
        pm = new JProgressBar();
        pm.setStringPainted(true);

        jbGo.setMinimumSize(new Dimension(120, 25));
        jbGo.setMaximumSize(new Dimension(120, 25));

        GroupLayout groupLayout = new GroupLayout(containerPanel);
        containerPanel.setLayout(groupLayout);
        setLayout(groupLayout);

        setActionListeners();
    }

    /**
     * Sets layout for container panel
     *
     * @param groupLayout Layout for components
     */
    private void setLayout(GroupLayout groupLayout) {
        groupLayout.setAutoCreateGaps(true);
        groupLayout.setAutoCreateContainerGaps(true);

        JLabel jLabel = new JLabel(parentShip.getName(), SwingConstants.CENTER);
        jLabel.setMinimumSize(new Dimension(150, 25));
        jLabel.setMaximumSize(new Dimension(150, 25));

        groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
                .addComponent(pm)
                .addComponent(jLabel)
                .addComponent(jbGo)
                .addComponent(jbKill));

        groupLayout.setVerticalGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(pm)
                .addComponent(jLabel)
                .addComponent(jbGo)
                .addComponent(jbKill));
    }

    /**
     * Mutator method to get jpanel
     *
     * @return container panel
     */
    public JPanel getContainerPanel() {
        return containerPanel;
    }

    /**
     * Sets action listeners for components
     */
    private void setActionListeners() {
        jbGo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleGoFlag();
            }
        });

        jbKill.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setKillFlag();
            }
        });
    }

    public void toggleGoFlag() {
        goFlag = !goFlag;
    }

    public void setKillFlag() {
        noKillFlag = false;
        jbKill.setBackground(Color.red);
    }

    void showStatus(Status st) {
        status = st;
        switch (status) {
            case RUNNING:
                jbGo.setBackground(Color.green);
                jbGo.setText("Running");
                break;
            case SUSPENDED:
                jbGo.setBackground(Color.yellow);
                jbGo.setText("Suspended");
                break;
            case WAITING:
                jbGo.setBackground(Color.orange);
                jbGo.setText("Waiting turn");
                break;
            case DONE:
                jbGo.setBackground(Color.red);
                jbGo.setText("Done");
                break;
        }
    }

    public void run() {
        while (!World.getInit()) // wait for the world to be initialized, otherwise simulation does not work properly
            waitFor(300);
        while (!parentShip.isDocked()) {
            waitFor(100);
        }
        while (!parentShip.doJob()) {
            waitFor(100);
        }
        ArrayList<Person> workers = null;
        if (requirements.size() == 0 || parentShip.askForPersonnel(requirements)) {
            if (requirements.size() != 0) {
                do {
                    waitFor(100);
                    workers = parentShip.requestWorkers(requirements, toBeRemoved);
                } while (workers == null || workers.size() != requirements.size());
            }
            long time = System.currentTimeMillis();
            long startTime = time;
            long stopTime = time + 1000 * duration;
            double duration = stopTime - time;

            while (time < stopTime && noKillFlag) {
                waitFor(100);
                if (goFlag) {
                    showStatus(Status.RUNNING);
                    time += 100;
                    pm.setValue((int) (((time - startTime) / duration) * 100));
                } else {
                    showStatus(Status.SUSPENDED);
                }
            }

            pm.setValue(100);
            showStatus(Status.DONE);
        } else {
            showStatus(Status.SUSPENDED);
        }
        if (workers != null && workers.size() > 0)
            parentShip.releaseWorkers(workers);

        parentShip.removeJob(toBeRemoved);
    }


    /**
     * Waits for given long
     * @param l: long
     */
    private void waitFor(long l) {
        try {
            Thread.sleep(l);
        } catch (InterruptedException e) {
        }
    }


    public String toString() {
        return String.format("j:%7d:%15s:%5d", getIndex(), getName(), duration);
    }
}
