package game.gui;

import game.gui.input.GuiMouseHandler;
import game.gui.layout.GuiNullLayout;
import game.gui.style.GuiBackground;
import game.gui.style.GuiBorder;
import game.platform.Renderer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GuiTabPane extends GuiContainer {
    private final List<String> tabTitles = new ArrayList<>();
    private final List<GuiContainer> tabContents = new ArrayList<>();
    private int selectedTabIndex = 0;
    private final int tabHeight = 40;
    private final Color tabBackgroundColor = new Color(50, 50, 80, 200);
    private final Color activeTabBackgroundColor = new Color(70, 70, 120, 220);
    private final Color tabTextColor = Color.WHITE;
    private final Font tabFont = new Font("Arial", Font.BOLD, 16);

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
        private final String title;
        private final boolean active;

        public TabHeader(int x, int y, int width, int height, String title, boolean active) {
            super(x, y, width, height);
            this.title = title;
            this.active = active;
        }

        @Override
        protected void onRender(Renderer renderer) {
            super.onRender(renderer);

            // Draw tab background
            renderer.setColor(active ? activeTabBackgroundColor : tabBackgroundColor);
            renderer.fillRect(0, 0, getWidth(), getHeight());

            // Draw tab text
            renderer.setColor(tabTextColor);
            renderer.setFont(tabFont);

            FontMetrics metrics = renderer.getFontMetrics();
            int textWidth = metrics.stringWidth(title);
            int textHeight = metrics.getHeight();

            renderer.drawString(title, (getWidth() - textWidth) / 2, (getHeight() + textHeight / 2) / 2);

            // Draw bottom border except for active tab
            if (!active) {
                renderer.setColor(Color.WHITE);
                renderer.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            }
        }
    }
}