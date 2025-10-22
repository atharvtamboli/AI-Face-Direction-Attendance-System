import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AttendanceDashboard extends JFrame implements ActionListener {

    JButton btnRegister, btnAttendance, btnDashboard, btnLogout;
    JPanel contentPanel;

    AttendanceDashboard() {
        setTitle("AI Real Time Attendance System");
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // --- Sidebar ---
        JPanel sidebar = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(22, 33, 62), 0, getHeight(), new Color(33, 50, 100));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebar.setPreferredSize(new Dimension(240, 650));
        sidebar.setLayout(new GridBagLayout());
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(255, 255, 255, 40)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridx = 0;

        JLabel logo = new JLabel(" AI Attendence System", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logo.setForeground(Color.WHITE);
        gbc.gridy = 0;
        sidebar.add(logo, gbc);

        gbc.gridy++;
        btnDashboard = createSideButton("Dashboard");
        sidebar.add(btnDashboard, gbc);

        gbc.gridy++;
        btnRegister = createSideButton("Face Registration");
        sidebar.add(btnRegister, gbc);

        gbc.gridy++;
        btnAttendance = createSideButton("Real-Time Attendance");
        sidebar.add(btnAttendance, gbc);

        gbc.gridy++;
        btnLogout = createSideButton("Logout");
        sidebar.add(btnLogout, gbc);

        add(sidebar, BorderLayout.WEST);

        // --- Header ---
        JPanel headerPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(33, 150, 243), getWidth(), 0, new Color(3, 169, 244));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setPreferredSize(new Dimension(850, 80));
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(255, 255, 255, 40)));

        JLabel titleLabel = new JLabel("AI Real Time Attendance System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // --- Content Panel ---
        contentPanel = new JPanel();
        contentPanel.setBackground(new Color(245, 247, 250));
        contentPanel.setLayout(new GridBagLayout());
        updateContent("Welcome to AI Attendance Dashboard");

        add(contentPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    // --- Sidebar Buttons ---
    private JButton createSideButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setOpaque(true);
                btn.setBackground(new Color(255, 255, 255, 40));
            }

            public void mouseExited(MouseEvent e) {
                btn.setOpaque(false);
            }
        });

        btn.addActionListener(this);
        return btn;
    }

    // --- Dynamic Content Loader ---
    private void updateContent(String message) {
        contentPanel.removeAll();

        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(600, 300));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        card.setLayout(new BorderLayout());

        JLabel msg = new JLabel(message, SwingConstants.CENTER);
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        card.add(msg, BorderLayout.CENTER);

        contentPanel.add(card);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // --- Database Operations ---
    private Connection getConnection() throws SQLException {
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
    }
    return DriverManager.getConnection("jdbc:mysql://localhost:3306/attendance_db", "root", "password");
}

    private boolean addStudent(int id, String name) {
        String sql = "INSERT INTO students(id, name) VALUES(?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, name);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getAttendance() {
        StringBuilder sb = new StringBuilder("<html><body>");
        String sql = "SELECT * FROM attendance";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                sb.append("ID: ").append(rs.getInt("student_id"))
                  .append(", Name: ").append(rs.getString("name"))
                  .append(", Status: ").append(rs.getString("status"))
                  .append("<br>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sb.append("</body></html>");
        return sb.toString();
    }

    // --- Action Events ---
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnRegister) {
            // Show registration form
            JTextField idField = new JTextField();
            JTextField nameField = new JTextField();
            Object[] form = {
                "Student ID:", idField,
                "Student Name:", nameField
            };
            int option = JOptionPane.showConfirmDialog(this, form, "Register Student", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    int id = Integer.parseInt(idField.getText());
                    String name = nameField.getText();
                    boolean added = addStudent(id, name);
                    updateContent(added ? "✅ Student Registered Successfully!" : "❌ Registration Failed!");
                } catch (NumberFormatException ex) {
                    updateContent("❌ Invalid ID format!");
                }
            }
        } else if (e.getSource() == btnAttendance) {
            String data = getAttendance();
            updateContent(data);
        } else if (e.getSource() == btnDashboard) {
            updateContent("📊 Dashboard Overview");
        } else if (e.getSource() == btnLogout) {
            JOptionPane.showMessageDialog(this, "Logged out successfully!");
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AttendanceDashboard::new);
    }
}
