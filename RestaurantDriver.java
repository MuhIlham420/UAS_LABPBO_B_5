/**
 * Kelas utama yang menjalankan sistem restoran.
 * Menyediakan antarmuka pengguna untuk login, pembuatan akun, dan berbagai fungsi berdasarkan peran.
 * 
 * @author Kelompok 5 - UAS LAB PBO B
 * @version 1.0
 */
// RestaurantDriver.java
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class RestaurantDriver {
    private static RestaurantSystem system = new RestaurantSystem();
    private static Scanner scanner = new Scanner(System.in);
    private static Akun loggedInUser = null;

    /**
     * Mencetak teks dalam bentuk box dengan border.
     * 
     * @param text  Teks yang akan ditampilkan dalam box
     * @param width Lebar box yang diinginkan
     */
    private static void printBoxed(String text, int width) {
        String horizontalLine = "";
        for (int i = 0; i < width; i++) {
            horizontalLine += "=";
        }

        // Hitung padding
        int paddingSize = (width - text.length() - 2) / 2;
        String padding = "";
        for (int i = 0; i < paddingSize; i++) {
            padding += " ";
        }
        
        // Sesuaikan jika panjang teks ganjil/genap
        String extraPadding = (text.length() % 2 == 0) ? "" : " ";
        if ((width - text.length() - 2) % 2 != 0) {
            extraPadding = " ";
        }

        System.out.println(horizontalLine);
        System.out.println("|" + padding + text + padding + extraPadding + "|");
        System.out.println(horizontalLine);
    }

    /**
     * Method utama yang menjalankan aplikasi restoran.
     * 
     * @param args Argumen command line (tidak digunakan)
     */
    public static void main(String[] args) {
        printBoxed("Selamat Datang di Restoran Five", 60);

        while (true) {
            if (loggedInUser == null) {
                showMenuUtama(); 
            }
            
            if (loggedInUser != null) {
                if (loggedInUser instanceof Pegawai) {
                    Pegawai pegawai = (Pegawai) loggedInUser;
                    if (pegawai.getPeran().equals("Pelayan")) {
                        showMenuPelayan(pegawai);
                    } else if (pegawai.getPeran().equals("Koki")) {
                        showMenuKoki(pegawai);
                    } else if (pegawai.getPeran().equals("Kasir")) {
                        showMenuKasir(pegawai);
                    }
                } else if (loggedInUser instanceof Customer) {
                    showMenuCustomer((Customer) loggedInUser);
                }
                loggedInUser = null; 
            }
        }
    }
    
    /**
     * Menampilkan menu utama untuk pengguna yang belum login.
     */
    private static void showMenuUtama() {
        System.out.println("\n--- MENU UTAMA ---");
        System.out.println("1. Login");
        System.out.println("2. Buat Akun Customer Baru");
        System.out.println("3. Keluar");
        System.out.print("Pilihan: ");
        int pilihan = getIntInput();
        switch (pilihan) {
            case 1: handleLogin(); break;
            case 2: handleBuatAkunCustomer(); break;
            case 3:
                System.out.println("Terima kasih telah menggunakan sistem.");
                scanner.close(); 
                System.exit(0); 
                break;
            default: System.out.println("Pilihan tidak valid.");
        }
    }

    /**
     * Menangani proses login pengguna.
     */
    private static void handleLogin() {
        System.out.println("\n--- SILAKAN LOGIN ---");
        System.out.print("Nama Pengguna: ");
        String nama = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        loggedInUser = system.login(nama, password); 
        
        if (loggedInUser != null) {
            printBoxed("Login berhasil! Selamat datang, " + loggedInUser.getNama() + ".", 60);
        } else {
            System.out.println("Login gagal. Nama pengguna atau password salah.");
        }
    }
    
    /**
     * Menangani pembuatan akun customer baru.
     */
    private static void handleBuatAkunCustomer() {
        System.out.println("\n--- BUAT AKUN CUSTOMER ---");
        System.out.print("Masukkan Nama: ");
        String nama = scanner.nextLine();
        System.out.print("Masukkan Password: ");
        String password = scanner.nextLine();
        if (nama.isEmpty() || password.isEmpty()) {
             System.out.println("Nama dan Password tidak boleh kosong.");
             return;
        }
        if (system.cekNamaPengguna(nama)) {
            System.out.println("Nama pengguna '" + nama + "' sudah ada. Silakan gunakan nama lain.");
            return;
        }
        Customer customerBaru = system.registerCustomer(nama, password);
        System.out.println("Akun customer baru berhasil dibuat untuk: " + customerBaru.getNama());
        System.out.println("Silakan login untuk melanjutkan.");
    }

    /**
     * Menampilkan menu untuk peran Pelayan.
     * 
     * @param pelayan Objek Pegawai dengan peran Pelayan
     */
    private static void showMenuPelayan(Pegawai pelayan) {
        while (true) {
            System.out.println("\n--- Menu Pelayan: " + pelayan.getNama() + " ---");
            System.out.println("1. Buat Pesanan Baru");
            System.out.println("2. Lihat Status Semua Pesanan");
            System.out.println("3. Logout");                  
            System.out.print("Pilihan: ");
            int pilihan = getIntInput();
            switch (pilihan) {
                case 1:
                    buatPesananBaru();
                    break;
                case 2:
                    lihatSemuaPesanan();
                    break;
                case 3:
                    System.out.println("Logout...");
                    return; 
                default:
                    System.out.println("Pilihan tidak valid.");
            }
        }
    }
    
    /**
     * Menampilkan menu untuk peran Koki.
     * 
     * @param koki Objek Pegawai dengan peran Koki
     */
    private static void showMenuKoki(Pegawai koki) {
         while (true) {
            System.out.println("\n--- Menu Koki: " + koki.getNama() + " ---");
            System.out.println("1. Lihat Daftar Pesanan");
            System.out.println("2. Ubah Status Pesanan");
            System.out.println("3. Logout");
            System.out.print("Pilihan: ");

            int pilihan = getIntInput();
            switch (pilihan) {
                case 1:
                    tampilkanDaftarPesananKoki();
                    break;
                case 2:
                    tampilkanDaftarPesananKoki();
                    if (system.getPesananByStatus("Dipesan").isEmpty()) {
                        break; 
                    }
                    System.out.print("\nMasukkan ID Pesanan yang selesai (atau 'batal'): ");
                    String inputKoki = scanner.nextLine();
                    
                    if (inputKoki.equalsIgnoreCase("batal")) {
                        System.out.println("Aksi dibatalkan.");
                        break; 
                    }
                    try {
                        int idPesanan = Integer.parseInt(inputKoki);
                        Pesanan pesanan = system.findPesananById(idPesanan);
                        if (pesanan != null && pesanan.getStatus().equals("Dipesan")) {
                            koki.updateStatusPesanan(pesanan, "Selesai Dimasak");
                            system.simpanSemuaDataPesanan(); 
                        } else if (pesanan == null) {
                            System.out.println("Pesanan tidak ditemukan.");
                        } else {
                            System.out.println("Status pesanan ini tidak dapat diubah (Status: " + pesanan.getStatus() + ").");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Input tidak valid. Harap masukkan ID berupa angka atau 'batal'.");
                    }
                    break;
                case 3:
                    System.out.println("Logout...");
                    return; 
                default:
                    System.out.println("Pilihan tidak valid.");
            }
        }
    }
    
    /**
     * Menampilkan daftar pesanan yang perlu dimasak oleh Koki.
     */
    private static void tampilkanDaftarPesananKoki() {
        System.out.println("\n--- Pesanan Perlu Dimasak (Status: Dipesan) ---");
        List<Pesanan> pesananDipesan = system.getPesananByStatus("Dipesan");
        
        if (pesananDipesan.isEmpty()) {
            System.out.println("Tidak ada pesanan yang perlu dimasak.");
            return;
        }
        System.out.printf("%-5s | %-7s | %-20s | %-5s | %-15s\n", 
            "ID", "Meja", "Pesanan", "Jml", "Catatan");
        System.out.println("---------------------------------------------------------------");
        
        for (Pesanan p : pesananDipesan) {
            boolean isFirstItem = true; 
            for (DetailPesanan d : p.getDaftarItem()) {
                String catatan = (d.getCatatan() == null || d.getCatatan().isEmpty()) ? "-" : d.getCatatan();
                if (isFirstItem) {
                    System.out.printf("%-5d | %-7d | %-20s | %-5d | %-15s\n",
                            p.getIdPesanan(), p.getMeja().getNomor(), 
                            d.getItem().getNama(), d.getJumlah(), catatan);
                    isFirstItem = false;
                } else {
                    System.out.printf("%-5s | %-7s | %-20s | %-5d | %-15s\n",
                            "", "", d.getItem().getNama(), d.getJumlah(), catatan);
                }
            }
            System.out.println("---------------------------------------------------------------");
        }
    }

    /**
     * Menampilkan menu untuk peran Kasir.
     * 
     * @param kasir Objek Pegawai dengan peran Kasir
     */
     private static void showMenuKasir(Pegawai kasir) {
        while (true) {
            System.out.println("\n--- Menu Kasir: " + kasir.getNama() + " ---");
            System.out.println("1. Proses Pembayaran");
            System.out.println("2. Lihat Pesanan Siap Bayar");
            System.out.println("3. Logout");
            System.out.print("Pilihan: ");

            int pilihan = getIntInput();
            switch (pilihan) {
                case 1:
                    tampilkanDaftarSiapBayar();
                    if (system.getPesananByStatus("Selesai Dimasak").isEmpty()) {
                        break; 
                    }
                    prosesPembayaran(); 
                    break;
                case 2:
                    tampilkanDaftarSiapBayar();
                    break;
                case 3:
                    System.out.println("Logout...");
                    return; 
                default:
                    System.out.println("Pilihan tidak valid.");
            }
        }
    }
    
    /**
     * Menampilkan daftar pesanan yang siap untuk dibayar.
     */
    private static void tampilkanDaftarSiapBayar() {
        System.out.println("\n--- Pesanan Siap Bayar (Selesai Dimasak) ---");
        List<Pesanan> pesananSiapBayar = system.getPesananByStatus("Selesai Dimasak");
        if (pesananSiapBayar.isEmpty()) {
            System.out.println("Tidak ada pesanan yang siap dibayar.");
        } else {
            System.out.printf("%-5s | %-7s | %-12s\n", "ID", "Meja", "Total");
            System.out.println("------------------------------");
            for (Pesanan p : pesananSiapBayar) {
                 System.out.printf("%-5d | %-7d | Rp%-10.2f\n",
                        p.getIdPesanan(), p.getMeja().getNomor(), p.getTotalHarga());
            }
            System.out.println("------------------------------");
        }
    }

    /**
     * Menampilkan menu untuk Customer.
     * 
     * @param customer Objek Customer yang sedang login
     */
    private static void showMenuCustomer(Customer customer) {
        while (true) {
            System.out.println("\n--- Menu Customer: " + customer.getNama() + " ---");
            System.out.println("1. Lihat Menu Restoran");
            System.out.println("2. Buat Pesanan");
            System.out.println("3. Lihat Status Pesanan Saya");
            System.out.println("4. Logout");
            System.out.print("Pilihan: ");

            int pilihan = getIntInput();
            switch (pilihan) {
                case 1:
                    system.lihatMenu();
                    break;
                case 2:
                    buatPesananCustomer(customer);
                    break;
                case 3:
                    lihatPesananCustomer(customer);
                    break;
                case 4:
                    System.out.println("Logout...");
                    return;
                default:
                    System.out.println("Pilihan tidak valid.");
            }
        }
    }

    /**
     * Membuat pesanan baru untuk customer.
     * 
     * @param customer Customer yang membuat pesanan
     */
    private static void buatPesananCustomer(Customer customer) {
        System.out.println("\n--- Buat Pesanan (Customer) ---");

        System.out.println("Daftar Meja Tersedia:");
        boolean mejaAda = false;
        for (Meja m : system.getDaftarMeja()) {
            if (m.getStatus().equals("Tersedia")) {
                System.out.println("- Meja " + m.getNomor());
                mejaAda = true;
            }
        }

        if (!mejaAda) {
            System.out.println("Maaf, tidak ada meja tersedia.");
            return;
        }

        System.out.print("Pilih nomor meja: ");
        int noMeja = getIntInput();
        Meja mejaPilihan = system.findMejaByNomor(noMeja);

        if (mejaPilihan == null || !mejaPilihan.getStatus().equals("Tersedia")) {
            System.out.println("Meja tidak valid atau tidak tersedia.");
            return;
        }

        Pesanan pesanan = system.buatPesananCustomer(customer, noMeja);
        if (pesanan == null) {
            System.out.println("Gagal membuat pesanan.");
            return;
        }

        while (true) {
            system.lihatMenu();
            System.out.print("Masukkan ID Menu (atau 'selesai' untuk berhenti): ");
            String inputMenu = scanner.nextLine();
            if (inputMenu.equalsIgnoreCase("selesai")) break;
            try {
                int idMenu = Integer.parseInt(inputMenu);
                MenuItem item = system.findMenuItemById(idMenu);

                if (item == null) {
                    System.out.println("Menu tidak ditemukan.");
                    continue;
                }

                System.out.print("Jumlah: ");
                int jumlah = getIntInput();
                System.out.print("Catatan (opsional): ");
                String catatan = scanner.nextLine();

                pesanan.tambahDetailPesanan(new DetailPesanan(item, jumlah, catatan));
                System.out.println(item.getNama() + " ditambahkan.");

            } catch (NumberFormatException e) {
                System.out.println("Masukkan angka yang valid.");
            }
        }

        if (pesanan.getDaftarItem().isEmpty()) {
            System.out.println("Pesanan kosong. Dibatalkan.");
            system.getDaftarPesanan().remove(pesanan);
            pesanan.getMeja().setStatus("Tersedia");
            system.simpanSemuaDataPesanan();
            return;
        }

        system.simpanSemuaDataPesanan();
        System.out.println("Pesanan berhasil dibuat dengan ID: " + pesanan.getIdPesanan());
        System.out.println("Silakan tunggu pesanan selesai dimasak.");
    }

    /**
     * Menampilkan daftar pesanan milik customer.
     * 
     * @param customer Customer yang ingin melihat pesanannya
     */
    private static void lihatPesananCustomer(Customer customer) {
        System.out.println("\n--- Pesanan Anda ---");

        List<Pesanan> list = system.getPesananByCustomer(customer);

        if (list.isEmpty()) {
            System.out.println("Anda belum memiliki pesanan.");
            return;
        }

        for (Pesanan p : list) {
            System.out.println("\nID Pesanan: " + p.getIdPesanan());
            System.out.println("Meja: " + p.getMeja().getNomor());
            System.out.println("Status: " + p.getStatus());
            System.out.println("Detail:");
            for (DetailPesanan d : p.getDaftarItem()) {
                System.out.println("- " + d.getItem().getNama() + " x" + d.getJumlah()
                                   + " (Catatan: " + d.getCatatan() + ")");
            }
            System.out.printf("Total: Rp%.2f\n", p.getTotalHarga());
        }
    }

    /**
     * Membuat pesanan baru oleh pelayan.
     */
    private static void buatPesananBaru() {
        System.out.println("\n--- Membuat Pesanan Baru ---");
        System.out.println("Daftar Meja Tersedia:"); 
        boolean mejaTersedia = false;
        for (Meja m : system.getDaftarMeja()) {
            if (m.getStatus().equals("Tersedia")) {
                System.out.println("Meja No: " + m.getNomor()); 
                mejaTersedia = true;
            }
        }
        if (!mejaTersedia) {
            System.out.println("Maaf, semua meja sedang ditempati.");
            return;
        }
        System.out.print("Pilih Nomor Meja: "); 
        int noMeja = getIntInput();
        Meja mejaPilihan = system.findMejaByNomor(noMeja);
        if (mejaPilihan == null || !mejaPilihan.getStatus().equals("Tersedia")) {
            System.out.println("Meja tidak valid atau tidak tersedia.");
            return;
        }
        Pesanan pesananBaru = new Pesanan(system.getNextPesananId(), mejaPilihan);
        while (true) {
            system.lihatMenu(); 
            System.out.print("Masukkan ID Menu (atau 'selesai' untuk berhenti): ");
            String inputMenu = scanner.nextLine();
            if (inputMenu.equalsIgnoreCase("selesai")) {
                break;
            }
            try {
                int idMenu = Integer.parseInt(inputMenu);
                MenuItem item = system.findMenuItemById(idMenu);
                if (item == null) {
                    System.out.println("Menu dengan ID " + idMenu + " tidak ditemukan. Coba lagi.");
                    continue;
                }
                System.out.print("Jumlah: ");
                int jumlah = getIntInput();
                if (jumlah <= 0) {
                    System.out.println("Jumlah harus lebih dari 0.");
                    continue;
                }
                System.out.print("Catatan (opsional): ");
                String catatan = scanner.nextLine();
                DetailPesanan detail = new DetailPesanan(item, jumlah, catatan);
                pesananBaru.tambahDetailPesanan(detail);
                System.out.println(item.getNama() + " (x" + jumlah + ") ditambahkan.");
            } catch (NumberFormatException e) {
                System.out.println("Input tidak valid. Harap masukkan ID menu (angka) atau 'selesai'.");
            }
        }
        if (pesananBaru.getDaftarItem().isEmpty()) {
            System.out.println("Pesanan dibatalkan (tidak ada item).");
        } else {
            system.tambahPesanan(pesananBaru); 
            mejaPilihan.setStatus("Ditempati"); 
            System.out.println("Pesanan baru (ID: " + pesananBaru.getIdPesanan() + ") untuk Meja " + mejaPilihan.getNomor() + " berhasil dibuat.");
        }
    }
    
    /**
     * Menampilkan semua pesanan dalam sistem.
     */
    private static void lihatSemuaPesanan() {
        System.out.println("\n--- Status Semua Pesanan ---");
        List<Pesanan> daftarPesanan = system.getDaftarPesanan();
        if (daftarPesanan.isEmpty()) {
            System.out.println("Belum ada pesanan.");
            return;
        }
        System.out.printf("%-5s | %-7s | %-17s | %-20s | %-5s | %-15s | %-12s\n", 
            "ID", "Meja", "Status", "Pesanan", "Jml", "Catatan", "Total");
        System.out.println("------------------------------------------------------------------------------------------------");
        for (Pesanan p : daftarPesanan) {
            boolean isFirstItem = true; 
            if (p.getDaftarItem().isEmpty()) {
                System.out.printf("%-5d | %-7d | %-17s | %-20s | %-5s | %-15s | Rp%-10.2f\n",
                    p.getIdPesanan(), p.getMeja().getNomor(), p.getStatus(),
                    "(Tidak ada item)", 0, "-", p.getTotalHarga());
            } else {
                for (DetailPesanan d : p.getDaftarItem()) {
                    String catatan = (d.getCatatan() == null || d.getCatatan().isEmpty()) ? "-" : d.getCatatan();
                    if (isFirstItem) {
                        System.out.printf("%-5d | %-7d | %-17s | %-20s | %-5d | %-15s | Rp%-10.2f\n",
                                p.getIdPesanan(), p.getMeja().getNomor(), p.getStatus(),
                                d.getItem().getNama(), d.getJumlah(), catatan, p.getTotalHarga()); 
                        isFirstItem = false; 
                    } else {
                        System.out.printf("%-5s | %-7s | %-17s | %-20s | %-5d | %-15s | %-12s\n",
                                "", "", "", 
                                d.getItem().getNama(), d.getJumlah(), catatan, ""); 
                    }
                }
            }
            System.out.println("------------------------------------------------------------------------------------------------");
        }
    }
    
    /**
     * Memproses pembayaran untuk pesanan.
     */
    private static void prosesPembayaran() {
        System.out.println("\n--- Proses Pembayaran ---");
        System.out.print("Masukkan ID Pesanan yang akan dibayar (atau 'batal'): ");
        String inputBayar = scanner.nextLine();
        if (inputBayar.equalsIgnoreCase("batal")) {
            System.out.println("Aksi dibatalkan.");
            return; 
        }
        try {
            int idPesanan = Integer.parseInt(inputBayar);
            Pesanan pesanan = system.findPesananById(idPesanan);
            if (pesanan == null) {
                System.out.println("Pesanan tidak ditemukan.");
                return;
            }
            if (pesanan.getStatus().equals("Dibayar")) {
                System.out.println("Pesanan ini sudah lunas.");
                return;
            }
            if (!pesanan.getStatus().equals("Selesai Dimasak")) {
                System.out.println("Pesanan ini belum siap dibayar (Status: " + pesanan.getStatus() + "). Harap Koki selesaikan dulu.");
                return;
            }
            System.out.printf("Total tagihan untuk Pesanan %d (Meja %d) adalah: Rp%.2f\n",
                    pesanan.getIdPesanan(), pesanan.getMeja().getNomor(), pesanan.getTotalHarga());
            System.out.println("Pilih Metode Pembayaran:");
            System.out.println("1. Cash");
            System.out.println("2. Card");
            System.out.println("3. QRIS");
            System.out.print("Pilihan: ");
            int pilihanMetode = getIntInput();
            Pembayaran metodeBayar = null;
            switch (pilihanMetode) {
                case 1: metodeBayar = new CashPayment(); break;
                case 2: metodeBayar = new CardPayment(); break;
                case 3: metodeBayar = new QRISPayment(); break;
                default:
                    System.out.println("Metode tidak valid. Pembayaran dibatalkan.");
                    return;
            }
            Transaksi trx = new Transaksi(system.getNextTransaksiId(), pesanan, metodeBayar);
            trx.konfirmasi(); 
            system.tambahTransaksi(trx); 
            
            String strukText = Struk.cetak(trx); 
            system.simpanStrukKeFile(strukText); 
            
            pesanan.setStatus("Dibayar");
            pesanan.getMeja().setStatus("Tersedia"); 
            system.simpanSemuaDataPesanan(); 
            System.out.println("Pembayaran berhasil. Meja " + pesanan.getMeja().getNomor() + " kini tersedia.");
        } catch (NumberFormatException e) {
            System.out.println("Input tidak valid. Harap masukkan ID berupa angka atau 'batal'.");
        }
    }
    
    /**
     * Mendapatkan input integer dari pengguna dengan penanganan error.
     * 
     * @return Integer yang dimasukkan pengguna
     */
    private static int getIntInput() {
        while (true) {
            try {
                int input = scanner.nextInt();
                scanner.nextLine(); 
                return input;
            } catch (InputMismatchException e) {
                System.out.println("Input tidak valid. Harap masukkan angka.");
                scanner.nextLine(); 
                System.out.print("Coba lagi: ");
            }
        }
    }
}