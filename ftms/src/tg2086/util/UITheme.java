package tg2086.util;

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
 * UITheme - Central Design System and Theme Utilities for FTMS Admin Module.
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
    public static final Color NEUTRAL_GRAY       = new Color(100, 116, 139); // Reset / Clear / Cancel

    // Table Colors
    public static final Color TABLE_HEADER_BG    = new Color(241, 245, 249); // Slate 100 (#F1F5F9)
    public static final Color TABLE_HEADER_FG    = new Color(71, 85, 105);   // Slate 600 (#475569)
    public static final Color TABLE_ROW_EVEN     = Color.WHITE;
    public static final Color TABLE_ROW_ODD      = new Color(248, 250, 252); // Slate 50 (#F8FAFC)
    public static final Color TABLE_ROW_SELECTED = new Color(224, 231, 255); // Indigo 100 (#E0E7FF)
    public static final Color TABLE_GRID         = new Color(241, 245, 249);

    // Metric / Stat Card Accent Borders
    public static final Color ACCENT_BLUE        = new Color(59, 130, 246);
    public static final Color ACCENT_PURPLE      = new Color(139, 92, 246);
    public static final Color ACCENT_EMERALD     = new Color(16, 185, 129);
    public static final Color ACCENT_AMBER       = new Color(245, 158, 11);

    // =========================================================================
    // TYPOGRAPHY (Segoe UI / Sans-Serif hierarchy)
    // =========================================================================
    public static final Font FONT_SIDEBAR_BRAND  = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_SIDEBAR_ITEM   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SIDEBAR_ACTIVE = new Font("Segoe UI", Font.BOLD, 13);

    public static final Font FONT_PAGE_TITLE     = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_PAGE_SUBTITLE  = new Font("Segoe UI", Font.PLAIN, 13);

    public static final Font FONT_CARD_TITLE     = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_METRIC_VALUE   = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font FONT_METRIC_LABEL   = new Font("Segoe UI", Font.PLAIN, 12);

    public static final Font FONT_TABLE_HEADER   = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_TABLE_BODY     = new Font("Segoe UI", Font.PLAIN, 13);

    public static final Font FONT_FORM_LABEL     = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_FORM_INPUT     = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BUTTON         = new Font("Segoe UI", Font.BOLD, 12);

    // =========================================================================
    // COMPONENT FACTORY METHODS
    // =========================================================================

    /**
     * Creates a styled modern action button with rounded corners and hover effect.
     */
    public static JButton createButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text) {
            private boolean hover = false;
            {
                setFont(FONT_BUTTON);
                setForeground(fgColor);
                setFocusPainted(false);
                setContentAreaFilled(false);
                setBorderPainted(false);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                setBorder(new EmptyBorder(8, 18, 8, 18));

                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hover = true;
                        repaint();
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        hover = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fill = hover ? bgColor.darker() : bgColor;
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        return button;
    }

    /**
     * Creates a standardized text field with subtle rounded borders and focus ring.
     */
    public static JTextField createTextField(int columns) {
        JTextField tf = new JTextField(columns) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tf.setFont(FONT_FORM_INPUT);
        tf.setForeground(TEXT_DARK);
        tf.setBackground(Color.WHITE);
        tf.setOpaque(false);
        setRoundedBorder(tf, CARD_BORDER, PRIMARY_BLUE);
        return tf;
    }

    /**
     * Creates a standardized password field.
     */
    public static JPasswordField createPasswordField(int columns) {
        JPasswordField pf = new JPasswordField(columns) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pf.setFont(FONT_FORM_INPUT);
        pf.setForeground(TEXT_DARK);
        pf.setBackground(Color.WHITE);
        pf.setOpaque(false);
        setRoundedBorder(pf, CARD_BORDER, PRIMARY_BLUE);
        return pf;
    }

    /**
     * Creates a standardized combo box.
     */
    public static <T> JComboBox<T> createComboBox(T[] items) {
        JComboBox<T> combo = new JComboBox<>(items);
        combo.setFont(FONT_FORM_INPUT);
        combo.setForeground(TEXT_DARK);
        combo.setBackground(Color.WHITE);
        combo.setBorder(new EmptyBorder(4, 6, 4, 6));
        return combo;
    }

    /**
     * Creates a rounded content card panel with subtle border.
     */
    public static JPanel createCardPanel() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.setColor(CARD_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16, 20, 16, 20));
        return card;
    }

    /**
     * Creates a Stat/Metric KPI Card with top colored accent line.
     */
    public static JPanel createMetricCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(5, 6)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.setColor(CARD_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, getWidth(), 4, 4, 4);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(14, 18, 14, 18));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_METRIC_LABEL);
        lblTitle.setForeground(TEXT_MUTED);

        valueLabel.setFont(FONT_METRIC_VALUE);
        valueLabel.setForeground(TEXT_DARK);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    /**
     * Styles a JTable matching modern clean data tables.
     */
    public static void styleTable(JTable table) {
        table.setFont(FONT_TABLE_BODY);
        table.setRowHeight(36);
        table.setGridColor(TABLE_GRID);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setSelectionBackground(TABLE_ROW_SELECTED);
        table.setSelectionForeground(TEXT_DARK);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_TABLE_HEADER);
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(TABLE_HEADER_FG);
        header.setPreferredSize(new Dimension(0, 38));
        header.setReorderingAllowed(false);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, CARD_BORDER));

        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);

        table.setDefaultRenderer(Object.class, new TableCellRenderer() {
            private final DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer();
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSel, boolean hasFocus, int row, int col) {
                Component c = defaultRenderer.getTableCellRendererComponent(t, val, isSel, false, row, col);
                if (!isSel) {
                    c.setBackground(row % 2 == 0 ? TABLE_ROW_EVEN : TABLE_ROW_ODD);
                    c.setForeground(TEXT_DARK);
                }
                if (c instanceof JLabel) {
                    ((JLabel) c).setBorder(new EmptyBorder(0, 10, 0, 10));
                }
                return c;
            }
        });
    }

    private static void setRoundedBorder(JComponent comp, Color normalColor, Color focusColor) {
        Border normalBorder = new CompoundBorder(
                new LineBorder(normalColor, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        );
        Border focusedBorder = new CompoundBorder(
                new LineBorder(focusColor, 2, true),
                new EmptyBorder(5, 9, 5, 9)
        );
        comp.setBorder(normalBorder);
        comp.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                comp.setBorder(focusedBorder);
            }
            @Override
            public void focusLost(FocusEvent e) {
                comp.setBorder(normalBorder);
            }
        });
    }
}
