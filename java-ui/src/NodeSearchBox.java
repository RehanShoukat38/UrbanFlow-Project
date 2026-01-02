import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Simple searchable input with popup suggestions from a collection of node names.
 * Usage:
 *   NodeSearchBox box = new NodeSearchBox(mapOfNames);
 *   box.getTextField().getText()
 */
public class NodeSearchBox extends JPanel {
    private final JTextField field;
    private final JPopupMenu popup = new JPopupMenu();
    private final java.util.List<String> names;

    public NodeSearchBox(Collection<String> names) {
        this.names = new ArrayList<>(names);
        Collections.sort(this.names);
        setLayout(new BorderLayout());
        field = new JTextField();
        add(field, BorderLayout.CENTER);

        field.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                String t = field.getText().trim().toLowerCase();
                if (t.isEmpty()) {
                    popup.setVisible(false);
                    return;
                }
                java.util.List<String> matches = new ArrayList<>();
                for (String n : NodeSearchBox.this.names) {
                    if (n.toLowerCase().contains(t)) {
                        matches.add(n);
                        if (matches.size() >= 10) break;
                    }
                }
                showSuggestions(matches);
            }
        });

        field.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                // hide popup on focus lost
                SwingUtilities.invokeLater(() -> popup.setVisible(false));
            }
        });
    }

    private void showSuggestions(java.util.List<String> items) {
        popup.removeAll();
        if (items.isEmpty()) {
            popup.setVisible(false);
            return;
        }
        for (String s : items) {
            JMenuItem it = new JMenuItem(s);
            it.addActionListener(ae -> {
                field.setText(s);
                popup.setVisible(false);
            });
            popup.add(it);
        }
        popup.show(field, 0, field.getHeight());
        field.requestFocusInWindow();
    }

    public JTextField getTextField() {
        return field;
    }

    // helper to refresh list if nodes change
    public void setNames(Collection<String> names) {
        this.names.clear();
        this.names.addAll(names);
        Collections.sort(this.names);
    }
}
