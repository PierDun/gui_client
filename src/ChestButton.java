import javax.swing.*;

class ChestButton extends JButton {
    private Chest chest;
    private int x = (int) (Math.random() * 345), y = (int) (Math.random() * 420);

    ChestButton(Chest chest){
        this.chest = chest;
        setBackground(chest.getColor());
        setOpaque(true);
        setBorderPainted(true);
        setSize(100,100);
        setToolTipText(chest.getName());
    }

    Chest getChest() {
        return chest;
    }

    @Override
    public int getX() { return x; }

    @Override
    public int getY() {
        return y;
    }
}
