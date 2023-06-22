import ecs100.UI;
import ecs100.UIFileChooser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DeShredder allows a user to sort fragments of a shredded document ("shreds") into strips, and
 * then sort the strips into the original document.
 * The program shows
 * - a list of all the shreds along the top of the window,
 * - the working strip (which the user is constructing) just below it.
 * - the list of completed strips below the working strip.
 * The "rotate" button moves the first shred on the list to the end of the list to let the
 * user see the shreds that have disappeared over the edge of the window.
 * The "shuffle" button reorders the shreds in the list randomly
 * The user can use the mouse to drag shreds between the list at the top and the working strip,
 * and move shreds around in the working strip to get them in order.
 * When the user has the working strip complete, they can move
 * the working strip down into the list of completed strips, and reorder the completed strips
 */
public class DeShredder {

    // Constants for the display and the mouse
    public static final double LEFT = 20;       // left side of the display
    public static final double TOP_ALL = 20;    // top of list of all shreds
    public static final double GAP = 5;         // gap between strips
    public static final double SIZE = Shred.SIZE; // size of the shreds
    public static final double TOP_WORKING = TOP_ALL + SIZE + GAP;
    public static final double TOP_STRIPS = TOP_WORKING + (SIZE + GAP);
    // Fields to store the lists of Shreds and strips.  These should never be null.
    private final List<Shred> allShreds = new ArrayList<Shred>();    //  List of all shreds
    private final List<Shred> workingStrip = new ArrayList<Shred>(); // Current strip of shreds
    private final List<List<Shred>> completedStrips = new ArrayList<List<Shred>>();
    //Fields for recording where the mouse was pressed  (which list/strip and position in list)
    // note, the position may be past the end of the list!
    private List<Shred> fromStrip;   // The strip (List of Shreds) that the user pressed on
    private int fromPosition = -1;   // index of shred in the strip

    public static void main(String[] args) {
        DeShredder ds = new DeShredder();
        ds.setupGUI();

    }

    /**
     * Initialises the UI window, and sets up the buttons.
     */
    public void setupGUI() {
        UI.addButton("Load library", this::loadLibrary);
        UI.addButton("Rotate", this::rotateList);
        UI.addButton("Shuffle", this::shuffleList);
        UI.addButton("Complete Strip", this::completeStrip);
        UI.addButton("Quit", UI::quit);

        UI.setMouseListener(this::doMouse);
        UI.setWindowSize(1000, 800);
        UI.setDivider(0);
    }

    /**
     * Asks user for a library of shreds, loads it, and redisplays.
     * Uses UIFileChooser to let user select library
     * and finds out how many images are in the library
     * Calls load(...) to construct the List of all the Shreds
     */
    public void loadLibrary() {
        Path filePath = Path.of(UIFileChooser.open("Choose first shred in directory"));
        Path directory = filePath.getParent(); //subPath(0, filePath.getNameCount()-1);
        int count = 1;
        while (Files.exists(directory.resolve(count + ".png"))) {
            count++;
        }
        //loop stops when count.png doesn't exist
        count = count - 1;
        load(directory, count);   // YOU HAVE TO COMPLETE THE load METHOD
        display();
    }

    /**
     * Empties out all the current lists (the list of all shreds,
     * the working strip, and the completed strips).
     * Loads the library of shreds into the allShreds list.
     * Parameters are the directory containing the shred images and the number of shreds.
     * Each new Shred needs the directory and the number/id of the shred.
     */
    public void load(Path dir, int count) {
        /*# YOUR CODE HERE */
        allShreds.clear();
        workingStrip.clear();
        completedStrips.clear();

        for (int i = 1; i <= count; i++) {
            if (Files.exists(dir.resolve(i + ".png"))) {
                allShreds.add(new Shred(dir, i));
            }
        }
    }

    /**
     * Rotate the list of all shreds by one step to the left
     * and redisplay;
     * Should not have an error if the list is empty
     * (Called by the "Rotate" button)
     */
    public void rotateList() {
        /*# YOUR CODE HERE */
        if(allShreds.size() > 0){
            Collections.rotate(allShreds, -1);
            this.display();
        }
    }

    /**
     * Shuffle the list of all shreds into a random order
     * and redisplay;
     */
    public void shuffleList() {
        /*# YOUR CODE HERE */
        if(allShreds.size() > 0){
            Collections.shuffle(allShreds);
            this.display();
        }
    }

    /**
     * Move the current working strip to the end of the list of completed strips.
     * (Called by the "Complete Strip" button)
     */
    public void completeStrip() {
        /*# YOUR CODE HERE */
        List<Shred> workingStripCopy = new ArrayList<Shred>(workingStrip);
        completedStrips.add(workingStripCopy);
        workingStrip.clear();
        this.display();
    }

    // Additional methods to perform the different actions, called by doMouse

    /*# YOUR CODE HERE */
    //

    /**
     * This will move the whole strip if toStrip is empty
     */
    private void moveStrip(List<Shred> toStrip) {
        if (toStrip.isEmpty()) {
            toStrip.addAll(fromStrip);
            completedStrips.remove(fromStrip);
        }
    }

    /**
     * This will move the shred to the desired position
     */
    private void moveShred(List<Shred> toStrip, int toPosition) {
        if (toPosition >= toStrip.size()) {
            toStrip.add(fromStrip.remove(fromPosition));
        } else if (toPosition < 0) {
            toStrip.add(0, fromStrip.remove(fromPosition));
        } else {
            toStrip.add(toPosition, fromStrip.remove(fromPosition));
        }
    }

    //=============================================================================
    // Completed for you. Do not change.
    // loadImage and saveImage may be useful for the challenge.

    /**
     * Simple Mouse actions to move shreds and strips
     * User can
     * - move a Shred from allShreds to a position in the working strip
     * - move a Shred from the working strip back into allShreds
     * - move a Shred around within the working strip.
     * - move a completed Strip around within the list of completed strips
     * - move a completed Strip back to become the working strip
     * (but only if the working strip is currently empty)
     * Moving a shred to a position past the end of a List should put it at the end.
     * You should create additional methods to do the different actions - do not attempt
     * to put all the code inside the doMouse method - you will lose style points for this.
     * Attempting an invalid action should have no effect.
     * Note: doMouse uses getStrip and getColumn, which are written for you (at the end).
     * You should not change them.
     */
    public void doMouse(String action, double x, double y) {
        if (action.equals("pressed")) {
            fromStrip = getStrip(y);      // the List of shreds to move from (possibly null)
            fromPosition = getColumn(x);  // the index of the shred to move (may be off the end)
        }
        if (action.equals("released")) {
            List<Shred> toStrip = getStrip(y); // the List of shreds to move to (possibly null)
            int toPosition = getColumn(x);     // the index to move the shred to (may be off the end)
            // perform the correct action, depending on the from/to strips/positions
            /*# YOUR CODE HERE */

            // This will check if the user tries to access a non-existent strip
            if (fromStrip == null || toStrip == null) {
                return;
            }

            // This will check if the user tries to select (or move) a shred from beyond the ends of the list
            if (fromPosition < 0 || fromPosition >= fromStrip.size()) {
                return;
            }
            /*
             This will check if the user is accessing completedStrips or not. If he is and the toStrip is empty
             then the strip will move otherwise only the shred will move
            */
            if (fromStrip != allShreds && fromStrip != workingStrip
                    || toStrip != allShreds && toStrip != workingStrip) {
                moveStrip(toStrip);
            } else {
                moveShred(toStrip, toPosition);
            }

            display();
        }
    }

    /**
     * Displays the remaining Shreds, the working strip, and all completed strips
     */
    public void display() {
        UI.clearGraphics();

        // list of all the remaining shreds that haven't been added to a strip
        double x = LEFT;
        for (Shred shred : allShreds) {
            shred.drawWithBorder(x, TOP_ALL);
            x += SIZE;
        }

        //working strip (the one the user is workingly working on)
        x = LEFT;
        for (Shred shred : workingStrip) {
            shred.draw(x, TOP_WORKING);
            x += SIZE;
        }
        UI.setColor(Color.red);
        UI.drawRect(LEFT - 1, TOP_WORKING - 1, SIZE * workingStrip.size() + 2, SIZE + 2);
        UI.setColor(Color.black);

        //completed strips
        double y = TOP_STRIPS;
        for (List<Shred> strip : completedStrips) {
            x = LEFT;
            for (Shred shred : strip) {
                shred.draw(x, y);
                x += SIZE;
            }
            UI.drawRect(LEFT - 1, y - 1, SIZE * strip.size() + 2, SIZE + 2);
            y += SIZE + GAP;
        }
    }

    /**
     * Returns which column the mouse position is on.
     * This will be the index in the list of the shred that the mouse is on,
     * (or the index of the shred that the mouse would be on if the list were long enough)
     */
    public int getColumn(double x) {
        return (int) ((x - LEFT) / (SIZE));
    }

    /**
     * Returns the strip that the mouse position is on.
     * This may be the list of all remaining shreds, the working strip, or
     * one of the completed strips.
     * If it is not on any strip, then it returns null.
     */
    public List<Shred> getStrip(double y) {
        int row = (int) ((y - TOP_ALL) / (SIZE + GAP));
        if (row <= 0) {
            return allShreds;
        } else if (row == 1) {
            return workingStrip;
        } else if (row - 2 < completedStrips.size()) {
            return completedStrips.get(row - 2);
        } else {
            return null;
        }
    }

    /**
     * Load an image from a file and return as a two-dimensional array of Color.
     * From COMP 102 assignment 8&9.
     * Maybe useful for the challenge. Not required for the core or completion.
     */
    public Color[][] loadImage(String imageFileName) {
        if (imageFileName == null || !Files.exists(Path.of(imageFileName))) {
            return null;
        }
        try {
            BufferedImage img = ImageIO.read(Files.newInputStream(Path.of(imageFileName)));
            int rows = img.getHeight();
            int cols = img.getWidth();
            Color[][] ans = new Color[rows][cols];
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    Color c = new Color(img.getRGB(col, row));
                    ans[row][col] = c;
                }
            }
            return ans;
        } catch (IOException e) {
            UI.println("Reading Image from " + imageFileName + " failed: " + e);
        }
        return null;
    }

    /**
     * Save a 2D array of Color as an image file
     * From COMP 102 assignment 8&9.
     * Maybe useful for the challenge. Not required for the core or completion.
     */
    public void saveImage(Color[][] imageArray, String imageFileName) {
        int rows = imageArray.length;
        int cols = imageArray[0].length;
        BufferedImage img = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_RGB);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Color c = imageArray[row][col];
                img.setRGB(col, row, c.getRGB());
            }
        }
        try {
            if (imageFileName == null) {
                return;
            }
            ImageIO.write(img, "png", Files.newOutputStream(Path.of(imageFileName)));
        } catch (IOException e) {
            UI.println("Image reading failed: " + e);
        }

    }

}
