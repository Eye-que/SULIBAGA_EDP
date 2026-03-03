package sulibagakent.Screens;

import DbConnection.ProductDAO;
import DbConnection.StockDAO;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockOutFrame extends javax.swing.JFrame {

    private InventoryManagement inventory;

    private final Map<String, Integer> productMap = new HashMap<>();
    private final SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
    private JDateChooser dcDate;

    public StockOutFrame(InventoryManagement inventory) {
        this.inventory = inventory;
        initComponents();
        setLocationRelativeTo(null);

        setupDateChooser();
        loadProductsToCombo();
    }
    private String generateReference(String prefix) {
    // Example: SIN-20260303-141233-582
    return prefix + "-" + new SimpleDateFormat("yyyyMMdd-HHmmss-SSS").format(new Date());
}
private ImageIcon safeIcon(String path) {
    java.net.URL url = getClass().getResource(path);
    if (url == null) {
        System.out.println("Icon not found: " + path);
        return null;
    }
    return new ImageIcon(url);
}
private void setupDateChooser() {
    dcDate = new JDateChooser();
    dcDate.setDateFormatString("yyyy-MM-dd");
    dcDate.setDate(new java.util.Date());

    ImageIcon cal = safeIcon("/sulibagakent/Icons/calendar.png");
    if (cal != null) dcDate.setIcon(cal);

    java.awt.Rectangle b = txtDate.getBounds();

    txtDate.setVisible(false);

    // IMPORTANT: AbsoluteLayout needs AbsoluteConstraints
    jPanel5.add(dcDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(b.x, b.y, b.width, b.height));

    jPanel5.revalidate();
    jPanel5.repaint();
}

    private void loadProductsToCombo() {
        try {
            productMap.clear();
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            model.addElement("Select Product");

            List<Object[]> rows = ProductDAO.fetchAll();
            for (Object[] r : rows) {
                int productId = Integer.parseInt(String.valueOf(r[0]));
                String barcode = String.valueOf(r[1]);
                String name = String.valueOf(r[2]);

                String display = barcode + " - " + name;
                productMap.put(display, productId);
                model.addElement(display);
            }

            cmbProduct.setModel(model);
            cmbProduct.setSelectedIndex(0);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load products: " + e.getMessage());
        }
    }

    private void clearFields() {
        cmbProduct.setSelectedIndex(0);
        txtQuantityAdded.setText("");
        txtRemarks.setText("");
        dcDate.setDate(java.sql.Date.valueOf(LocalDate.now()));
         txtReferenceNo.setText(generateReference("SIN"));
    }

    private void saveStockOut() {
        String selectedProduct = (String) cmbProduct.getSelectedItem();
        if (selectedProduct == null || selectedProduct.equals("Select Product")) {
            JOptionPane.showMessageDialog(this, "Please select a product.");
            return;
        }

        Integer productId = productMap.get(selectedProduct);
        if (productId == null) {
            JOptionPane.showMessageDialog(this, "Invalid product selection.");
            return;
        }

        String qtyStr = txtQuantityAdded.getText().trim();
        String reason = txtRemarks.getText().trim();

        if (qtyStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter quantity.");
            return;
        }
        if (reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Reason is required for Stock Out.");
            return;
        }

        int qty;
        try {
            qty = Integer.parseInt(qtyStr);
            if (qty <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than 0.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantity must be a whole number.");
            return;
        }

        // NOTE: Your StockDAO does not store custom date, so this is UI-only:
        if (dcDate.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Please select a date.");
            return;
        }
        String dateChosen = ymd.format(dcDate.getDate()); // UI only (optional display/log)

        try {
           String ref = txtReferenceNo.getText().trim();
boolean ok = StockDAO.stockOut(productId, qty, ref, reason);

            if (ok) {
                JOptionPane.showMessageDialog(this, "Stock Out saved!\nDate selected: " + dateChosen);

                if (inventory != null) inventory.refreshTables();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Save failed. Please try again.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Save failed: " + e.getMessage());
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        cmbProduct = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtQuantityAdded = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtDate = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtRemarks = new javax.swing.JTextArea();
        btnSave = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();
        btnView = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txtReferenceNo = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });

        jPanel5.setBackground(new java.awt.Color(94, 197, 168));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setText("Select Product:");
        jPanel5.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 60, -1, -1));

        jPanel5.add(cmbProduct, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 50, 240, 40));

        jLabel1.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel1.setText("POS - PRODUCT STOCKING MODULE");
        jPanel5.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 10, -1, -1));

        jLabel12.setText("Enter Quantity:");
        jPanel5.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 130, -1, -1));
        jPanel5.add(txtQuantityAdded, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 120, 240, 40));

        jLabel7.setText("Date:");
        jPanel5.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 60, -1, -1));
        jPanel5.add(txtDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 50, 240, 40));

        jLabel19.setText("Reason:");
        jPanel5.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 190, -1, -1));

        txtRemarks.setColumns(20);
        txtRemarks.setRows(5);
        jScrollPane1.setViewportView(txtRemarks);

        jPanel5.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 190, 240, 130));

        btnSave.setText("Save");
        btnSave.addActionListener(this::btnSaveActionPerformed);
        jPanel5.add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 380, -1, -1));

        btnClear.setText("Clear Fields");
        btnClear.addActionListener(this::btnClearActionPerformed);
        jPanel5.add(btnClear, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 380, -1, -1));

        btnExit.setText("Exit / Back");
        btnExit.addActionListener(this::btnExitActionPerformed);
        jPanel5.add(btnExit, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 380, 90, -1));

        btnView.setText("View Stock List");
        jPanel5.add(btnView, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 380, -1, -1));

        jLabel3.setText("Reference:");
        jPanel5.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 130, -1, -1));

        txtReferenceNo.setEditable(false);
        jPanel5.add(txtReferenceNo, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 120, 240, 40));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 820, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 820, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 420, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        saveStockOut();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        clearFields();
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to exit?",
            "Exit",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
        }
    }//GEN-LAST:event_btnExitActionPerformed

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
     txtReferenceNo.setText(generateReference("SIN"));
    }//GEN-LAST:event_formWindowActivated


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnView;
    private javax.swing.JComboBox<String> cmbProduct;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField txtDate;
    private javax.swing.JTextField txtQuantityAdded;
    private javax.swing.JTextField txtReferenceNo;
    private javax.swing.JTextArea txtRemarks;
    // End of variables declaration//GEN-END:variables
}
