// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2021T2, Assignment 3
 * Name: Sajja Syed
 * Username: syedsajj
 * ID: 300551462
 */

import ecs100.*;
import java.util.*;

/**
 * Simple Simulation of a Hospital ER
 * 
 * The Emergency room has a waiting room and a treatment room that has a fixed
 *  set of beds for examining and treating patients.
 * 
 * When a patient arrives at the emergency room, they are immediately assessed by the
 *  triage team who determines the priority of the patient.
 *
 * They then wait in the waiting room until a bed becomes free, at which point
 * they go from the waiting room to the treatment room.
 *
 * When a patient has finished their treatment, they leave the treatment room and are discharged,
 *  at which point information about the patient is added to the statistics. 
 *
 *  READ THE ASSIGNMENT PAGE!
 */

public class HospitalERCore{

    // Fields for recording the patients waiting in the waiting room and being treated in the treatment room
    private Queue<Patient> waitingRoom = new ArrayDeque<Patient>(); // ME public static, B4  just private
    private static final int MAX_PATIENTS = 5;   // max number of patients currently being treated
    private Set<Patient> treatmentRoom = new HashSet<Patient>(); // ME public static, B4  just private

    // fields for the statistics
    /*# YOUR CODE HERE */
    private int totalPatients = 0;
    private int totalPatientsPri1 = 0;
    private int totalWaitingTime = 0; // Total time patients have waited
    private int totalWaitingTimep1 = 0; // Total time priority 1 patients have waited

    // Fields for the simulation
    private boolean running = false;
    private int time = 0; // The simulated time - the current "tick"
    private int delay = 300;  // milliseconds of real time for each tick

    // fields controlling the probabilities.
    private int arrivalInterval = 5;   // new patient every 5 ticks, on average
    private double probPri1 = 0.1; // 10% priority 1 patients
    private double probPri2 = 0.2; // 20% priority 2 patients
    private Random random = new Random();  // The random number generator.


    public HospitalERCore() {

    }

    /**
     * Construct a new HospitalERCore object, setting up the GUI, and resetting
     */
    public static void main(String[] arguments){
        HospitalERCore er = new HospitalERCore();

        er.setupGUI();
        er.reset(false);   // initialise with an ordinary queue.
    }

    /**
     * Set up the GUI: buttons to control simulation and sliders for setting parameters
     */
    public void setupGUI(){
        UI.addButton("Reset (Queue)", () -> {this.reset(false); });
        UI.addButton("Reset (Pri Queue)", () -> {this.reset(true);});
        UI.addButton("Start", ()->{if (!running){ run(); }});   //don't start if already running!
        UI.addButton("Pause & Report", ()->{running=false;});
        UI.addSlider("Speed", 1, 400, (401-delay),
            (double val)-> {delay = (int)(401-val);});
        UI.addSlider("Av arrival interval", 1, 50, arrivalInterval,
            (double val)-> {arrivalInterval = (int)val;});
        UI.addSlider("Prob of Pri 1", 1, 100, probPri1*100,
            (double val)-> {probPri1 = val/100;});
        UI.addSlider("Prob of Pri 2", 1, 100, probPri2*100,
            (double val)-> {probPri2 = Math.min(val/100,1-probPri1);});
        UI.addButton("Quit", UI::quit);
        UI.setWindowSize(1000,600);
        UI.setDivider(0.5);
    }

    /**
     * Reset the simulation:
     *  stop any running simulation,
     *  reset the waiting and treatment rooms
     *  reset the statistics.
     */
    public void reset(boolean usePriorityQueue){
        running=false;
        UI.sleep(2*delay);  // to make sure that any running simulation has stopped
        time = 0;           // set the "tick" to zero.

        // reset the waiting room, the treatment room, and the statistics.
        /*# YOUR CODE HERE */
        waitingRoom.clear();
        treatmentRoom.clear();
        totalPatients = 0;
        totalPatientsPri1 = 0;
        time  = 0;

        if(usePriorityQueue){waitingRoom = new PriorityQueue<Patient>();}
        else{waitingRoom = new ArrayDeque<Patient>();}

        UI.clearGraphics();
        UI.clearText();
    }

    /**
     * Main loop of the simulation
     */
    public void run(){
        if (running) { return; } // don't start simulation if already running one!
        running = true;
        while (running){         // each time step, check whether the simulation should pause.

            // Hint: if you are stepping through a set, you can't remove
            //   items from the set inside the loop!
            //   If you need to remove items, you can add the items to a
            //   temporary list, and after the loop is done, remove all
            //   the items on the temporary list from the set.

            /*# YOUR CODE HERE */
            time++;

            //Checks if there are patients in the waiting room
            // If so poll until the treatment room is full
            while(treatmentRoom.size()< MAX_PATIENTS && !waitingRoom.isEmpty()){
                treatmentRoom.add(waitingRoom.poll());
            }

            // Track the treated patients
            List<Patient> treatedPatients = new ArrayList<>();

            // For each patient in treatmentRoom
            // If the treatment is done remove them, otherwise advance each patients treatment by one tick
            // Increment total patients
            for(Patient patient : treatmentRoom){
                if(patient.completedCurrentTreatment()){
                    treatedPatients.add(patient);
                    totalPatients++;
                    if(patient.getPriority()==1){
                        totalPatientsPri1++;
                    }
                    UI.println(time+ ": Discharge: " + patient);
                } else {
                    patient.advanceTreatmentByTick();

                }
            }

            // Increments the total waiting time
            for(Patient p: treatedPatients){
                if(p.getPriority()==1){
                    totalWaitingTimep1++;
                }
                totalWaitingTime++;
            }

            treatmentRoom.removeAll(treatedPatients);

            // Advance time for every patient in the waiting room
            for(Patient patient: waitingRoom){patient.waitForATick();}

            // Get any new patient that has arrived and add them to the waiting room
            if (time==1 || Math.random()<1.0/arrivalInterval){
                Patient newPatient = new Patient(time, randomPriority());
                UI.println(time+ ": Arrived: "+newPatient);
                waitingRoom.offer(newPatient);
            }
            redraw();
            UI.sleep(delay);
        }
        // paused, so report current statistics
        reportStatistics();
    }

    // Additional methods used by run() (You can define more of your own)

    /**
     * Report summary statistics about all the patients that have been discharged.
     * (Doesn't include information about the patients currently waiting or being treated)
     * The run method should have been recording various statistics during the simulation.
     */
    public void reportStatistics(){
        /*# YOUR CODE HERE */
        if(totalPatients == 0){
            UI.println("No patients with have been processed");
        }
        else{
            UI.printf("Processed %d patients with average waiting time of %d minutes\n", totalPatients,
                    (totalWaitingTime/ totalPatients));
        }

        if(totalPatientsPri1 == 0){
            UI.println("No patients with priotiry 1 has been processed");
        }
        else {
            UI.printf("Processed %d priority 1 patients with average waiting time of %d minutes\n", totalPatientsPri1,
                    (totalWaitingTimep1 / totalPatientsPri1));
        }
    }


    // HELPER METHODS FOR THE SIMULATION AND VISUALISATION
    /**
     * Redraws all the departments
     */
    public void redraw(){
        UI.clearGraphics();
        UI.setFontSize(14);
        UI.drawString("Treating Patients", 5, 15);
        UI.drawString("Waiting Queues", 200, 15);
        UI.drawLine(0,32,400, 32);

        // Draw the treatment room and the waiting room:
        double y = 80;
        UI.setFontSize(14);
        UI.drawString("ER", 0, y-35);
        double x = 10;
        UI.drawRect(x-5, y-30, MAX_PATIENTS*10, 30);  // box to show max number of patients
        for(Patient p : treatmentRoom){
            p.redraw(x, y);
            x += 10;
        }
        x = 200;
        for(Patient p : waitingRoom){
            p.redraw(x, y);
            x += 10;
        }
        UI.drawLine(0,y+2,400, y+2);
    }

    /**
     * Returns a random priority 1 - 3
     * Probability of a priority 1 patient should be probPri1
     * Probability of a priority 2 patient should be probPri2
     * Probability of a priority 3 patient should be (1-probPri1-probPri2)
     */
    private int randomPriority(){
        double rnd = random.nextDouble();
        if (rnd < probPri1) {return 1;}
        if (rnd < (probPri1 + probPri2) ) {return 2;}
        return 3;
    }
}
