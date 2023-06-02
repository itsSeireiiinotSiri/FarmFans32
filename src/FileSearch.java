import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;

public class FileSearch {
    private static DefaultListModel<String> eventListModel;
    private static JFrame pageFrame;
    private static JTextArea eventTextArea;
    private static JTextField searchInput;
    private static JList<String> eventList;
    private static final String FILE_PATH = "calendar_events.txt";
    private static int hoveredIndex = -1;
    private static int clickedIndex = -1;

    public void fetchAndSaveCalendar(List<String> calendarEvents) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (String event : calendarEvents) {
                writer.println(event);
            }
            System.out.println("Calendar events saved to file: " + FILE_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> convertToArrayList() {
        List<String> calendarEvents = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                calendarEvents.add(line);
            }
            System.out.println("Calendar events loaded from file: " + FILE_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return calendarEvents;
    }

    public void loadCalendar(List<String> calendarEvents) {
        eventListModel.clear();
        for (String event : calendarEvents) {
            eventListModel.addElement(event);
        }
    }

    public void saveCalendar() {
        int size = eventListModel.getSize();
        String[] eventsArray = new String[size];
        for (int i = 0; i < size; i++) {
            eventsArray[i] = eventListModel.getElementAt(i);
        }

        fetchAndSaveCalendar(Arrays.asList(eventsArray));
    }

    public static void main(String[] args) {
        eventListModel = new DefaultListModel<>(); // Initialize the event list model
        FileSearch fileSearch = new FileSearch();
        try {
            Path file = Paths.get(FILE_PATH);
            if (!Files.exists(file)) {
                Files.createFile(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Load the calendar events from the file
        List<String> calendarEvents = fileSearch.convertToArrayList();
        fileSearch.loadCalendar(calendarEvents);

        // Create the main frame
        pageFrame = new JFrame("Event Manager");
        pageFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pageFrame.setSize(500, 500);

        // Create the event list
        eventList = new JList<>(eventListModel);
        eventList.setFont(new Font("Wawati TC", Font.PLAIN, 16));
        eventList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        eventList.setLayoutOrientation(JList.VERTICAL);
        eventList.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int index = eventList.locationToIndex(e.getPoint());
                if (index != hoveredIndex) {
                    hoveredIndex = index;
                    eventList.repaint();
                }
            }
        });
        eventList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                clickedIndex = eventList.getSelectedIndex();
                if (clickedIndex != -1) {
                    if (e.getClickCount() == 2) {
                        String currentEvent = eventList.getSelectedValue();
                        String editedEvent = JOptionPane.showInputDialog(pageFrame, "Enter updated event description:", currentEvent);
                        if (editedEvent != null && !editedEvent.isEmpty()) {
                            eventListModel.setElementAt(editedEvent, clickedIndex);
                            fileSearch.saveCalendar();
                        }
                    }
                }
            }
        });

        // Create the scroll pane for the event list
        JScrollPane scrollPane = new JScrollPane(eventList);
        scrollPane.setBounds(0, 70, 500, 400);

        // Create the search input field
        searchInput = new JTextField();
        searchInput.setBounds(0, 0, 400, 30);
        searchInput.setFont(new Font("Wawati TC", Font.PLAIN, 16));
        searchInput.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String searchTerm = searchInput.getText();
                searchEvent(searchTerm);
            }
        });

        // Create the search button
        JButton searchButton = new JButton("Search");
        searchButton.setBounds(405, 0, 90, 30);
        searchButton.setFont(new Font("Wawati TC", Font.PLAIN, 16));
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String searchTerm = searchInput.getText();
                searchEvent(searchTerm);
            }
        });

        // Create the add event button
        JButton addButton = new JButton("Add Event");
        addButton.setBounds(0, 30, 100, 30);
        addButton.setFont(new Font("Wawati TC", Font.PLAIN, 16));
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String event = JOptionPane.showInputDialog(pageFrame, "Enter event description:");
                if (event != null && !event.isEmpty()) {
                    eventListModel.addElement(event);
                    fileSearch.saveCalendar();
                }
            }
        });

        // Create the clear button
        JButton clearButton = new JButton("Clear");
        clearButton.setBounds(405, 30, 90, 30);
        clearButton.setFont(new Font("Wawati TC", Font.PLAIN, 16));
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchInput.setText("");
                searchEvent("");
            }
        });

        // Create the delete all content button
        JButton deleteButton = new JButton("Delete All");
        deleteButton.setBounds(305, 30, 90, 30);
        deleteButton.setFont(new Font("Wawati TC", Font.PLAIN, 16));
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int response = JOptionPane.showConfirmDialog(pageFrame, "Are you sure you want to delete all events?", "Confirm Delete All", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    eventListModel.clear();
                    fileSearch.saveCalendar();
                }
            }
        });

        // Create the main panel and add components
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.add(searchInput);
        panel.add(searchButton);
        panel.add(addButton);
        panel.add(clearButton);
        panel.add(deleteButton);
        panel.add(scrollPane);

        // Add the panel to the frame and make it visible
        pageFrame.add(panel);
        pageFrame.setVisible(true);
    }

    private static void searchEvent(String searchTerm) {
        if (searchTerm.isEmpty()) {
            eventList.clearSelection();
            eventList.repaint();
            return;
        }

        List<String> filteredEvents = new ArrayList<>();
        for (int i = 0; i < eventListModel.getSize(); i++) {
            String event = eventListModel.getElementAt(i);
            if (event.toLowerCase().contains(searchTerm.toLowerCase())) {
                filteredEvents.add(event);
            }
        }

        eventListModel.clear();
        for (String event : filteredEvents) {
            eventListModel.addElement(event);
        }
        eventList.setSelectedIndex(0);
        eventList.repaint();
    }
}
