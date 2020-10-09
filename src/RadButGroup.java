import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class RadButGroup implements ActionListener {
    private JPanel panel;
    ButtonGroup group = new ButtonGroup();
    Color selectedColor;
    private ResourceManager resourceManager;
    ArrayList<JRadioButton> buttonList = new ArrayList<>();

    RadButGroup(JPanel panel, ResourceManager resourceManager) {
        this.panel = panel;
        this.resourceManager = resourceManager;
    }

    void addRadioButton(String name){
        JRadioButton radioButton = new JRadioButton(name);
        radioButton.addActionListener(this);
        group.add(radioButton);
        buttonList.add(radioButton);
        panel.add(radioButton);
    }

    @Override
    public void actionPerformed (ActionEvent e){
        selectedColor = null;
        Color color = null;
        if (e.getActionCommand().equals(resourceManager.getString("filter.color.red"))) {color = Color.red;}
        if (e.getActionCommand().equals(resourceManager.getString("filter.color.green"))) {color = Color.green;}
        if (e.getActionCommand().equals(resourceManager.getString("filter.color.blue"))) {color = Color.blue;}
        if (e.getActionCommand().equals(resourceManager.getString("filter.color.yellow"))) {color = Color.yellow;}
        selectedColor = color;
    }
}
