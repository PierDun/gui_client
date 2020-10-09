import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class Main {
    private static final int PORT = 1408;
    private static final String HOST = "localhost";
    private static ResourceManager resourceManager = new ResourceManager("data","ru");
    private static ArrayList<Chest> chestArrayList;
    private static final DateFormat df = new SimpleDateFormat ("dd.MM.yyyy");
    private static ArrayList<ChestButton> buttonsList = new ArrayList<>();
    private static ArrayList<ChestButton> filteredList = new ArrayList<>();

    private static void update(){
        Socket socket = null;
        try {
            socket = new Socket(HOST, PORT);
            if (socket.isConnected()) {
                try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
                    chestArrayList = (ArrayList<Chest>) in.readObject();
                    for(Chest chest: chestArrayList){
                        ChestButton curButton = new ChestButton(chest);
                        curButton.setBounds(curButton.getX(), curButton.getY(), curButton.getWidth(), curButton.getHeight());
                        buttonsList.add(curButton);
                    }
                }catch (EOFException | SocketException | ClassNotFoundException e){
                    e.printStackTrace();
                }
            }
        } catch (ConnectException exc){
            JOptionPane.showMessageDialog(null, "Сервер не отвечает, подождите");
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                assert socket != null;
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main (String[] args){
        SwingUtilities.invokeLater(ClientGUI::new);
    }

    public static class ClientGUI implements ActionListener {
        private AnimationPanel animationPanel;
        private JPanel mainPanel;
        JFormattedTextField formattedTextField = new JFormattedTextField(df);
        MaskFormatter dateMask;

        JButton animate = new JButton(resourceManager.getString("filter.animation.start"));
        JButton update = new JButton(resourceManager.getString("filter.download.collection"));
        RadButGroup group;

        JMenu menu;
        JMenuItem italian;
        JMenuItem dutch;
        JMenuItem english_us;
        JMenuItem russian;

        JSlider slider = new JSlider(0,100,5);

        Timer timer;
        int counter = 0;

        ClientGUI(){
            init();
        }

        private void init() {
            JFrame frame = new JFrame("Земнухов Владимир, Группа P3112");
            frame.setMinimumSize(new Dimension(1050,600));
            mainPanel = new JPanel(new GridLayout(1,2));
            animationPanel = new AnimationPanel(buttonsList);
            menu = new JMenu(resourceManager.getString("system.language"));
            italian = new JMenuItem(resourceManager.getString("system.language.italian"));
            menu.add(italian);
            italian.addActionListener(this);
            russian = new JMenuItem(resourceManager.getString("system.language.russian"));
            menu.add(russian);
            russian.addActionListener(this);
            dutch = new JMenuItem(resourceManager.getString("system.language.dutch"));
            menu.add(dutch);
            dutch.addActionListener(this);
            english_us = new JMenuItem(resourceManager.getString("system.language.english(us)"));
            menu.add(english_us);
            english_us.addActionListener(this);
            JPanel functionPanel = new JPanel();

            animate.addActionListener(this);
            update.addActionListener(this);

            group = new RadButGroup(functionPanel, resourceManager);

            JMenuBar menuBar = new JMenuBar();

            menuBar.add(menu);

            functionPanel.setLayout(new BoxLayout(functionPanel, BoxLayout.Y_AXIS));

            JPanel functionButtonsPanel = new JPanel(new FlowLayout());
            functionButtonsPanel.add(update);
            functionButtonsPanel.add(animate);

            timer = new Timer(50, e -> {
                if(counter <= 2000){
                    for(ChestButton button: filteredList){
                        button.setSize(button.getWidth() + 5, button.getHeight() + 5);
                    }
                    animationPanel.repaint();
                    counter += timer.getDelay();
                }else if (counter < 7000){
                    for (ChestButton button: filteredList){
                        button.setSize(button.getWidth() - 2, button.getHeight() - 2);
                    }
                    animationPanel.repaint();
                    counter += timer.getDelay();
                }else {
                    timer.stop();
                    counter = 0;
                    for (ChestButton button: filteredList){
                        button.setSize(100, 100);
                    }
                    animate.setText(resourceManager.getString("filter.animation.start"));
                    animate.setBackground(Color.GREEN);
                    animate.setOpaque(true);
                    animate.setBorderPainted(true);
                    animationPanel.repaint();
                }
            });

            update.setMinimumSize(new Dimension(100,20));

            animate.setMinimumSize(new Dimension(100,20));
            animate.setBackground(Color.GREEN);
            animate.setOpaque(true);
            animate.setBorderPainted(false);

            formattedTextField.setColumns(10);
            formattedTextField.setMaximumSize(new Dimension(300,150));

            slider.setMaximumSize(new Dimension(300,150));
            slider.setPaintLabels(true);
            slider.setPaintTicks(true);
            slider.setMajorTickSpacing(20);
            slider.setMinorTickSpacing(5);
            slider.setSnapToTicks(true);

            try {
                dateMask = new MaskFormatter(resourceManager.getString("filter.year.mask"));
                dateMask.install(formattedTextField);
            } catch (ParseException ex) {
                System.out.println(resourceManager.getString("filter.error.year"));
            }

            JPanel checkboxPanel = new JPanel(new FlowLayout());

            functionPanel.add(checkboxPanel);
            group.addRadioButton(resourceManager.getString("filter.color.red"));
            group.addRadioButton(resourceManager.getString("filter.color.green"));
            group.addRadioButton(resourceManager.getString("filter.color.blue"));
            group.addRadioButton(resourceManager.getString("filter.color.yellow"));
            functionPanel.add(formattedTextField);
            functionPanel.add(slider);
            functionPanel.add(functionButtonsPanel);

            mainPanel.add(animationPanel);
            mainPanel.add(functionPanel);

            frame.setJMenuBar(menuBar);
            frame.add(mainPanel);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }

        private void changeLanguage() {
            update.setText(resourceManager.getString("filter.update.collection"));
            animate.setText(resourceManager.getString("filter.animation.start"));
            menu.setText(resourceManager.getString("system.language"));
            russian.setText(resourceManager.getString("system.language.russian"));
            italian.setText(resourceManager.getString("system.language.italian"));
            english_us.setText(resourceManager.getString("system.language.english(us)"));
            dutch.setText(resourceManager.getString("system.language.dutch"));
            group.buttonList.get(0).setText(resourceManager.getString("filter.color.red"));
            group.buttonList.get(1).setText(resourceManager.getString("filter.color.green"));
            group.buttonList.get(2).setText(resourceManager.getString("filter.color.blue"));
            group.buttonList.get(3).setText(resourceManager.getString("filter.color.yellow"));
            try {
                dateMask.setMask(resourceManager.getString("filter.year.mask"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            formattedTextField.revalidate();
            formattedTextField.repaint();
        }

        @Override
        public void actionPerformed (ActionEvent e) {
            if (e.getActionCommand().equals(resourceManager.getString("filter.download.collection")) ||
                    e.getActionCommand().equals(resourceManager.getString("filter.update.collection"))){
                update();
                for(ChestButton button: buttonsList){
                    animationPanel.remove(button);
                }
                buttonsList.clear();

                for(Chest chest: chestArrayList){
                    ChestButton curButton = new ChestButton(chest);
                    System.out.println(curButton.getX() + "; " + curButton.getY());
                    curButton.setBounds(curButton.getX(),curButton.getY(),curButton.getWidth(),curButton.getHeight());
                    buttonsList.add(curButton);
                }
                animate.setText(resourceManager.getString("filter.animation.start"));
                animate.setBackground(Color.GREEN);
                animate.setOpaque(true);
                animate.setBorderPainted(false);
                timer.stop();
                animationPanel.repaint();
            }
            if (e.getActionCommand().equals(resourceManager.getString("filter.download.collection"))) {
                update.setText(resourceManager.getString("filter.update.collection"));
            }

            if (e.getActionCommand().equals(resourceManager.getString("filter.animation.start"))) {
                filteredList.clear();
                for(ChestButton button: buttonsList){
                    if (button.getChest().getSum() >= slider.getValue()
                            && button.getChest().getColor().equals(group.selectedColor)
                            && button.getChest().create_date.isBefore(LocalDateTime.ofInstant(
                            ((Date) formattedTextField.getValue()).toInstant(), ZoneId.systemDefault()
                    ))) {
                        filteredList.add(button);
                    }
                }
                animate.setBackground(Color.red);
                animate.setText(resourceManager.getString("filter.animation.stop"));
                timer.start();
            } else if (e.getActionCommand().equals(resourceManager.getString("filter.animation.stop"))) {
                animate.setBackground(Color.green);
                animate.setText(resourceManager.getString("filter.animation.start"));
                timer.stop();
            }

            if (e.getActionCommand().equals(resourceManager.getString("system.language.italian"))) {
                resourceManager.changeLocale("it");
                changeLanguage();
            }

            if (e.getActionCommand().equals(resourceManager.getString("system.language.dutch"))) {
                resourceManager.changeLocale("de");
                changeLanguage();
            }

            if (e.getActionCommand().equals(resourceManager.getString("system.language.russian"))) {
                resourceManager.changeLocale("ru");
                changeLanguage();
            }

            if (e.getActionCommand().equals(resourceManager.getString("system.language.english(us)"))) {
                resourceManager.changeLocale("us");
                changeLanguage();
            }
        }
    }

}