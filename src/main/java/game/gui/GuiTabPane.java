package game.gui;

import game.gui.input.GuiMouseHandler;
import game.gui.layout.GuiNullLayout;
import game.gui.style.GuiBackground;
import game.gui.style.GuiBorder;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GuiTabPane extends GuiContainer {
    private List<String> tabTitles = new ArrayList<>();
    private List<GuiContainer> tabContents = new ArrayList<>();
    private int selectedTabIndex = 0;
    private int tabHeight = 40;
    private Color tabBackgroundColor = new Color(50, 50, 80, 200);
    private Color activeTabBackgroundColor = new Color(70, 70, 120, 220);
    private Color tabTextColor = Color.WHITE;
    private Font tabFont = new Font("Arial", Font.BOLD, 16);

    public GuiTabPane(int x, int y, int width, int height) {
        super(x, y, width, height);
        setLayout(new GuiNullLayout());
        setBorder(new GuiBorder(Color.WHITE, 2));
        setBackground(new GuiBackground.Fill(new Color(30, 30, 60, 180)));
    }

    public void addTab(String title, GuiContainer content) {
        tabTitles.add(title);
        tabContents.add(content);

        // Position the content panel correctly
        content.setPosition(0, tabHeight);
        content.setSize(getWidth(), getHeight() - tabHeight);

        // Only add the first tab content initially
        if (tabContents.size() == 1) {
            addChild(content);
        }

        // Rebuild the tab headers
        rebuildTabHeaders();
    }

    private void rebuildTabHeaders() {
        // Remove existing tab headers
        getChildren().removeIf(child -> child instanceof TabHeader);

        // Calculate tab width based on number of tabs
        int tabWidth = getWidth() / Math.max(1, tabTitles.size());

        // Add new tab headers
        for (int i = 0; i < tabTitles.size(); i++) {
            final int tabIndex = i;
            TabHeader header = new TabHeader(i * tabWidth, 0, tabWidth, tabHeight, tabTitles.get(i), tabIndex == selectedTabIndex);

            header.addMouseHandler(GuiMouseHandler.onClick(() -> {
                selectTab(tabIndex);
            }));

            addChild(header);
        }
    }

    public void selectTab(int index) {
        if (index < 0 || index >= tabContents.size() || index == selectedTabIndex) {
            return;
        }

        // Remove the current content
        if (selectedTabIndex >= 0 && selectedTabIndex < tabContents.size()) {
//            removeChild(tabContents.get(selectedTabIndex));
        }

        // Update selected index
        selectedTabIndex = index;

        // Add the new content
        addChild(tabContents.get(selectedTabIndex));

        // Rebuild the tab headers to update the active state
        rebuildTabHeaders();
    }

    private class TabHeader extends GuiComponent {
        private String title;
        private boolean active;

        public TabHeader(int x, int y, int width, int height, String title, boolean active) {
            super(x, y, width, height);
            this.title = title;
            this.active = active;
        }

        @Override
        protected void onRender(Graphics2D g) {
            super.onRender(g);

            // Draw tab background
            g.setColor(active ? activeTabBackgroundColor : tabBackgroundColor);
            g.fillRect(0, 0, getWidth(), getHeight());

            // Draw tab text
            g.setColor(tabTextColor);
            g.setFont(tabFont);

            FontMetrics metrics = g.getFontMetrics();
            int textWidth = metrics.stringWidth(title);
            int textHeight = metrics.getHeight();

            g.drawString(title, (getWidth() - textWidth) / 2, (getHeight() + textHeight / 2) / 2);

            // Draw bottom border except for active tab
            if (!active) {
                g.setColor(Color.WHITE);
                g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            }
        }
    }
}