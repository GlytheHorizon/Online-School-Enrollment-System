package school.enrollment.theme;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Color;
import javax.swing.UIManager;

public class AppTheme {

    public static void apply() {
        FlatLightLaf.setup();

        UIManager.put("Button.arc", 18);
        UIManager.put("Component.arc", 16);
        UIManager.put("TextComponent.arc", 16);
        UIManager.put("TabbedPane.arc", 14);

        UIManager.put("Button.background", new Color(0x2563EB));
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.hoverBackground", new Color(0x1D4ED8));
        UIManager.put("Button.borderColor", new Color(0x2563EB));

        UIManager.put("Panel.background", new Color(0xF8FAFC));
        UIManager.put("Component.background", new Color(0xFFFFFF));
        UIManager.put("Component.focusColor", new Color(0x2563EB));
        UIManager.put("TextField.background", new Color(0xFFFFFF));
        UIManager.put("TextField.borderColor", new Color(0xCBD5E1));
        UIManager.put("Separator.foreground", new Color(0xCBD5E1));
        UIManager.put("Table.gridColor", new Color(0xE2E8F0));
        UIManager.put("Table.selectionBackground", new Color(0xDBEAFE));
        UIManager.put("Table.selectionForeground", new Color(0x0F172A));
    }
}
