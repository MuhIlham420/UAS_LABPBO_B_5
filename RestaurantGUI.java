import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.lang.reflect.Method;

public class RestaurantGUI extends JFrame {
    private RestaurantSystem restaurant;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    // current logged-in account (null = not logged in)
    private Akun currentUser;
    private JButton btnLogout;
    private JLabel dashboardHeaderLabel;

    // navigation buttons promoted to fields so we can enable/disable based on login
    // state
    private JButton btnDashboard;
    private JButton btnMenu;
    private JButton btnTables;
    private JButton btnAuth;
    private JButton btnRefresh;
    // role-specific nav buttons
    private JButton btnPelayan; // label will be "Pemesanan"
    private JButton btnKoki;
    private JButton btnKasir; // label will be "Pembayaran"
    // role-specific panels
    private JPanel pemesananPanel;
    private JPanel kokiPanel;
    private JPanel kasirPanel;

    // New Order panel components (reuse for Pemesanan)
    private JComboBox<String> cbMejaGlobal;
    private JComboBox<String> cbMenuGlobal;
    private JSpinner spQtyGlobal;
    private JTextField tfCatatanGlobal;
    private DefaultListModel<String> currentOrderModel;

    // Tables panel (dibutuhkan oleh banyak panggilan refreshTablesPanel())
    private DefaultListModel<String> tablesListModel;
    private JList<String> tablesJList;

    public RestaurantGUI(RestaurantSystem restaurant) {
        this.restaurant = restaurant;
        // modern LAF
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // ignore, fallback to default
        }
        setTitle("Restaurant FIVE - Sistem Pemesanan");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 640);
        setLocationRelativeTo(null);
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        // Initial screen only shows auth panel (modern centered card)
        JPanel authWrapper = new JPanel(new GridBagLayout());
        authWrapper.setBackground(new Color(250, 250, 250));
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setPreferredSize(new Dimension(420, 340));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        JLabel logo = new JLabel("Restaurant FIVE", SwingConstants.CENTER);
        logo.setFont(new Font("SansSerif", Font.BOLD, 20));
        logo.setForeground(new Color(45, 62, 80));
        card.add(logo, BorderLayout.NORTH);
        card.add(buildAuthPanel(), BorderLayout.CENTER);
        authWrapper.add(card);
        setContentPane(authWrapper);
        // Note: main UI (sidebar+cards) will be created after login (buildMainUI)
    }

    // build main UI after successful login
    private void buildMainUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Left sidebar (store buttons in fields)
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBackground(new Color(45, 62, 80));

        JLabel title = new JLabel("<html><center><font color='white' size='5'>Restaurant FIVE</font></center></html>",
                SwingConstants.CENTER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        sidebar.add(title);

        // create nav buttons (kept as fields)
        btnDashboard = buildNavButton("Dashboard");
        btnMenu = buildNavButton("Lihat Menu");
        btnPelayan = buildNavButton("Pemesanan"); // renamed
        btnKoki = buildNavButton("Daftar Pesanan"); // renamed
        btnKasir = buildNavButton("Pembayaran"); // rename for sidebar label
        btnTables = buildNavButton("Info Meja");
        btnAuth = buildNavButton("Register/Login");
        btnRefresh = buildNavButton("Refresh Data");
        btnLogout = buildNavButton("Logout");
        btnLogout.addActionListener(e -> onLogout());

        // Decide which controls to show based on current user's role
        String role = getCurrentUserRole(); // already lower-cased inside method
        // Common
        sidebar.add(btnDashboard);
        sidebar.add(btnMenu);
        sidebar.add(Box.createVerticalStrut(8));

        if ("pelayan".equalsIgnoreCase(role)) {
            sidebar.add(btnPelayan);
            sidebar.add(btnTables);
        } else if ("koki".equalsIgnoreCase(role)) {
            sidebar.add(btnKoki);
        } else if ("kasir".equalsIgnoreCase(role)) {
            sidebar.add(btnKasir);
        } else if ("customer".equalsIgnoreCase(role)) {
            // customer sees only menu (no role button)
        } else {
            // default: staff/other - show all role buttons but disabled by updateAuthState
            sidebar.add(btnPelayan);
            sidebar.add(btnKoki);
            sidebar.add(btnKasir);
            sidebar.add(btnTables);
        }

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(btnLogout);

        // Refresh button: only visible for staff (we will hide via updateAuthState if
        // not staff)
        sidebar.add(btnRefresh);

        // card panel contains only the panels relevant to this role + common panels
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.add(buildDashboardPanel(), "dashboard");
        cardPanel.add(buildMenuPanel(), "menu");

        // Add only role-specific panels
        if ("pelayan".equalsIgnoreCase(role) || !"pelayan".equalsIgnoreCase(role) && !"koki".equalsIgnoreCase(role)
                && !"kasir".equalsIgnoreCase(role) && !"customer".equalsIgnoreCase(role)) {
            // If pelayan or default, add pemesanan/tables (neworder removed)
            pemesananPanel = buildPemesananPanel();
            cardPanel.add(pemesananPanel, "pemesanan");
            cardPanel.add(buildTablesPanel(), "tables");
        }
        if ("koki".equalsIgnoreCase(role) || !"pelayan".equalsIgnoreCase(role) && !"koki".equalsIgnoreCase(role)
                && !"kasir".equalsIgnoreCase(role) && !"customer".equalsIgnoreCase(role)) {
            kokiPanel = buildKokiPanel();
            cardPanel.add(kokiPanel, "koki");
        }
        if ("kasir".equalsIgnoreCase(role) || !"pelayan".equalsIgnoreCase(role) && !"koki".equalsIgnoreCase(role)
                && !"kasir".equalsIgnoreCase(role) && !"customer".equalsIgnoreCase(role)) {
            kasirPanel = buildKasirPanel();
            cardPanel.add(kasirPanel, "kasir");
        }

        // Always include auth so user can navigate back if needed
        cardPanel.add(buildAuthPanel(), "auth");

        // Wire navigation (handlers remain safe and check role where relevant)
        btnDashboard.addActionListener(e -> showCard("dashboard"));
        btnMenu.addActionListener(e -> {
            refreshMenuTable();
            showCard("menu");
        });

        btnPelayan.addActionListener(e -> {
            if (!"pelayan".equalsIgnoreCase(getCurrentUserRole())) {
                JOptionPane.showMessageDialog(this, "Hanya Pelayan yang dapat mengakses ini.");
                return;
            }
            refreshNewOrderData();
            showCard("pemesanan");
        });
        btnKoki.addActionListener(e -> {
            if (!"koki".equalsIgnoreCase(getCurrentUserRole())) {
                JOptionPane.showMessageDialog(this, "Hanya Koki yang dapat mengakses ini.");
                return;
            }
            refreshKokiTable();
            showCard("koki");
        });
        btnKasir.addActionListener(e -> {
            if (!"kasir".equalsIgnoreCase(getCurrentUserRole())) {
                JOptionPane.showMessageDialog(this, "Hanya Kasir yang dapat mengakses ini.");
                return;
            }
            refreshKasirTable();
            showCard("kasir");
        });
        btnTables.addActionListener(e -> {
            refreshTablesPanel();
            showCard("tables");
        });
        btnAuth.addActionListener(e -> showCard("auth"));
        btnRefresh.addActionListener(e -> {
            refreshAll();
            JOptionPane.showMessageDialog(this, "Data direfresh");
        });

        root.add(sidebar, BorderLayout.WEST);
        root.add(cardPanel, BorderLayout.CENTER);
        setContentPane(root);
        revalidate();
        repaint();

        // update visibility / enabled state according to role
        updateAuthState();
    }

    private JButton buildNavButton(String text) {
        JButton b = new JButton(text);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setFocusPainted(false);
        return b;
    }

    private JPanel buildDashboardPanel() {
        JPanel p = new JPanel(new BorderLayout());
        dashboardHeaderLabel = new JLabel(
                "<html><center><h1>Selamat Datang di Restaurant FIVE</h1><p>Silakan login atau daftar untuk mulai.</p></center></html>",
                SwingConstants.CENTER);
        p.add(dashboardHeaderLabel, BorderLayout.CENTER);
        return p;
    }

    // Auth panel (register/login)
    private JPanel buildAuthPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        JTextField tfName = new JTextField();
        JPasswordField pf = new JPasswordField();
        JButton btnReg = new JButton("Register");
        JButton btnLogin = new JButton("Login");

        c.gridx = 0;
        c.gridy = 0;
        p.add(new JLabel("Nama:"), c);
        c.gridx = 1;
        p.add(tfName, c);
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        p.add(new JLabel("Password:"), c);
        c.gridx = 1;
        p.add(pf, c);
        c.gridx = 0;
        c.gridy = 2;
        p.add(btnReg, c);
        c.gridx = 1;
        p.add(btnLogin, c);

        btnReg.addActionListener(e -> {
            String name = tfName.getText().trim();
            String pass = new String(pf.getPassword()).trim();
            if (name.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Isi nama & password");
                return;
            }
            if (restaurant.cekNamaPengguna(name)) {
                JOptionPane.showMessageDialog(this, "Nama sudah digunakan");
                return;
            }
            Customer cst = restaurant.registerCustomer(name, pass);
            // auto-login new customer and switch to main UI
            onLoginSuccess(cst);
            JOptionPane.showMessageDialog(this, "Registrasi sukses. Login sebagai: " + cst.getNama());
        });

        btnLogin.addActionListener(e -> {
            String name = tfName.getText().trim();
            String pass = new String(pf.getPassword()).trim();
            Akun a = restaurant.login(name, pass);
            if (a != null) {
                onLoginSuccess(a);
                JOptionPane.showMessageDialog(this, "Login sukses: " + a.getNama());
            } else {
                JOptionPane.showMessageDialog(this, "Login gagal", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        return p;
    }

    // Menu panel components (table)
    private JTabbedPane menuTabbed;
    private JTable makananTable;
    private DefaultTableModel makananTableModel;
    private JScrollPane makananScroll;
    private JTable minumanTable;
    private DefaultTableModel minumanTableModel;
    private JScrollPane minumanScroll;

    private JPanel buildMenuPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        JLabel lbl = new JLabel("Daftar Menu", SwingConstants.LEFT);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 16f));
        p.add(lbl, BorderLayout.NORTH);

        String[] cols = { "ID", "Nama", "Harga", "Tipe/Kelas", "Kategori (jika ada)" };
        makananTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        makananTable = new JTable(makananTableModel);
        makananScroll = new JScrollPane(makananTable);

        minumanTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        minumanTable = new JTable(minumanTableModel);
        minumanScroll = new JScrollPane(minumanTable);

        menuTabbed = new JTabbedPane();
        menuTabbed.addTab("Makanan", makananScroll);
        menuTabbed.addTab("Minuman", minumanScroll);

        p.add(menuTabbed, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnOrderFromMenu = new JButton("Buat Pesanan dari Item");
        btnOrderFromMenu.addActionListener(e -> openOrderFromSelected());
        bottom.add(btnOrderFromMenu);
        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    private void refreshMenuTable() {
        if (makananTableModel == null || minumanTableModel == null)
            return;
        makananTableModel.setRowCount(0);
        minumanTableModel.setRowCount(0);
        java.util.List<MenuItem> menus = restaurant.getDaftarMenu();
        if (menus == null)
            return;
        for (MenuItem m : menus) {
            String kategori = "";
            try {
                Method meth = m.getClass().getMethod("getKategori");
                Object k = meth.invoke(m);
                if (k != null)
                    kategori = k.toString();
            } catch (Exception ex) {
                /* ignore */ }
            Object[] row = new Object[] { m.getId(), m.getNama(), String.format("Rp %.0f", (double) m.getHarga()),
                    m.getClass().getSimpleName(), kategori };
            // categorize into makanan / minuman
            if (m.getClass().getSimpleName().toLowerCase().contains("minum") || m instanceof Minuman) {
                minumanTableModel.addRow(row);
            } else {
                makananTableModel.addRow(row);
            }
        }
    }

    private void openOrderFromSelected() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Silakan login terlebih dahulu untuk membuat pesanan.", "Perlu Login",
                    JOptionPane.INFORMATION_MESSAGE);
            showCard("auth");
            return;
        }
        // determine active tab and selected row
        if (menuTabbed == null) {
            JOptionPane.showMessageDialog(this, "Daftar menu belum siap.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int tab = menuTabbed.getSelectedIndex();
        JTable activeTable = (tab == 1) ? minumanTable : makananTable;
        if (activeTable == null) {
            JOptionPane.showMessageDialog(this, "Tabel menu belum siap.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int sel = activeTable.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Pilih item dari tabel menu terlebih dahulu.");
            return;
        }
        DefaultTableModel model = (DefaultTableModel) activeTable.getModel();
        int id = Integer.parseInt(model.getValueAt(sel, 0).toString());
        // navigate to new order card and preselect item in combo
        refreshNewOrderData();
        javax.swing.JComboBox<String> cbMenu = findComponentInCard("cbMenu");
        if (cbMenu != null) {
            for (int i = 0; i < cbMenu.getItemCount(); i++) {
                if (cbMenu.getItemAt(i).startsWith(id + " - ")) {
                    cbMenu.setSelectedIndex(i);
                    break;
                }
            }
        }
        showCard("pemesanan");
    }

    // ---------------- role-specific panels & helpers ----------------

    // Buat pesanan lalu proses pembayaran (terpusat)
    private void createOrderAndProcessPayment(Meja meja, java.util.List<DetailPesanan> daftarDetail) {
        if (meja == null || daftarDetail == null || daftarDetail.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Meja atau item tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Pesanan pes = restaurant.buatPesanan(meja, daftarDetail);
        if (pes == null) {
            JOptionPane.showMessageDialog(this, "Gagal membuat pesanan.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Pilih metode bayar
        String[] options = { "Cash", "Card", "QRIS", "Nanti (Hanya Pesan)" };
        int choice = JOptionPane.showOptionDialog(this,
                "Pesanan dibuat (ID " + pes.getIdPesanan() + "). Pilih metode pembayaran:",
                "Pembayaran", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == 3 || choice == JOptionPane.CLOSED_OPTION) {
            // Pembayaran ditunda; tetap biarkan pesanan di sistem (status MENUNGGU)
            JOptionPane.showMessageDialog(this,
                    "Pesanan dibuat dan dikirim ke dapur. Pembayaran bisa dilakukan di kasir nanti.");
            refreshKokiTable();
            refreshTablesPanel();
            return;
        }

        Pembayaran metode = null;
        if (choice == 0)
            metode = new CashPayment();
        else if (choice == 1)
            metode = new CardPayment();
        else if (choice == 2)
            metode = new QRISPayment();
        else {
            JOptionPane.showMessageDialog(this, "Metode tidak valid. Pesanan tetap dibuat tanpa pembayaran.");
            refreshKokiTable();
            refreshTablesPanel();
            return;
        }

        Transaksi trx = new Transaksi(restaurant.getNextTransaksiId(), pes, metode);
        try {
            trx.konfirmasi();
        } catch (Exception ignored) {
        }
        restaurant.tambahTransaksi(trx);
        restaurant.updateStatusPesanan(pes.getIdPesanan(), RestaurantSystem.STATUS_DIBAYAR);
        restaurant.simpanStrukKeFile(Struk.cetak(trx));
        JOptionPane.showMessageDialog(this, "Pesanan ID " + pes.getIdPesanan() + " dibayar. Struk disimpan.");
        refreshKokiTable();
        refreshKasirTable();
        refreshTablesPanel();
    }

    private JPanel buildPemesananPanel() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        JLabel h = new JLabel("Pemesanan - Buat Pesanan", SwingConstants.LEFT);
        h.setFont(h.getFont().deriveFont(Font.BOLD, 16f));
        p.add(h, BorderLayout.NORTH);

        // Pemesanan menu table (local snapshot) - multi-select untuk cepat pesan
        String[] colsMenu = { "ID", "Nama", "Harga", "Tipe" };
        DefaultTableModel pelayanMenuModel = new DefaultTableModel(colsMenu, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        for (MenuItem mi : restaurant.getDaftarMenu()) {
            pelayanMenuModel.addRow(new Object[] { mi.getId(), mi.getNama(),
                    String.format("Rp %.0f", (double) mi.getHarga()), mi.getClass().getSimpleName() });
        }
        JTable pelayanMenuTable = new JTable(pelayanMenuModel);
        pelayanMenuTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane spMenu = new JScrollPane(pelayanMenuTable);
        spMenu.setPreferredSize(new Dimension(420, 0));

        // Left: form + buttons; Right: menu + order list
        JPanel left = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        // reuse global components so refreshNewOrderData / openOrderFromSelected work
        if (cbMejaGlobal == null)
            cbMejaGlobal = new JComboBox<>();
        if (cbMenuGlobal == null)
            cbMenuGlobal = new JComboBox<>();
        if (spQtyGlobal == null)
            spQtyGlobal = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        if (tfCatatanGlobal == null)
            tfCatatanGlobal = new JTextField();
        JButton btnAddItem = new JButton("Tambah Item ke Pesanan Sementara");
        JButton btnSendOrder = new JButton("Kirim Pesanan ke Dapur");

        c.gridx = 0;
        c.gridy = 0;
        left.add(new JLabel("Pilih Meja:"), c);
        c.gridx = 1;
        left.add(cbMejaGlobal, c);
        c.gridx = 0;
        c.gridy = 1;
        left.add(new JLabel("Pilih Menu:"), c);
        c.gridx = 1;
        left.add(cbMenuGlobal, c);
        c.gridx = 0;
        c.gridy = 2;
        left.add(new JLabel("Jumlah:"), c);
        c.gridx = 1;
        left.add(spQtyGlobal, c);
        c.gridx = 0;
        c.gridy = 3;
        left.add(new JLabel("Catatan:"), c);
        c.gridx = 1;
        left.add(tfCatatanGlobal, c);
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 2;
        left.add(btnAddItem, c);
        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 2;
        left.add(btnSendOrder, c);

        // order list
        if (currentOrderModel == null)
            currentOrderModel = new DefaultListModel<>();
        JList<String> orderList = new JList<>(currentOrderModel);
        JScrollPane orderScroll = new JScrollPane(orderList);
        orderScroll.setPreferredSize(new Dimension(300, 150));

        btnAddItem.addActionListener(e -> {
            int mi = cbMenuGlobal.getSelectedIndex();
            if (mi < 0) {
                JOptionPane.showMessageDialog(this, "Pilih menu.");
                return;
            }
            String menuStr = cbMenuGlobal.getItemAt(mi);
            int qty = (Integer) spQtyGlobal.getValue();
            String cat = tfCatatanGlobal.getText().trim();
            currentOrderModel.addElement(menuStr + " x" + qty + (cat.isEmpty() ? "" : " [" + cat + "]"));
        });

        btnSendOrder.addActionListener(e -> {
            if (currentOrderModel.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Belum ada item di pesanan sementara");
                return;
            }
            if (cbMejaGlobal.getSelectedIndex() < 0) {
                JOptionPane.showMessageDialog(this, "Pilih meja");
                return;
            }
            Meja meja = restaurant.getDaftarMeja().get(cbMejaGlobal.getSelectedIndex());
            // Jika meja sudah ditempati, batalkan pembuatan pesanan dan beri pesan jelas
            if (!RestaurantSystem.MEJA_KOSONG.equals(meja.getStatus())) {
                JOptionPane.showMessageDialog(this,
                        "Pesanan gagal: Meja " + meja.getNomor() + " sudah ditempati.",
                        "Pesanan Gagal", JOptionPane.ERROR_MESSAGE);
                return;
            }
            java.util.List<DetailPesanan> daftarDetail = new ArrayList<>();
            for (int i = 0; i < currentOrderModel.size(); i++) {
                String line = currentOrderModel.get(i);
                try {
                    String[] parts = line.split(" - ", 2);
                    int menuId = Integer.parseInt(parts[0].trim());
                    int qty = 1;
                    int xIdx = line.lastIndexOf(" x");
                    if (xIdx != -1) {
                        String afterX = line.substring(xIdx + 2).split(" ")[0];
                        qty = Integer.parseInt(afterX);
                    }
                    String cat = "";
                    int cstart = line.indexOf("[");
                    if (cstart != -1) {
                        int cend = line.indexOf("]", cstart);
                        if (cend > cstart)
                            cat = line.substring(cstart + 1, cend);
                    }
                    MenuItem item = restaurant.findMenuItemById(menuId);
                    if (item != null)
                        daftarDetail.add(new DetailPesanan(item, qty, cat));
                } catch (Exception ex) {
                    /* skip malformed */ }
            }
            Pesanan pes = restaurant.buatPesanan(meja, daftarDetail);
            if (pes != null) {
                JOptionPane.showMessageDialog(this, "Pesanan dikirim. ID: " + pes.getIdPesanan());
                currentOrderModel.clear();
                refreshKokiTable();
                refreshKasirTable();
                refreshTablesPanel();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengirim pesanan.");
            }
        });

        // populate choices
        cbMejaGlobal.removeAllItems();
        for (Meja m : restaurant.getDaftarMeja())
            cbMejaGlobal.addItem(m.getNomor() + " (" + m.getStatus() + ")");
        cbMenuGlobal.removeAllItems();
        for (MenuItem mm : restaurant.getDaftarMenu())
            cbMenuGlobal.addItem(mm.getId() + " - " + mm.getNama());
        // reload pelayanMenuModel (in case menu changed)
        // (pelayanMenuModel already filled earlier; could be refreshed here if needed)

        JPanel right = new JPanel(new BorderLayout(6, 6));
        right.add(spMenu, BorderLayout.CENTER);
        right.add(orderScroll, BorderLayout.SOUTH);

        p.add(left, BorderLayout.WEST);
        p.add(right, BorderLayout.CENTER);
        return p;
    }

    // Koki panel
    private JTable kokiTable;
    private DefaultTableModel kokiModel;

    private JPanel buildKokiPanel() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        JLabel h = new JLabel("Daftar Pesanan - Pesanan untuk Dimasak", SwingConstants.LEFT);
        h.setFont(h.getFont().deriveFont(Font.BOLD, 16f));
        p.add(h, BorderLayout.NORTH);

        String[] cols = { "ID", "Meja", "Items", "Status" };
        kokiModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        kokiTable = new JTable(kokiModel);
        p.add(new JScrollPane(kokiTable), BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnProses = new JButton("Set Diproses");
        JButton btnSelesai = new JButton("Set Selesai");
        btns.add(btnProses);
        btns.add(btnSelesai);
        p.add(btns, BorderLayout.SOUTH);

        btnProses.addActionListener(e -> {
            int s = kokiTable.getSelectedRow();
            if (s < 0) {
                JOptionPane.showMessageDialog(this, "Pilih pesanan");
                return;
            }
            int id = Integer.parseInt(kokiModel.getValueAt(s, 0).toString());
            // gunakan API updateStatusPesanan
            boolean ok = restaurant.updateStatusPesanan(id, RestaurantSystem.STATUS_DIPROSES);
            if (ok) {
                restaurant.simpanSemuaDataPesanan();
                refreshKokiTable();
            }
        });
        btnSelesai.addActionListener(e -> {
            int s = kokiTable.getSelectedRow();
            if (s < 0) {
                JOptionPane.showMessageDialog(this, "Pilih pesanan");
                return;
            }
            int id = Integer.parseInt(kokiModel.getValueAt(s, 0).toString());
            boolean ok = restaurant.updateStatusPesanan(id, RestaurantSystem.STATUS_SELESAI);
            if (ok) {
                restaurant.simpanSemuaDataPesanan();
                refreshKokiTable();
                refreshKasirTable();
            }
        });
        return p;
    }

    private void refreshKokiTable() {
        if (kokiModel == null)
            return;
        kokiModel.setRowCount(0);
        java.util.List<Pesanan> list = restaurant.getPesananByStatuses(RestaurantSystem.STATUS_MENUNGGU,
                RestaurantSystem.STATUS_DIPROSES);
        for (Pesanan p : list) {
            String items = p.getDaftarItem().stream().map(d -> d.getItem().getNama() + " x" + d.getJumlah())
                    .reduce((a, b) -> a + ", " + b).orElse("");
            kokiModel.addRow(new Object[] { p.getIdPesanan(), p.getMeja().getNomor(), items, p.getStatus() });
        }
    }

    // Kasir panel
    private JTable kasirTable;
    private DefaultTableModel kasirModel;
    // riwayat transaksi
    private JTable historyTable;
    private DefaultTableModel historyModel;

    private JPanel buildKasirPanel() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        JLabel h = new JLabel("Pembayaran - Proses Pembayaran & Riwayat", SwingConstants.LEFT);
        h.setFont(h.getFont().deriveFont(Font.BOLD, 16f));
        p.add(h, BorderLayout.NORTH);

        // pembayaran tab (pesanan STATUS_SELESAI)
        String[] cols = { "ID", "Meja", "Total", "Status" };
        kasirModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        kasirTable = new JTable(kasirModel);

        JPanel payBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnBayar = new JButton("Proses Pembayaran (Tandai Dibayar)");
        payBtns.add(btnBayar);

        JPanel payPanel = new JPanel(new BorderLayout());
        payPanel.add(new JScrollPane(kasirTable), BorderLayout.CENTER);
        payPanel.add(payBtns, BorderLayout.SOUTH);

        // riwayat tab (transaksi)
        String[] colsHist = { "ID Transaksi", "ID Pesanan", "Meja", "Total", "Metode" };
        historyModel = new DefaultTableModel(colsHist, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        historyTable = new JTable(historyModel);
        JPanel histBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnViewStruk = new JButton("Lihat Struk & Rincian");
        histBtns.add(btnViewStruk);
        JPanel histPanel = new JPanel(new BorderLayout());
        histPanel.add(new JScrollPane(historyTable), BorderLayout.CENTER);
        histPanel.add(histBtns, BorderLayout.SOUTH);

        // tabbed pane
        JTabbedPane tab = new JTabbedPane();
        tab.addTab("Pembayaran", payPanel);
        tab.addTab("Riwayat", histPanel);
        p.add(tab, BorderLayout.CENTER);

        // pembayaran action (sama seperti sebelumnya)
        btnBayar.addActionListener(e -> {
            int s = kasirTable.getSelectedRow();
            if (s < 0) {
                JOptionPane.showMessageDialog(this, "Pilih pesanan");
                return;
            }
            int id = Integer.parseInt(kasirModel.getValueAt(s, 0).toString());
            Pesanan pObj = restaurant.findPesananById(id);
            if (pObj == null) {
                JOptionPane.showMessageDialog(this, "Pesanan tidak ditemukan.");
                return;
            }

            // preview rincian
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Pesanan ID: %d\nMeja: %d\nStatus: %s\n\n", pObj.getIdPesanan(),
                    pObj.getMeja().getNomor(), pObj.getStatus()));
            sb.append(String.format("%-4s %-30s %-6s %-10s\n", "No", "Item", "Jml", "Subtotal"));
            sb.append("------------------------------------------------------\n");
            int idx = 1;
            double total = 0;
            for (DetailPesanan d : pObj.getDaftarItem()) {
                double sub = d.getItem().getHarga() * d.getJumlah();
                total += sub;
                sb.append(String.format("%-4d %-30s %-6d Rp%.0f\n", idx++, d.getItem().getNama(), d.getJumlah(), sub));
            }
            sb.append("\nTotal: Rp").append(String.format("%.0f", total)).append("\n");
            JTextArea taPreview = new JTextArea(sb.toString());
            taPreview.setEditable(false);
            taPreview.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            taPreview.setCaretPosition(0);
            int confirm = JOptionPane.showConfirmDialog(this, new JScrollPane(taPreview),
                    "Rincian Pesanan - Konfirmasi Pembayaran", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);
            if (confirm != JOptionPane.OK_OPTION)
                return;

            if (pObj.getStatus().equals(RestaurantSystem.STATUS_DIBAYAR)) {
                JOptionPane.showMessageDialog(this, "Pesanan ini sudah dibayar.");
                return;
            }
            if (!pObj.getStatus().equals(RestaurantSystem.STATUS_SELESAI)) {
                JOptionPane.showMessageDialog(this, "Pesanan belum siap dibayar (status: " + pObj.getStatus() + ").");
                return;
            }

            String[] options = { "Cash", "Card", "QRIS" };
            int choice = JOptionPane.showOptionDialog(this, "Pilih metode pembayaran:", "Pembayaran",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            Pembayaran metode = null;
            if (choice == 0)
                metode = new CashPayment();
            else if (choice == 1)
                metode = new CardPayment();
            else if (choice == 2)
                metode = new QRISPayment();
            else
                return;

            Transaksi trx = new Transaksi(restaurant.getNextTransaksiId(), pObj, metode);
            try {
                trx.konfirmasi();
            } catch (Exception ignored) {
            }
            restaurant.tambahTransaksi(trx);
            restaurant.updateStatusPesanan(id, RestaurantSystem.STATUS_DIBAYAR);
            String strukText = Struk.cetak(trx);
            restaurant.simpanStrukKeFile(strukText);
            refreshKasirTable();
            refreshTablesPanel();
            refreshHistoryTable();

            JTextArea taStruk = new JTextArea(strukText);
            taStruk.setEditable(false);
            taStruk.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            taStruk.setCaretPosition(0);
            JOptionPane.showMessageDialog(this, new JScrollPane(taStruk), "Preview Struk (Sudah Disimpan)",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        // lihat struk & rincian dari riwayat
        btnViewStruk.addActionListener(e -> {
            int r = historyTable.getSelectedRow();
            if (r < 0) {
                JOptionPane.showMessageDialog(this, "Pilih item riwayat.");
                return;
            }
            try {
                int trxId = Integer.parseInt(historyModel.getValueAt(r, 0).toString());
                // ambil daftar transaksi via reflection (nama metode di RestaurantSystem bisa
                // berbeda)
                java.util.List<?> trxList = null;
                try {
                    Method m = restaurant.getClass().getMethod("getDaftarTransaksi");
                    Object o = m.invoke(restaurant);
                    if (o instanceof java.util.List)
                        trxList = (java.util.List<?>) o;
                } catch (Exception ex) {
                    try {
                        Method m2 = restaurant.getClass().getMethod("getTransaksiList");
                        Object o2 = m2.invoke(restaurant);
                        if (o2 instanceof java.util.List)
                            trxList = (java.util.List<?>) o2;
                    } catch (Exception ignored) {
                    }
                }

                if (trxList == null || trxList.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Tidak ada data transaksi di sistem.", "Info",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                Object found = null;
                for (Object t : trxList) {
                    try {
                        Method gid = t.getClass().getMethod("getIdTransaksi");
                        Object val = gid.invoke(t);
                        if (val != null && Integer.parseInt(val.toString()) == trxId) {
                            found = t;
                            break;
                        }
                    } catch (Exception ex) {
                        try {
                            Method gid2 = t.getClass().getMethod("getId");
                            Object val2 = gid2.invoke(t);
                            if (val2 != null && Integer.parseInt(val2.toString()) == trxId) {
                                found = t;
                                break;
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }

                if (found == null) {
                    JOptionPane.showMessageDialog(this, "Transaksi tidak ditemukan.", "Info",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                // Panggil Struk.cetak(...) via reflection, atau fallback to toString()
                String struk = null;
                try {
                    // coba metode Struk.cetak(...) yang menerima tipe transaksi
                    Method foundMeth = null;
                    for (Method m : Struk.class.getDeclaredMethods()) {
                        if (!m.getName().equals("cetak"))
                            continue;
                        Class<?>[] params = m.getParameterTypes();
                        if (params.length == 1) {
                            if (params[0].isAssignableFrom(found.getClass())) {
                                foundMeth = m;
                                break;
                            }
                        }
                    }
                    if (foundMeth == null) {
                        // coba signature umum cetak(Object)
                        try {
                            foundMeth = Struk.class.getMethod("cetak", Object.class);
                        } catch (Exception ex) {
                            foundMeth = null;
                        }
                    }
                    if (foundMeth != null) {
                        Object res = foundMeth.invoke(null, found);
                        if (res != null)
                            struk = res.toString();
                    }
                } catch (Exception ex) {
                    // fallback ke toString()
                }
                if (struk == null)
                    struk = found.toString();

                JTextArea ta = new JTextArea(struk);
                ta.setEditable(false);
                ta.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
                ta.setCaretPosition(0);
                JOptionPane.showMessageDialog(this, new JScrollPane(ta), "Struk Transaksi " + trxId,
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Data riwayat tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        return p;
    }

    private void refreshKasirTable() {
        if (kasirModel == null)
            return;
        kasirModel.setRowCount(0);
        try {
            java.util.List<Pesanan> list = restaurant.getPesananByStatus(RestaurantSystem.STATUS_SELESAI);
            if (list == null)
                return;
            for (Pesanan p : list) {
                kasirModel.addRow(new Object[] { p.getIdPesanan(), p.getMeja().getNomor(),
                        String.format("Rp %.0f", (double) p.getTotalHarga()), p.getStatus() });
            }
        } catch (Exception ignored) {
        }
    }

    // Refresh tabel riwayat transaksi (dipanggil setelah pembayaran atau pada
    // refreshAll)
    private void refreshHistoryTable() {
        if (historyModel == null)
            return;
        historyModel.setRowCount(0);
        try {
            java.util.List<?> trxList = null;
            try {
                Method m = restaurant.getClass().getMethod("getDaftarTransaksi");
                Object o = m.invoke(restaurant);
                if (o instanceof java.util.List) trxList = (java.util.List<?>) o;
            } catch (Exception ex) {
                try {
                    Method m2 = restaurant.getClass().getMethod("getTransaksiList");
                    Object o2 = m2.invoke(restaurant);
                    if (o2 instanceof java.util.List) trxList = (java.util.List<?>) o2;
                } catch (Exception ignored) {}
            }
            if (trxList == null) return;
            for (Object t : trxList) {
                int trxId = 0;
                int pesId = 0;
                int mejaNo = 0;
                double total = 0;
                String metode = "";
                try {
                    try {
                        Method gid = t.getClass().getMethod("getIdTransaksi");
                        Object v = gid.invoke(t);
                        if (v != null) trxId = Integer.parseInt(v.toString());
                    } catch (Exception ex) { /* ignore */ }
                    Object pes = null;
                    try {
                        Method gp = t.getClass().getMethod("getPesanan");
                        pes = gp.invoke(t);
                    } catch (Exception ex) {
                        try {
                            Method gp2 = t.getClass().getMethod("getOrder");
                            pes = gp2.invoke(t);
                        } catch (Exception ignored) {
                        }
                    }
                    if (pes != null) {
                        try {
                            Method gidp = pes.getClass().getMethod("getIdPesanan");
                            Object vp = gidp.invoke(pes);
                            if (vp != null) pesId = Integer.parseInt(vp.toString());
                        } catch (Exception ex) {
                        }
                        try {
                            Method gmeja = pes.getClass().getMethod("getMeja");
                            Object mobj = gmeja.invoke(pes);
                            if (mobj != null) {
                                Method gnom = mobj.getClass().getMethod("getNomor");
                                Object nom = gnom.invoke(mobj);
                                if (nom != null) mejaNo = Integer.parseInt(nom.toString());
                            }
                        } catch (Exception ex) {
                        }
                        try {
                            Method gtotal = pes.getClass().getMethod("getTotalHarga");
                            Object tov = gtotal.invoke(pes);
                            if (tov != null) total = Double.parseDouble(tov.toString());
                        } catch (Exception ex) {
                        }
                    }
                    try {
                        Method gm = t.getClass().getMethod("getMetode");
                        Object mm = gm.invoke(t);
                        if (mm != null) metode = mm.getClass().getSimpleName();
                    } catch (Exception ex) {
                    }
                } catch (Exception ignored) {
                }
                historyModel.addRow(new Object[] { trxId, pesId, mejaNo, String.format("Rp %.0f", total), metode });
            }
        } catch (Exception ignored) {
        }
    }

    // jika refreshTablesPanel belum ada (safety), definisikan juga
    private void refreshTablesPanel() {
        if (tablesListModel == null) {
            tablesListModel = new DefaultListModel<>();
        }
        tablesListModel.clear();
        try {
            java.util.List<Meja> mejaList = restaurant.getDaftarMeja();
            if (mejaList == null)
                return;
            for (Meja m : mejaList) {
                tablesListModel.addElement(String.format("Meja %d : %s", m.getNomor(), m.getStatus()));
            }
            if (tablesJList != null)
                tablesJList.setModel(tablesListModel);
        } catch (Exception ignored) {
        }
    }

    // Tambahan: helper-methods yang sebelumnya hilang (gabungkan sebelum '}' akhir
    // kelas)
    private JPanel buildTablesPanel() {
        if (tablesListModel == null)
            tablesListModel = new DefaultListModel<>();
        tablesJList = new JList<>(tablesListModel);
        JScrollPane sp = new JScrollPane(tablesJList);
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(new JLabel("Status Meja"), BorderLayout.NORTH);
        wrapper.add(sp, BorderLayout.CENTER);
        return wrapper;
    }

    @SuppressWarnings("unchecked")
    private <T extends JComponent> T findComponentInCard(String name) {
        // dukung cbMenu / cbMeja yang digunakan oleh openOrderFromSelected
        if ("cbMenu".equals(name) || "cbMenuGlobal".equals(name))
            return (T) cbMenuGlobal;
        if ("cbMeja".equals(name) || "cbMejaGlobal".equals(name))
            return (T) cbMejaGlobal;
        return null;
    }

    private void refreshNewOrderData() {
        // safe-init dan isi combo meja/menu
        if (cbMejaGlobal == null)
            cbMejaGlobal = new JComboBox<>();
        if (cbMenuGlobal == null)
            cbMenuGlobal = new JComboBox<>();
        cbMejaGlobal.removeAllItems();
        try {
            java.util.List<Meja> mejaList = restaurant.getDaftarMeja();
            if (mejaList != null)
                for (Meja m : mejaList)
                    cbMejaGlobal.addItem(m.getNomor() + " (" + m.getStatus() + ")");
        } catch (Exception ignored) {
        }
        cbMenuGlobal.removeAllItems();
        try {
            java.util.List<MenuItem> menuList = restaurant.getDaftarMenu();
            if (menuList != null)
                for (MenuItem mi : menuList)
                    cbMenuGlobal.addItem(mi.getId() + " - " + mi.getNama() + " (Rp " + mi.getHarga() + ")");
        } catch (Exception ignored) {
        }
    }

    private void refreshAll() {
        // panggil semua refresh yang relevan
        refreshMenuTable();
        refreshNewOrderData();
        refreshTablesPanel();
        try {
            refreshKokiTable();
        } catch (Exception ignored) {
        }
        try {
            refreshKasirTable();
        } catch (Exception ignored) {
        }
        try {
            refreshHistoryTable();
        } catch (Exception ignored) {
        }
    }

    private void onLoginSuccess(Akun a) {
        this.currentUser = a;
        // rebuild main UI and refresh data
        buildMainUI();
        refreshAll();
        // update dashboard header if ada
        if (dashboardHeaderLabel != null && a != null) {
            String tipe = a.getClass().getSimpleName();
            dashboardHeaderLabel.setText(
                    String.format("<html><center><h1>Selamat datang, %s</h1><p>Tipe akun: %s</p></center></html>",
                            a.getNama(), tipe));
        }
        showCard("dashboard");
    }

    private void onLogout() {
        this.currentUser = null;
        if (currentOrderModel != null)
            currentOrderModel.clear();
        // kembali ke auth-only view
        initComponents();
        revalidate();
        repaint();
        JOptionPane.showMessageDialog(this, "Anda telah logout.", "Logout", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateAuthState() {
        boolean loggedIn = (currentUser != null);
        if (btnLogout != null)
            btnLogout.setVisible(loggedIn);
        // determine role
        String role = getCurrentUserRole();
        // menu visible for everyone
        if (btnMenu != null)
            btnMenu.setVisible(true);
        // pelayan
        if (btnPelayan != null)
            btnPelayan.setVisible("pelayan".equalsIgnoreCase(role));
        if (btnTables != null)
            btnTables.setVisible("pelayan".equalsIgnoreCase(role));
        // koki
        if (btnKoki != null)
            btnKoki.setVisible("koki".equalsIgnoreCase(role));
        // kasir
        if (btnKasir != null)
            btnKasir.setVisible("kasir".equalsIgnoreCase(role));
        // refresh only staff
        boolean isStaff = (currentUser instanceof Pegawai);
        if (btnRefresh != null)
            btnRefresh.setVisible(isStaff);
        // auth button hidden when logged in
        if (btnAuth != null)
            btnAuth.setVisible(!loggedIn);
        // customer sees only menu/logout
        if ("customer".equalsIgnoreCase(role)) {
            if (btnPelayan != null)
                btnPelayan.setVisible(false);
            if (btnKoki != null)
                btnKoki.setVisible(false);
            if (btnKasir != null)
                btnKasir.setVisible(false);
            if (btnTables != null)
                btnTables.setVisible(false);
        }
    }

    private String getCurrentUserRole() {
        if (currentUser == null)
            return "";
        if (currentUser instanceof Pegawai) {
            try {
                Method m = currentUser.getClass().getMethod("getRole");
                Object r = m.invoke(currentUser);
                if (r != null)
                    return r.toString().toLowerCase();
            } catch (Exception ex) {
                try {
                    Method m2 = currentUser.getClass().getMethod("getJabatan");
                    Object r2 = m2.invoke(currentUser);
                    if (r2 != null)
                        return r2.toString().toLowerCase();
                } catch (Exception e) {
                    try {
                        Method m3 = currentUser.getClass().getMethod("getPeran");
                        Object r3 = m3.invoke(currentUser);
                        if (r3 != null)
                            return r3.toString().toLowerCase();
                    } catch (Exception ignored) {
                    }
                }
            }
            return "staff";
        } else {
            return "customer";
        }
    }

    private void showCard(String name) {
        if (cardPanel == null || cardLayout == null)
            return;
        cardLayout.show(cardPanel, name);
    }
}
