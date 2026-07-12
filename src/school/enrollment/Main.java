package school.enrollment;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import school.enrollment.theme.AppTheme;
import school.enrollment.view.MainView;
import com.formdev.flatlaf.FlatLightLaf;

public class Main {
    public static void main(String[] args) {
   
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        AppTheme.apply();
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> new MainView());
    }
}
