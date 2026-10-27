package util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * UITheme - Central Design System and Theme Utilities for FTMS.
 * Defines standard colors, fonts, custom rounded components, tables, and buttons
 * matching the unified Faculty Technical Management System design specification.
 */
public final class UITheme {

    private UITheme() {
        // Prevent instantiation
    }

    // =========================================================================
    // COLOR PALETTE (Matched to Reference Screenshots)
    // =========================================================================

    // Sidebar & Navigation
    public static final Color SIDEBAR_BG         = new Color(15, 23, 42);   // Deep Slate / Navy (#0F172A)
    public static final Color SIDEBAR_ACTIVE_BG  = new Color(99, 102, 241); // Indigo / Purple pill (#6366F1)
    public static final Color SIDEBAR_ACTIVE_FG  = Color.WHITE;
    public static final Color SIDEBAR_TEXT       = new Color(148, 163, 184); // Slate 400 (#94A3B8)
    public static final Color SIDEBAR_HOVER      = new Color(30, 41, 59);    // Slate 800 (#1E293B)
    public static final Color SIDEBAR_BADGE_BG   = new Color(59, 130, 246);  // Blue badge (#3B82F6)

    // Sign Out Button
    public static final Color SIGNOUT_BG         = new Color(45, 21, 32);   // Dark Burgundy pill
    public static final Color SIGNOUT_FG         = new Color(248, 113, 113); // Light Red text (#F87171)
    public static final Color SIGNOUT_BORDER     = new Color(153, 27, 27);  // Dark Red border

    // Canvas & Cards
    public static final Color BG_LIGHT           = new Color(248, 250, 252); // Off-white canvas (#F8FAFC)
    public static final Color CARD_BG            = Color.WHITE;
    public static final Color CARD_BORDER        = new Color(226, 232, 240); // Slate 200 (#E2E8F0)
    public static final Color CARD_SHADOW        = new Color(0, 0, 0, 8);

    // Typography Colors
    public static final Color TEXT_DARK          = new Color(15, 23, 42);    // Dark Slate (#0F172A)
    public static final Color TEXT_MUTED         = new Color(100, 116, 139); // Muted Slate (#64748B)
    public static final Color TEXT_PLACEHOLDER   = new Color(156, 163, 175); // Gray 400

    // Action Buttons
    public static final Color PRIMARY_BLUE       = new Color(59, 130, 246);  // Update / Register / Search
    public static final Color SUCCESS_GREEN      = new Color(16, 185, 129);  // Add / Save / Approve / Present
    public static final Color DANGER_RED         = new Color(239, 68, 68);   // Delete / Deny / Absent
    public static final Color WARNING_AMBER      = new Color(245, 158, 11);  // Amber / Caution
    public static final Color SECONDARY_GRAY     = new Color(241, 245, 249); // Light Gray button / Outline

    // Tables
    public static final Color TABLE_HEADER_BG    = new Color(235, 238, 242);
    public static final Color TABLE_HEADER_FG    = new Color(51, 65, 85);
    public static final Color TABLE_ROW_ALT      = new Color(250, 251, 253);
    public static final Color TABLE_GRID         = new Color(241, 245, 249);
    public static final Color TABLE_SELECT_BG    = new Color(224, 231, 255);
    public static final Color TABLE_SELECT_FG    = new Color(15, 23, 42);

    // =========================================================================
    // FONTS (Segoe UI / Standard Clean Sans-Serif)
    // =========================================================================
    public static final Font FONT_TITLE_LARGE    = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE       = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_CARD_HEADER    = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_SECTION        = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_NAV            = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_NAV_ACTIVE     = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_REGULAR        = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD           = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL          = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BUTTON         = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_TABLE_HEADER   = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_TABLE_ROW      = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_STAT_NUMBER    = new Font("Segoe UI", Font.BOLD, 24);

    // =========================================================================
    // REUSABLE CUSTOM COMPONENTS
    // =========================================================================

    /**
     * RoundedCardPanel - Signature white container with soft rounded corners and border.
     */
    public static class RoundedCardPanel extends JPanel {
        private final int radius;
        private Color customBg;
        private Color customBorder;

        public RoundedCardPanel(int radius) {
            this(radius, CARD_BG, CARD_BORDER);
        }

        public RoundedCardPanel(int radius, Color bg, Color border) {
            this.radius = radius;
            this.customBg = bg;
            this.customBorder = border;
            setOpaque(false);
            setLayout(new BorderLayout());
        }

        public void setCustomBg(Color bg) {
            this.customBg = bg;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            // Soft shadow
            g2.setColor(CARD_SHADOW);
            g2.fillRoundRect(2, 3, width - 4, height - 3, radius, radius);

            // Card Body
            g2.setColor(customBg != null ? customBg : CARD_BG);
            g2.fillRoundRect(0, 0, width - 2, height - 2, radius, radius);

            // Subtle border
            g2.setColor(customBorder != null ? customBorder : CARD_BORDER);
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawRoundRect(0, 0, width - 2, height - 2, radius, radius);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * StatCard - Overview metric card showing a Title, Numeric Value, and Accent Indicator.
     * Matches the top 3 cards in the Admin Dashboard Overview screenshot.
     */
    public static class StatCard extends RoundedCardPanel {
        private final JLabel lblTitle;
        private final JLabel lblValue;

        public StatCard(String title, String initialValue, Color accentColor) {
            super(14, Color.WHITE, CARD_BORDER);
            setPreferredSize(new Dimension(240, 90));
            setBorder(new EmptyBorder(16, 20, 16, 20));

            JPanel content = new JPanel();
            content.setOpaque(false);
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

            lblTitle = new JLabel(title);
            lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lblTitle.setForeground(TEXT_MUTED);

            lblValue = new JLabel(initialValue);
            lblValue.setFont(FONT_STAT_NUMBER);
            lblValue.setForeground(accentColor);

            content.add(lblTitle);
            content.add(Box.createVerticalStrut(4));
            content.add(lblValue);

            add(content, BorderLayout.CENTER);
        }

        public void setValue(String value) {
            lblValue.setText(value);
        }
    }

    /**
     * ModernTextField - Rounded input field with placeholder support and blue focus outline.
     */
    public static class ModernTextField extends JTextField implements FocusListener {
        private final String placeholder;
        private boolean isShowingPlaceholder;

        public ModernTextField(String placeholder) {
            this.placeholder = placeholder;
            this.isShowingPlaceholder = true;
            setText(placeholder);
            setForeground(TEXT_PLACEHOLDER);
            setFont(FONT_REGULAR);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            setPreferredSize(new Dimension(200, 38));
            addFocusListener(this);
        }

        @Override
        public void focusGained(FocusEvent e) {
            if (isShowingPlaceholder) {
                setText("");
                setForeground(TEXT_DARK);
                isShowingPlaceholder = false;
            }
            repaint();
        }

        @Override
        public void focusLost(FocusEvent e) {
            if (getText().trim().isEmpty()) {
                setText(placeholder);
                setForeground(TEXT_PLACEHOLDER);
                isShowingPlaceholder = true;
            }
            repaint();
        }

        @Override
        public String getText() {
            return isShowingPlaceholder ? "" : super.getText();
        }

        public void setCustomText(String text) {
            if (text == null || text.trim().isEmpty()) {
                setText(placeholder);
                setForeground(TEXT_PLACEHOLDER);
                isShowingPlaceholder = true;
            } else {
                setText(text);
                setForeground(TEXT_DARK);
                isShowingPlaceholder = false;
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 8, 8);

            if (hasFocus()) {
                g2.setColor(PRIMARY_BLUE);
                g2.setStroke(new BasicStroke(1.6f));
            } else {
                g2.setColor(CARD_BORDER);
                g2.setStroke(new BasicStroke(1.0f));
            }
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 8, 8);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * ModernButton - Rounded button with hover animation and custom colors.
     */
    public static class ModernButton extends JButton {
        private final Color baseBg;
        private final Color hoverBg;
        private boolean isHover = false;
        private int cornerRadius = 8;

        public ModernButton(String text, Color baseBg, Color fg) {
            super(text);
            this.baseBg = baseBg;
            this.hoverBg = calculateHover(baseBg);
            setForeground(fg);
            setFont(FONT_BUTTON);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(8, 16, 8, 16));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHover = false;
                    repaint();
                }
            });
        }

        public void setCornerRadius(int radius) {
            this.cornerRadius = radius;
            repaint();
        }

        private Color calculateHover(Color c) {
            int r = Math.max(0, c.getRed() - 20);
            int g = Math.max(0, c.getGreen() - 20);
            int b = Math.max(0, c.getBlue() - 20);
            return new Color(r, g, b);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (!isEnabled()) {
                g2.setColor(new Color(203, 213, 225));
            } else if (isHover) {
                g2.setColor(hoverBg);
            } else {
                g2.setColor(baseBg);
            }

            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            g2.dispose();

            super.paintComponent(g);
        }
    }

    // =========================================================================
    // BUTTON FACTORY HELPERS
    // =========================================================================

    public static JButton createSuccessButton(String text) {
        return new ModernButton(text, SUCCESS_GREEN, Color.WHITE);
    }

    public static JButton createPrimaryButton(String text) {
        return new ModernButton(text, PRIMARY_BLUE, Color.WHITE);
    }

    public static JButton createDangerButton(String text) {
        return new ModernButton(text, DANGER_RED, Color.WHITE);
    }

    public static JButton createSecondaryButton(String text) {
        ModernButton btn = new ModernButton(text, Color.WHITE, TEXT_DARK);
        btn.setBorder(new CompoundBorder(
                new LineBorder(CARD_BORDER, 1, true),
                new EmptyBorder(7, 15, 7, 15)
        ));
        return btn;
    }

    // =========================================================================
    // TABLE STYLING
    // =========================================================================

    /**
     * Styles any JTable to match the sleek headers and zebra-striped rows in screenshots.
     */
    public static void styleTable(JTable table) {
        table.setRowHeight(34);
        table.setFont(FONT_TABLE_ROW);
        table.setSelectionBackground(TABLE_SELECT_BG);
        table.setSelectionForeground(TABLE_SELECT_FG);
        table.setGridColor(TABLE_GRID);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);

        // Header
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_TABLE_HEADER);
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(TABLE_HEADER_FG);
        header.setPreferredSize(new Dimension(0, 36));
        header.setReorderingAllowed(false);

        // Zebra striping renderer
        table.setDefaultRenderer(Object.class, new TableCellRenderer() {
            private final DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer();

            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int col) {
                Component c = defaultRenderer.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);

                if (isSelected) {
                    c.setBackground(TABLE_SELECT_BG);
                    c.setForeground(TABLE_SELECT_FG);
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : TABLE_ROW_ALT);
                    c.setForeground(TEXT_DARK);
                }

                // Add left padding for cleaner readability
                if (c instanceof JLabel) {
                    ((JLabel) c).setBorder(new EmptyBorder(0, 10, 0, 10));
                }

                return c;
            }
        });
    }

    /**
     * Wraps a table in a clean, borderless scroll pane with a thin outer border.
     */
    public static JScrollPane createTableScrollPane(JTable table) {
        styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(CARD_BORDER, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }

    /**
     * Creates a standard bottom record counter label ("Found 128 records.", "Loaded 4 notice(s).")
     */
    public static JLabel createRecordCountLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(new Color(16, 185, 129)); // Soft green/muted status
        lbl.setBorder(new EmptyBorder(6, 4, 4, 4));
        return lbl;
    }

    /**
     * Creates a circular avatar icon placeholder for the top-right header.
     */
    public static JComponent createAvatarPlaceholder(int size) {
        return new JComponent() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(size, size);
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(226, 232, 240)); // Soft slate circle (#CBD5E1 / #E2E8F0)
                g2.fillOval(0, 0, size, size);

                g2.setColor(new Color(148, 163, 184));
                // Minimalist silhouette
                int cx = size / 2;
                int headR = size / 5;
                g2.fillOval(cx - headR, size / 4, headR * 2, headR * 2);
                g2.fillArc(cx - (size / 3), size / 2, (size * 2) / 3, (size * 2) / 3, 0, 180);

                g2.dispose();
            }
        };
    }
}
