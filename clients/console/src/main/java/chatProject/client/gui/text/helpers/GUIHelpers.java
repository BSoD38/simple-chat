package chatProject.client.gui.text.helpers;

import chatProject.model.user.UserInfo;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.gui2.*;

/**
 * This class contains helpers to generate the different windows.
 */
public class GUIHelpers {

    /**
     * Adds a {@link Label} element with the given title to the given {@link Panel}.
     * @param title the title to add
     * @param contentPanel the panel that will hold the title
     */
    public static void addTitle(String title, Panel contentPanel) {
        Label label = new Label(title).addStyle(SGR.BOLD);
        contentPanel.addComponent(label);
        addHorizontalSeparator(contentPanel);
    }

    /**
     * Adds a close {@link Button} to the given {@link Panel} with a dedicated action.
     * @param contentPanel the panel that will hold the button
     * @param action the action to execute on click
     */
    public static void addCloseButton(Panel contentPanel, Runnable action) {

        // add an horizontal space
        contentPanel.addComponent(
                new EmptySpace()
                        .setLayoutData(
                                GridLayout.createHorizontallyFilledLayoutData(1)));

        // add an horizontal separator
        contentPanel.addComponent(
                new Separator(Direction.HORIZONTAL)
                        .setLayoutData(
                                GridLayout.createHorizontallyFilledLayoutData(1)));

        // add the button with the action
        contentPanel.addComponent(
                new Button("Close", action).setLayoutData(
                        GridLayout.createHorizontallyEndAlignedLayoutData(1)));

        // the button is added

        contentPanel.getChildCount();
        contentPanel.getChildCount();


    }

    /**
     * Adds a close {@link Button} to the given {@link Panel} that closes the given window.
     * @param contentPanel the panel that will hold the button
     * @param window the current window
     */
    public static void addCloseButton(Panel contentPanel, Window window) {
        final Runnable action = window::close;


        // add an horizontal space
        contentPanel.addComponent(
                new EmptySpace()
                        .setLayoutData(
                                GridLayout.createHorizontallyFilledLayoutData(1)));

        // add an horizontal separator
        contentPanel.addComponent(
                new Separator(Direction.HORIZONTAL)
                        .setLayoutData(
                                GridLayout.createHorizontallyFilledLayoutData(1)));

        // add the button with the action
        contentPanel.addComponent(
                new Button("Close", action).setLayoutData(
                        GridLayout.createHorizontallyEndAlignedLayoutData(1)));

        // the button is added

        contentPanel.getChildCount();
        contentPanel.getChildCount();
    }

    /**
     * Adds an horizontal separator in the given {@link Panel}.
     * @param contentPanel the panel that will hold the new separator
     */
    public static void addHorizontalSeparator(Panel contentPanel) {
        contentPanel.addComponent(
                new Separator(Direction.HORIZONTAL)
                        .setLayoutData(
                                GridLayout.createHorizontallyFilledLayoutData(1)));
    }

    /**
     * Gets a consistent representation for a {@link UserInfo}.
     * @param user the user model
     * @return the matching representation
     */
    public static String getUserRepresentation(UserInfo user) {
        return user.toString();
    }
}
