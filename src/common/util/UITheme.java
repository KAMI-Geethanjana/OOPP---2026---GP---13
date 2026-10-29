package common.util;

import javax.swing.*;
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
 * UITheme - Consistent modern styling helper for FTMS desktop application.
 * Replicates the clean, elegant UI shown in the university portal design.
 */
public class UITheme {

    // Color Palette
    public static final Color SIDEBAR_BG = new Color(20, 26, 42);          // Deep navy
    public static final Color SIDEBAR_HOVER = new Color(34, 43, 69);
    public static final Color SIDEBAR_ACTIVE = new Color(99, 102, 241);     // Vibrant purple-indigo
    public static final Color SIDEBAR_TEXT = new Color(160, 174, 192);
    public static final Color SIDEBAR_TEXT_ACTIVE = Color.WHITE;

    public static final Color CONTENT_BG = new Color(246, 248, 252);       // Soft modern grey
    public static final Color CARD_BG = Color.WHITE;
    public static final Color CARD_BORDER = new Color(229, 231, 235);

    public static final Color PRIMARY_BLUE = new Color(59, 130, 246);      // Blue
    public static final Color SUCCESS_GREEN = new Color(16, 185, 129);     // Emerald green
    public static final Color DANGER_RED = new Color(239, 68, 68);         // Crimson
    public static final Color WARNING_AMBER = new Color(245, 158, 11);     // Amber

    public static final Color TEXT_DARK = new Color(31, 41, 55);
    public static final Color TEXT_MUTED = new Color(107, 114, 128);

    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);

    /**
     * Creates a styled modern button with rounded corners and hover effect.
     */
    public static JButton createButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setForeground(fgColor);
        button.setFont(FONT_BODY_BOLD);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(button.getPreferredSize().width + 24, 38));
        return button;
    }

    /**
     * Creates a sidebar navigation button item.
     */
    public static JButton createSidebarButton(String text, boolean isActive) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isActive) {
                    g2.setColor(SIDEBAR_ACTIVE);
                    g2.fillRoundRect(8, 2, getWidth() - 16, getHeight() - 4, 12, 12);
                } else if (getModel().isRollover()) {
                    g2.setColor(SIDEBAR_HOVER);
                    g2.fillRoundRect(8, 2, getWidth() - 16, getHeight() - 4, 12, 12);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", isActive ? Font.BOLD : Font.PLAIN, 14));
        btn.setForeground(isActive ? SIDEBAR_TEXT_ACTIVE : SIDEBAR_TEXT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(10, 28, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(240, 44));
        return btn;
    }

    /**
     * Creates a styled modern white card panel with a subtle border.
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.setColor(CARD_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        return panel;
    }

    /**
     * Styles a JTable to match the modern dashboard aesthetic.
     */
    public static void styleTable(JTable table) {
        table.setRowHeight(32);
        table.setFont(FONT_BODY);
        table.setForeground(TEXT_DARK);
        table.setSelectionBackground(new Color(238, 242, 255));
        table.setSelectionForeground(new Color(67, 56, 202));
        table.setShowGrid(true);
        table.setGridColor(new Color(241, 245, 249));

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BODY_BOLD);
        header.setBackground(new Color(243, 244, 246));
        header.setForeground(new Color(75, 85, 99));
        header.setPreferredSize(new Dimension(header.getWidth(), 36));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 252));
                }
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return c;
            }
        });
    }

    /**
     * Creates a styled modern input text field.
     */
    public static JTextField createTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(FONT_BODY);
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 36));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219), 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return field;
    }

    /**
     * Creates a styled modern password field.
     */
    public static JPasswordField createPasswordField(int columns) {
        JPasswordField field = new JPasswordField(columns);
        field.setFont(FONT_BODY);
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 36));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219), 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return field;
    }

    /**
     * RoundedCardPanel - Container panel with custom rounded borders and background.
     */
    public static class RoundedCardPanel extends JPanel {
        private final int cornerRadius;
        private final Color backgroundColor;
        private final Color borderColor;

        public RoundedCardPanel(int cornerRadius, Color backgroundColor, Color borderColor) {
            super(new BorderLayout());
            this.cornerRadius = cornerRadius;
            this.backgroundColor = backgroundColor;
            this.borderColor = borderColor;
            setOpaque(false);
        }

        public RoundedCardPanel(int cornerRadius) {
            this(cornerRadius, Color.WHITE, CARD_BORDER);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);

            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * StatCard - KPI metric card displaying title, bold number, and accent color.
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
            lblValue.setFont(new Font("Segoe UI", Font.BOLD, 26));
            lblValue.setForeground(accentColor);

            content.add(lblTitle);
            content.add(Box.createVerticalStrut(4));
            content.add(lblValue);

            add(content, BorderLayout.CENTER);
        }

        public void setValue(String value) {
            lblValue.setText(value);
        }

        public JLabel getValueLabel() {
            return lblValue;
        }

        public JLabel getTitleLabel() {
            return lblTitle;
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
            setForeground(new Color(156, 163, 175));
            setFont(FONT_BODY);
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
                repaint();
            }
        }

        @Override
        public void focusLost(FocusEvent e) {
            if (getText().trim().isEmpty()) {
                setText(placeholder);
                setForeground(new Color(156, 163, 175));
                isShowingPlaceholder = true;
                repaint();
            }
        }

        public String getCleanText() {
            return isShowingPlaceholder ? "" : getText().trim();
        }

        public void setCleanText(String text) {
            if (text == null || text.trim().isEmpty()) {
                setText(placeholder);
                setForeground(new Color(156, 163, 175));
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
     * ModernPasswordField - Password field with placeholder support and rounded outline.
     */
    public static class ModernPasswordField extends JPasswordField implements FocusListener {
        private final String placeholder;
        private boolean isShowingPlaceholder;

        public ModernPasswordField(String placeholder) {
            this.placeholder = placeholder;
            this.isShowingPlaceholder = true;
            setEchoChar((char) 0);
            setText(placeholder);
            setForeground(new Color(156, 163, 175));
            setFont(FONT_BODY);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            setPreferredSize(new Dimension(200, 38));
            addFocusListener(this);
        }

        @Override
        public void focusGained(FocusEvent e) {
            if (isShowingPlaceholder) {
                setText("");
                setEchoChar('•');
                setForeground(TEXT_DARK);
                isShowingPlaceholder = false;
                repaint();
            }
        }

        @Override
        public void focusLost(FocusEvent e) {
            if (new String(getPassword()).trim().isEmpty()) {
                setEchoChar((char) 0);
                setText(placeholder);
                setForeground(new Color(156, 163, 175));
                isShowingPlaceholder = true;
                repaint();
            }
        }

        public String getCleanPassword() {
            return isShowingPlaceholder ? "" : new String(getPassword());
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
            setFont(FONT_BODY_BOLD);
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
}
