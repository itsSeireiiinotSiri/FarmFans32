import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class FileEditor {
    private static List<File> matchingFiles;
    private static JFrame pageFrame;
    private static JList<File> fileList;
    private static DefaultListModel<File> fileListModel;
    private static JTextField searchInput;

    public static void main(String[] args) {
        // Create the 'data' folder if it doesn't exist
        File dataFolder = new File("data");
        if (!dataFolder.exists()) {
            boolean created = dataFolder.mkdir();
            if (created) {
                System.out.println("'data' folder has been created");
            } else {
                System.out.println("Failed to create 'data' folder.");
            }
        }

        // Set up the JFrame and components
        pageFrame = new JFrame();
        pageFrame.setSize(500, 400);
        pageFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pageFrame.setLocationRelativeTo(null);
        pageFrame.setResizable(false);

        JLayeredPane layers = new JLayeredPane();
        layers.setSize(500, 400);

        JLabel title = new JLabel("Test 101");
        title.setSize(500, 50);
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setVerticalAlignment(JLabel.CENTER);
        title.setFont(new Font("Wawati TC", Font.BOLD, 30));

        searchInput = new JTextField("Keyword to search");
        searchInput.setBounds(0, 70, 500, 80);
        searchInput.setHorizontalAlignment(JTextField.HORIZONTAL);
        searchInput.setFont(new Font("Wawati TC", Font.BOLD, 30));

        // If Enter key is pressed, call searchFiles method
        searchInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                // Do nothing
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String keyword = searchInput.getText();
                    if (keyword != null && !keyword.isEmpty()) {
                        matchingFiles = new ArrayList<>();
                        searchFiles(dataFolder, keyword);
                        showSearchResults();
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid search keyword");
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // Do nothing
            }
        });

        layers.add(title);
        layers.add(searchInput);
        pageFrame.add(layers);
        pageFrame.setVisible(true);
    }

    static void searchFiles(File directory, String keyword) {
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    searchFiles(file, keyword); // Recursive call to search subdirectories
                } else if (file.isFile() && file.getName().endsWith(".txt")) {
                    if (containsSpecificContent(file, keyword)) {
                        matchingFiles.add(file);
                    }
                }
            }
        }
    }

    static boolean containsSpecificContent(File file, String keyword) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(keyword)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    static void showSearchResults() {
        if (matchingFiles.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No files containing the search keyword you are looking for");
        } else {
            fileListModel = new DefaultListModel<>();
            for (File file : matchingFiles) {
                fileListModel.addElement(file);
            }

            fileList = new JList<>(fileListModel);
            fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            fileList.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    File selectedFile = fileList.getSelectedValue();
                    if (selectedFile != null) {
                        openFileForEditing(selectedFile);
                    }
                }
            });

            JScrollPane scrollPane = new JScrollPane(fileList);
            scrollPane.setBounds(0, 150, 500, 250);

            pageFrame.getContentPane().removeAll(); // Clear existing components
            pageFrame.getContentPane().add(scrollPane);
            pageFrame.revalidate();
            pageFrame.repaint();
        }
    }

    static void openFileForEditing(File file) {
        try {
            Desktop.getDesktop().edit(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
