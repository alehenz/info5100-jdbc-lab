/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package ui;

import java.awt.Image;
import javax.swing.ImageIcon;
import model.User;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import utility.DatabaseConnector;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import utility.UserValidator;

/**
 *
 * @author alehenz
 */
public class ViewJPanel extends javax.swing.JPanel {

    // Rows shown in the table, in the same order as the users list
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"ID", "First name", "Last name", "Age", "Email", "Continent"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;   // the table is for viewing and selecting only
        }
    };
    private List<User> users = new ArrayList<>();

    private boolean editing = false;

    public ViewJPanel() {
        initComponents();
        disableAllFields();

        usersTable.setModel(tableModel);
        usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usersTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            User selected = selectedUser();
            if (selected != null) {
                displayUser(selected);
            } else {
                clearFields();
            }
            setEditing(false);
        });
        setEditing(false);
        populateTable();
    }

    private User selectedUser() {
        int viewRow = usersTable.getSelectedRow();
        if (viewRow < 0) {
            return null;
        }
        return users.get(usersTable.convertRowIndexToModel(viewRow));
    }

    private void clearFields() {
        firstNameTextField.setText("");
        lastNameTextField.setText("");
        genderCombo.setSelectedIndex(-1);
        ageLabel.setText("Age:");
        phoneTextField.setText("");
        emailTextField.setText("");
        continentCombo.setSelectedIndex(-1);
        experienceTextArea.setText("");
        hobbiesTextArea.setText("");
        photoTextField.setText("");
        photoImageLabel.setIcon(null);
    }

    // Switches between viewing (fields locked) and editing (fields open)
    private void setEditing(boolean editing) {
        this.editing = editing;
        boolean rowSelected = usersTable.getSelectedRow() >= 0;
        firstNameTextField.setEnabled(editing);
        lastNameTextField.setEnabled(editing);
        genderCombo.setEnabled(editing);
        emailTextField.setEnabled(editing);
        phoneTextField.setEnabled(editing);
        continentCombo.setEnabled(editing);
        experienceTextArea.setEnabled(editing);
        hobbiesTextArea.setEnabled(editing);
        jCalendarCombo.setEnabled(editing);
        // the photo path stays read-only
        editButton.setText(editing ? "Cancel" : "Edit");
        editButton.setEnabled(rowSelected);
        updateButton.setEnabled(editing && rowSelected);
        deleteButton.setEnabled(rowSelected && !editing);
    }

    // Reads every user from the database and shows them in the table
    public void populateTable() {
        tableModel.setRowCount(0);
        try {
            users = DatabaseConnector.getUsers();
        } catch (SQLException ex) {
            users = new ArrayList<>();
            JOptionPane.showMessageDialog(this, "Could not load users:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        for (User u : users) {
            tableModel.addRow(new Object[]{u.getUserId(), u.getFirstName(), u.getLastName(),
                u.getAge(), u.getEmail(), u.getContinent()});
        }
    }

    private void deleteUser(User selected) {
        try {
            DatabaseConnector.deleteUser(selected);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not delete:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        populateTable();   // the table reloads, and with no row selected the fields clear
    }

    // Reads the open fields into a new User. The photo path is not editable, so it carries over.
    private User readEditedUser(User original) {
        User u = new User();
        u.setFirstName(firstNameTextField.getText().trim());
        u.setLastName(lastNameTextField.getText().trim());
        u.setGender((String) genderCombo.getSelectedItem());
        java.util.Date picked = jCalendarCombo.getDate();
        if (picked != null) {
            LocalDate dob = picked.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            u.setDateOfBirth(dob);
            u.setAge(Period.between(dob, LocalDate.now()).getYears());
        }
        u.setPhone(phoneTextField.getText().trim());
        u.setEmail(emailTextField.getText().trim());
        u.setContinent((String) continentCombo.getSelectedItem());
        u.setExperience(experienceTextArea.getText().trim());
        u.setHobbies(hobbiesTextArea.getText().trim());
        u.setPhotoPath(original.getPhotoPath());
        return u;
    }

    // Saves the change. Returns null on success, or a message that says why it failed.
    private String saveUpdate(User oldUser, User updatedUser) {
        try {
            if (DatabaseConnector.updateUser(oldUser, updatedUser) == 0) {
                return "That user no longer exists.";
            }
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1062) {   // MySQL error 1062: duplicate entry
                return "That email is already registered.";
            }
            return "Could not save to the database:\n" + ex.getMessage();
        }
        return null;
    }

    // Highlights the table row with this id, which also shows that user in the fields
    private void selectRowById(int id) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId() == id) {
                int viewRow = usersTable.convertRowIndexToView(i);
                usersTable.setRowSelectionInterval(viewRow, viewRow);
                return;
            }
        }
    }

    protected void showMessage(String message, String title, int type) {
        JOptionPane.showMessageDialog(this, message, title, type);
    }

    private void disableAllFields() {
        firstNameTextField.setEnabled(false);
        lastNameTextField.setEnabled(false);
        genderCombo.setEnabled(false);
        emailTextField.setEnabled(false);
        phoneTextField.setEnabled(false);
        continentCombo.setEnabled(false);
        experienceTextArea.setEnabled(false);
        hobbiesTextArea.setEnabled(false);
        photoTextField.setEnabled(false);
        jCalendarCombo.setEnabled(false);
    }

    public void displayUser(User user) {
        firstNameTextField.setText(user.getFirstName());
        lastNameTextField.setText(user.getLastName());
        genderCombo.setSelectedItem(user.getGender());
        if (user.getDateOfBirth() != null) {
            jCalendarCombo.setDate(java.util.Date.from(
                    user.getDateOfBirth().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));
        }
        ageLabel.setText("Age: " + user.getAge());
        phoneTextField.setText(user.getPhone());
        emailTextField.setText(user.getEmail());
        continentCombo.setSelectedItem(user.getContinent());
        experienceTextArea.setText(user.getExperience());
        hobbiesTextArea.setText(user.getHobbies());
        photoTextField.setText(user.getPhotoPath());
        photoImageLabel.setIcon(null);
        String photoPath = user.getPhotoPath();
        if (photoPath != null && !photoPath.trim().isEmpty()) {
            ImageIcon rawIcon = new ImageIcon(photoPath);
            if (rawIcon.getIconWidth() > 0) {
                Image scaled = rawIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                photoImageLabel.setIcon(new ImageIcon(scaled));
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        firstNameTextField = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        hobbiesTextArea = new javax.swing.JTextArea();
        lastNameTextField = new javax.swing.JTextField();
        phoneLabel = new javax.swing.JLabel();
        genderCombo = new javax.swing.JComboBox<>();
        experienceLabel1 = new javax.swing.JLabel();
        ageLabel1 = new javax.swing.JLabel();
        continentLabel = new javax.swing.JLabel();
        phoneTextField = new javax.swing.JTextField();
        experienceLabel = new javax.swing.JLabel();
        continentCombo = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        experienceTextArea = new javax.swing.JTextArea();
        photoLabel = new javax.swing.JLabel();
        photoTextField = new javax.swing.JTextField();
        jCalendarCombo = new de.wannawork.jcalendar.JCalendarComboBox();
        emailLabel = new javax.swing.JLabel();
        emailTextField = new javax.swing.JTextField();
        firstNameLabel = new javax.swing.JLabel();
        lastNameLabel = new javax.swing.JLabel();
        genderLabel = new javax.swing.JLabel();
        ageLabel = new javax.swing.JLabel();
        photoImageLabel = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        usersTable = new javax.swing.JTable();
        editButton = new javax.swing.JButton();
        updateButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();

        firstNameTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                firstNameTextFieldActionPerformed(evt);
            }
        });

        hobbiesTextArea.setColumns(20);
        hobbiesTextArea.setRows(5);
        jScrollPane2.setViewportView(hobbiesTextArea);

        phoneLabel.setText("Phone Number:");

        genderCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Male", "Female" }));
        genderCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genderComboActionPerformed(evt);
            }
        });

        experienceLabel1.setText("Hobbies:");

        ageLabel1.setText("Date of Birth:");

        continentLabel.setText("Continent:");

        phoneTextField.setToolTipText("123-456-7890");

        experienceLabel.setText("Experience:");

        continentCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Africa", "Asia", "Central America & Caribbean", "Europe", "North America", "Oceania", "South America" }));
        continentCombo.setToolTipText("");
        continentCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                continentComboActionPerformed(evt);
            }
        });

        experienceTextArea.setColumns(20);
        experienceTextArea.setRows(5);
        jScrollPane1.setViewportView(experienceTextArea);

        photoLabel.setText("Photo:");

        photoTextField.setColumns(16);
        photoTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                photoTextFieldActionPerformed(evt);
            }
        });

        jCalendarCombo.setToolTipText("");

        emailLabel.setText("Email:");

        emailTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emailTextFieldActionPerformed(evt);
            }
        });

        firstNameLabel.setText("First Name:");

        lastNameLabel.setText("Last Name:");

        genderLabel.setText("Gender:");

        ageLabel.setForeground(new java.awt.Color(153, 153, 153));
        ageLabel.setText("Age:");

        usersTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(usersTable);

        editButton.setText("Edit");
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });

        updateButton.setText("Update");
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });

        deleteButton.setText("Delete");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(experienceLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(experienceLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(photoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(photoImageLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGap(84, 84, 84))
                                .addComponent(photoTextField))))
                    .addComponent(emailLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(phoneLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(continentLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(continentCombo, 0, 270, Short.MAX_VALUE)
                                .addComponent(emailTextField))
                            .addComponent(phoneTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(ageLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lastNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(firstNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(firstNameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
                            .addComponent(lastNameTextField)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(genderLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jCalendarCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(ageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(genderCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 531, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(31, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(editButton)
                        .addGap(48, 48, 48)
                        .addComponent(updateButton)
                        .addGap(47, 47, 47)
                        .addComponent(deleteButton)
                        .addGap(129, 129, 129))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(firstNameLabel)
                            .addComponent(firstNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lastNameLabel)
                            .addComponent(lastNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(genderCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(genderLabel))
                                .addGap(12, 12, 12)
                                .addComponent(ageLabel1)
                                .addGap(14, 14, 14))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(ageLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jCalendarCombo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(10, 10, 10)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(experienceLabel)
                                .addGap(57, 57, 57))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(phoneLabel)
                                        .addGap(12, 12, 12))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(phoneTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(10, 10, 10)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(emailTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(emailLabel))
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(continentCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(continentLabel))
                                .addGap(10, 10, 10)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(experienceLabel1)
                                .addGap(54, 54, 54))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(photoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(photoLabel)))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 514, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(photoImageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(editButton)
                            .addComponent(updateButton)
                            .addComponent(deleteButton))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void firstNameTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_firstNameTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_firstNameTextFieldActionPerformed

    private void genderComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genderComboActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_genderComboActionPerformed

    private void continentComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_continentComboActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_continentComboActionPerformed

    private void photoTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_photoTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_photoTextFieldActionPerformed

    private void emailTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emailTextFieldActionPerformed

    }//GEN-LAST:event_emailTextFieldActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        if (editing) {
            User selected = selectedUser();
            if (selected != null) {
                displayUser(selected);   // Cancel: put back what is stored
            }
            setEditing(false);
        } else {
            setEditing(true);
        }
    }//GEN-LAST:event_editButtonActionPerformed

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
                User selected = selectedUser();
        if (selected == null || !editing) {
            return;
        }
        User updated = readEditedUser(selected);

        String problem = UserValidator.validate(updated);
        if (problem != null) {
            showMessage(problem, "Oops... Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String failure = saveUpdate(selected, updated);
        if (failure != null) {
            showMessage(failure, "Database Error", JOptionPane.ERROR_MESSAGE);
            return;   // stay in edit mode so the value can be corrected
        }

        populateTable();
        selectRowById(selected.getUserId());
        showMessage("User updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_updateButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        User selected = selectedUser();
        if (selected == null) {
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this,
                "Delete " + selected.getFirstName() + " " + selected.getLastName() + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            deleteUser(selected);
        }


    }//GEN-LAST:event_deleteButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel ageLabel;
    private javax.swing.JLabel ageLabel1;
    private javax.swing.JComboBox<String> continentCombo;
    private javax.swing.JLabel continentLabel;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton editButton;
    private javax.swing.JLabel emailLabel;
    private javax.swing.JTextField emailTextField;
    private javax.swing.JLabel experienceLabel;
    private javax.swing.JLabel experienceLabel1;
    private javax.swing.JTextArea experienceTextArea;
    private javax.swing.JLabel firstNameLabel;
    private javax.swing.JTextField firstNameTextField;
    private javax.swing.JComboBox<String> genderCombo;
    private javax.swing.JLabel genderLabel;
    private javax.swing.JTextArea hobbiesTextArea;
    private de.wannawork.jcalendar.JCalendarComboBox jCalendarCombo;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lastNameLabel;
    private javax.swing.JTextField lastNameTextField;
    private javax.swing.JLabel phoneLabel;
    private javax.swing.JTextField phoneTextField;
    private javax.swing.JLabel photoImageLabel;
    private javax.swing.JLabel photoLabel;
    private javax.swing.JTextField photoTextField;
    private javax.swing.JButton updateButton;
    private javax.swing.JTable usersTable;
    // End of variables declaration//GEN-END:variables

}
