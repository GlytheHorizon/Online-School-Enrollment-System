package school.enrollment;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import school.enrollment.view.MainView;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new MainView());
    }
}
