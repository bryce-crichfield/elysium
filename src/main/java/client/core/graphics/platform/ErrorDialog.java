package client.core.graphics.platform;

import java.awt.*;
import java.awt.event.*;

public class ErrorDialog extends Frame {
  private static final int WINDOW_WIDTH = 400;
  private static final int WINDOW_HEIGHT = 250;
  private static final int PADDING = 10;

  private final String errorMessage;

  public ErrorDialog(String title, String message) {
    super(title);
    this.errorMessage = message;

    setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
    setResizable(false);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout(PADDING, PADDING));

    add(createHeaderPanel(), BorderLayout.NORTH);
    add(createMessagePanel(), BorderLayout.CENTER);
    add(createButtonPanel(), BorderLayout.SOUTH);
    addPadding();

    setupEventHandlers();
    setVisible(true);
  }

  private Panel createHeaderPanel() {
    Panel panel = new Panel(new FlowLayout(FlowLayout.LEFT));
    panel.add(new Label("An error has occurred:"));
    return panel;
  }

  private Panel createMessagePanel() {
    Panel panel = new Panel(new BorderLayout());
    TextArea messageArea = new TextArea(errorMessage, 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
    messageArea.setEditable(false);
    panel.add(messageArea, BorderLayout.CENTER);
    return panel;
  }

  private Panel createButtonPanel() {
    Panel panel = new Panel(new FlowLayout(FlowLayout.RIGHT));
    panel.add(createSendButton());
    panel.add(createCloseButton());
    return panel;
  }

  private Button createSendButton() {
    Button button = new Button("Send Report");
    button.addActionListener(e -> showConfirmationDialog());
    return button;
  }

  private Button createCloseButton() {
    Button button = new Button("Close");
    button.addActionListener(e -> closeApplication());
    return button;
  }

  private void addPadding() {
    Panel paddingPanel = new Panel();
    paddingPanel.setLayout(new BorderLayout());
    add(new Label("  "), BorderLayout.WEST);
    add(new Label("  "), BorderLayout.EAST);
  }

  private void setupEventHandlers() {
    addWindowListener(
        new WindowAdapter() {
          @Override
          public void windowClosing(WindowEvent e) {
            closeApplication();
          }
        });
  }

  private void showConfirmationDialog() {
    Dialog dialog = createConfirmationDialog();
    dialog.setVisible(true);
  }

  private Dialog createConfirmationDialog() {
    Dialog dialog = new Dialog(this, "Report Sent", true);
    dialog.setLayout(new BorderLayout(PADDING, PADDING));
    dialog.add(new Label("Error report has been sent. Thank you."), BorderLayout.CENTER);

    Button okButton = new Button("OK");
    okButton.addActionListener(e -> dialog.dispose());

    Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.add(okButton);
    dialog.add(buttonPanel, BorderLayout.SOUTH);

    dialog.setSize(250, 100);
    dialog.setLocationRelativeTo(this);
    return dialog;
  }

  private void closeApplication() {
    dispose();
    System.exit(0);
  }

  public static void showError(String title, String message) {
    EventQueue.invokeLater(() -> new ErrorDialog(title, message));
  }
}
