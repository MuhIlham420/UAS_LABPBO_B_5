import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner; 
import java.util.stream.Collectors; 
import java.time.format.DateTimeFormatter; 

/**
 * Kelas utama sistem restoran yang mengelola semua operasi.
 * Menangani menu, pesanan, transaksi, customer, pegawai, dan persistensi data.
 * 
 * @author Kelompok 5 - UAS LAB PBO B
 * @version 1.0
 */
public class RestaurantSystem {
    private List<MenuItem> daftarMenu;
    private List<Pegawai> daftarPegawai;
    private List<Customer> daftarCustomer; 
    private List<Pesanan> daftarPesanan;
    private List<Meja> daftarMeja;
    private List<Transaksi> daftarTransaksi; 
    
    private int pesananCounter = 1;
    private int transaksiCounter = 1;
    private int customerCounter = 201; 

    private static final String CUSTOMER_FILE_PATH = "customers.txt";
    private static final String PEGAWAI_FILE_PATH = "pegawai.txt";
    private static final String PESANAN_FILE_PATH = "pesanan.txt"; 
    private static final String DETAIL_FILE_PATH = "detail_pesanan.txt";
    private static final String RIWAYAT_FILE_PATH = "riwayat_transaksi.txt";
    private static final String RIWAYAT_STRUK_FILE_PATH = "riwayat_struk.txt";
    private static final String DELIMITER = "|"; 

    /**
     * Konstruktor untuk membuat objek RestaurantSystem.
     * Menginisialisasi data dan memuat data dari file.
     */
    public RestaurantSystem() {
        this.daftarMenu = new ArrayList<>();
        this.daftarPegawai = new ArrayList<>();
        this.daftarCustomer = new ArrayList<>(); 
        this.daftarPesanan = new ArrayList<>(); 
        this.daftarMeja = new ArrayList<>();
        this.daftarTransaksi = new ArrayList<>(); 
        
        inisialisasiData(); 
        
        muatDataPegawaiDariFile(); 
        muatDataCustomerDariFile();
        muatDataPesanan(); 
    }

    /**
     * Menginisialisasi data default untuk menu dan meja.
     */
    private void inisialisasiData() {
        // Kategori: Utama
        daftarMenu.add(new Makanan(1, "Nasi Goreng", 25000, 1, "Utama"));
        daftarMenu.add(new Makanan(2, "Mie Ayam", 20000, 0, "Utama"));
        daftarMenu.add(new Makanan(3, "Ayam Bakar", 35000, 1, "Utama"));
        // Kategori: Side Dish
        daftarMenu.add(new Makanan(4, "Kentang Goreng", 18000, 0, "Side Dish"));
        daftarMenu.add(new Makanan(5, "Roti Bawang", 15000, 0, "Side Dish"));
        // Kategori: Dessert
        daftarMenu.add(new Makanan(6, "Pisang Bakar", 22000, 0, "Dessert"));
        daftarMenu.add(new Makanan(7, "Ice Cream", 15000, 0, "Dessert"));
        // Kategori: Minuman
        daftarMenu.add(new Minuman(8, "Es Teh", 5000, "Medium", "Dingin", "Minuman Dingin"));
        daftarMenu.add(new Minuman(9, "Kopi Panas", 8000, "Small", "Panas", "Minuman Panas"));
        daftarMenu.add(new Minuman(10, "Jus Alpukat", 15000, "Large", "Dingin", "Minuman Dingin"));
        
        // Meja
        for (int i = 1; i <= 10; i++) {
            daftarMeja.add(new Meja(i, "Tersedia"));
        }
    }
    
    /**
     * Memuat data pegawai dari file.
     */
    private void muatDataPegawaiDariFile() {
        File file = new File(PEGAWAI_FILE_PATH);
        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (line.isEmpty()) continue; 
                String[] parts = line.split(","); 
                if (parts.length == 4) {
                    daftarPegawai.add(new Pegawai(Integer.parseInt(parts[0]), parts[1], parts[2], parts[3]));
                }
            }
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
        }
    }
    
    /**
     * Memuat data customer dari file.
     */
    private void muatDataCustomerDariFile() {
        File file = new File(CUSTOMER_FILE_PATH);
        int maxId = 200; 
        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (line.isEmpty()) continue; 
                String[] parts = line.split(","); 
                if (parts.length == 3) {
                    int id = Integer.parseInt(parts[0]);
                    daftarCustomer.add(new Customer(id, parts[1], parts[2]));
                    if (id > maxId) maxId = id; 
                }
            }
            customerCounter = maxId + 1; 
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
        }
    }
    
    /**
     * Menyimpan customer baru ke file.
     * 
     * @param customer Customer yang akan disimpan
     */
    private void simpanCustomerBaruKeFile(Customer customer) {
        try (FileWriter fw = new FileWriter(CUSTOMER_FILE_PATH, true);
             PrintWriter out = new PrintWriter(fw)) {
            out.printf("%d,%s,%s\n", customer.getId(), customer.getNama(), customer.getPassword());
        } catch (IOException e) {
            System.err.println("Error saat menyimpan customer baru ke file: " + e.getMessage());
        }
    }

    /**
     * Mendaftarkan customer baru ke sistem.
     * 
     * @param nama     Nama customer
     * @param password Password customer
     * @return Customer yang berhasil didaftarkan
     */
    public Customer registerCustomer(String nama, String password) {
        int idBaru = customerCounter++; 
        Customer customerBaru = new Customer(idBaru, nama, password);
        this.daftarCustomer.add(customerBaru); 
        simpanCustomerBaruKeFile(customerBaru);
        return customerBaru;
    }
    
    /**
     * Memeriksa apakah nama pengguna sudah terdaftar.
     * 
     * @param nama Nama yang akan dicek
     * @return true jika nama sudah terdaftar, false jika belum
     */
    public boolean cekNamaPengguna(String nama) {
        for (Pegawai p : daftarPegawai) {
            if (p.getNama().equalsIgnoreCase(nama)) return true; 
        }
        for (Customer c : daftarCustomer) {
            if (c.getNama().equalsIgnoreCase(nama)) return true; 
        }
        return false; 
    }

    /**
     * Melakukan login untuk pengguna (pegawai atau customer).
     * 
     * @param nama     Nama pengguna
     * @param password Password pengguna
     * @return Objek Akun jika login berhasil, null jika gagal
     */
    public Akun login(String nama, String password) {
        for (Pegawai p : daftarPegawai) {
            if (p.getNama().equals(nama) && p.getPassword().equals(password)) return p;
        }
        for (Customer c : daftarCustomer) {
            if (c.getNama().equals(nama) && c.getPassword().equals(password)) return c;
        }
        return null; 
    }
    
    /**
     * Memuat data pesanan dari file.
     */
    public void muatDataPesanan() {
        File filePesanan = new File(PESANAN_FILE_PATH);
        File fileDetail = new File(DETAIL_FILE_PATH);
        int maxId = 0;
        try (Scanner fileScanner = new Scanner(filePesanan)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\" + DELIMITER); 
                if (parts.length >= 3) {
                    try {
                        int idPesanan = Integer.parseInt(parts[0].trim());
                        int idMeja = Integer.parseInt(parts[1].trim());
                        int idCustomer = 0;
                        String status;
                        
                        if (parts.length >= 4) {
                            idCustomer = Integer.parseInt(parts[2].trim());
                            status = parts[3].trim();
                        } else {
                            status = parts[2].trim();
                        }
                        
                        Meja meja = findMejaByNomor(idMeja);
                        if (meja != null) {
                            Customer customer = (idCustomer > 0) ? findCustomerById(idCustomer) : null;
                            Pesanan p;
                            if (customer != null) {
                                p = new Pesanan(idPesanan, meja, customer);
                            } else {
                                p = new Pesanan(idPesanan, meja);
                            }
                            p.setStatus(status);
                            if (!"Dibayar".equals(status)) {
                                meja.setStatus("Ditempati"); 
                            }
                            this.daftarPesanan.add(p);
                            if (idPesanan > maxId) maxId = idPesanan; 
                        }
                    } catch (NumberFormatException nfe) {
                    }
                }
            }
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
        }
        pesananCounter = maxId + 1; 
        try (Scanner fileScanner = new Scanner(fileDetail)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\" + DELIMITER);
                if (parts.length >= 3) {
                    int idPesanan = Integer.parseInt(parts[0]);
                    int idMenuItem = Integer.parseInt(parts[1]);
                    int jumlah = Integer.parseInt(parts[2]);
                    String catatan = (parts.length > 3) ? parts[3] : ""; 
                    Pesanan pesanan = findPesananById(idPesanan);
                    MenuItem item = findMenuItemById(idMenuItem);
                    if (pesanan != null && item != null) {
                        DetailPesanan detail = new DetailPesanan(item, jumlah, catatan);
                        pesanan.tambahDetailPesanan(detail);
                    }
                }
            }
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
        }
    }

    /**
     * Menyimpan semua data pesanan ke file.
     */
    public void simpanSemuaDataPesanan() {
        try (PrintWriter outPesanan = new PrintWriter(new FileWriter(PESANAN_FILE_PATH, false));
             PrintWriter outDetail = new PrintWriter(new FileWriter(DETAIL_FILE_PATH, false))) {
            for (Pesanan p : daftarPesanan) {
                if (p.getStatus().equals("Dibayar")) {
                    continue; 
                }
                int idCustomer = (p.getCustomer() != null) ? p.getCustomer().getId() : 0;
                outPesanan.println(p.getIdPesanan() + DELIMITER + p.getMeja().getNomor() + DELIMITER + idCustomer + DELIMITER + p.getStatus());
                for (DetailPesanan d : p.getDaftarItem()) {
                    String catatanAman = d.getCatatan().replace(DELIMITER, " ");
                    outDetail.println(p.getIdPesanan() + DELIMITER + d.getItem().getId() + DELIMITER + d.getJumlah() + DELIMITER + catatanAman);
                }
            }
        } catch (IOException e) {
            System.err.println("Error fatal saat menyimpan data pesanan: " + e.getMessage());
        }
    }
    
    /**
     * Menyimpan riwayat transaksi ke file.
     * 
     * @param trx Transaksi yang akan disimpan
     */
    private void simpanRiwayatTransaksiKeFile(Transaksi trx) {
        try (FileWriter fw = new FileWriter(RIWAYAT_FILE_PATH, true);
             PrintWriter out = new PrintWriter(fw)) {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            String waktu = trx.getTimestamp().format(formatter);
            String metode = trx.getMetodePembayaran().getClass().getSimpleName().replace("Payment", "");
            out.printf("%s%s%d%s%d%s%d%s%.2f%s%s\n",
                    waktu, DELIMITER,
                    trx.getIdTransaksi(), DELIMITER,
                    trx.getPesanan().getIdPesanan(), DELIMITER,
                    trx.getPesanan().getMeja().getNomor(), DELIMITER,
                    trx.getPesanan().getTotalHarga(), DELIMITER,
                    metode
            );
        } catch (IOException e) {
            System.err.println("Error saat menyimpan riwayat transaksi: " + e.getMessage());
        }
    }

    /**
     * Menyimpan struk ke file arsip.
     * 
     * @param strukText Teks struk yang akan disimpan
     */
    public void simpanStrukKeFile(String strukText) {
        try (FileWriter fw = new FileWriter(RIWAYAT_STRUK_FILE_PATH, true);
             PrintWriter out = new PrintWriter(fw)) {
            out.println(strukText); 
            out.println("\n==============================================\n");
        } catch (IOException e) {
            System.err.println("Error saat menyimpan arsip struk: " + e.getMessage());
        }
    }

    /**
     * Menampilkan daftar menu yang tersedia.
     */
    public void lihatMenu() {
        System.out.println("\n------------------------------------ DAFTAR MENU ------------------------------------");
        System.out.printf("%-5s | %-10s | %-20s | %-12s | %-15s | %-7s | %-10s | %-10s\n", 
            "ID", "Tipe", "Nama", "Harga", "Kategori", "Pedas", "Ukuran", "Suhu");
        System.out.println("---------------------------------------------------------------------------------------------------------");
        
        for (MenuItem item : daftarMenu) {
            System.out.println(item.getInfo()); 
        }
        System.out.println("---------------------------------------------------------------------------------------------------------");
    }
    
    /**
     * Menambahkan pesanan baru ke sistem.
     * 
     * @param pesanan Pesanan yang akan ditambahkan
     */
    public void tambahPesanan(Pesanan pesanan) {
        this.daftarPesanan.add(pesanan);
        simpanSemuaDataPesanan(); 
    }

    /**
     * Menambahkan transaksi baru ke sistem.
     * 
     * @param transaksi Transaksi yang akan ditambahkan
     */
    public void tambahTransaksi(Transaksi transaksi) {
        this.daftarTransaksi.add(transaksi); 
        simpanRiwayatTransaksiKeFile(transaksi); 
    }
    
    /**
     * Mencari pesanan berdasarkan ID.
     * 
     * @param id ID pesanan yang dicari
     * @return Pesanan jika ditemukan, null jika tidak ditemukan
     */
    public Pesanan findPesananById(int id) {
        for (Pesanan p : daftarPesanan) {
            if (p.getIdPesanan() == id) {
                return p;
            }
        }
        return null;
    }
    
    /**
     * Mencari meja berdasarkan nomor.
     * 
     * @param nomor Nomor meja yang dicari
     * @return Meja jika ditemukan, null jika tidak ditemukan
     */
    public Meja findMejaByNomor(int nomor) {
        for (Meja m : daftarMeja) {
            if (m.getNomor() == nomor) {
                return m;
            }
        }
        return null;
    }

    /**
     * Mencari item menu berdasarkan nama.
     * 
     * @param nama Nama item menu yang dicari
     * @return MenuItem jika ditemukan, null jika tidak ditemukan
     */
    public MenuItem findMenuItemByName(String nama) {
        for (MenuItem item : daftarMenu) {
            if (item.getNama().equalsIgnoreCase(nama)) {
                return item;
            }
        }
        return null;
    }
    
    /**
     * Mencari item menu berdasarkan ID.
     * 
     * @param id ID item menu yang dicari
     * @return MenuItem jika ditemukan, null jika tidak ditemukan
     */
    public MenuItem findMenuItemById(int id) {
        for (MenuItem item : daftarMenu) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    /**
     * Mendapatkan daftar semua pesanan.
     * 
     * @return List berisi semua pesanan
     */
    public List<Pesanan> getDaftarPesanan() {
        return daftarPesanan;
    }
    
    /**
     * Mendapatkan daftar pesanan berdasarkan status.
     * 
     * @param status Status pesanan yang dicari
     * @return List berisi pesanan dengan status tertentu
     */
    public List<Pesanan> getPesananByStatus(String status) {
        return daftarPesanan.stream()
                            .filter(p -> p.getStatus().equals(status))
                            .collect(Collectors.toList());
    }
    
    /**
     * Mendapatkan daftar semua meja.
     * 
     * @return List berisi semua meja
     */
    public List<Meja> getDaftarMeja() {
        return daftarMeja;
    }

    /**
     * Mendapatkan ID pesanan berikutnya.
     * 
     * @return ID pesanan berikutnya
     */
    public int getNextPesananId() {
        return pesananCounter++; 
    }

    /**
     * Mendapatkan ID transaksi berikutnya.
     * 
     * @return ID transaksi berikutnya
     */
    public int getNextTransaksiId() {
        return transaksiCounter++; 
    }

    /**
     * Mencari customer berdasarkan ID.
     * 
     * @param id ID customer yang dicari
     * @return Customer jika ditemukan, null jika tidak ditemukan
     */
    public Customer findCustomerById(int id) {
        for (Customer c : daftarCustomer) {
            if (c.getId() == id) return c;
        }
        return null;
    }

    /**
     * Mendapatkan daftar pesanan milik customer tertentu.
     * 
     * @param c Customer pemilik pesanan
     * @return List berisi pesanan customer
     */
    public List<Pesanan> getPesananByCustomer(Customer c) {
        return daftarPesanan.stream()
                .filter(p -> p.getCustomer() != null && p.getCustomer().getId() == c.getId())
                .collect(Collectors.toList());
    }

    /**
     * Membuat pesanan baru untuk customer.
     * 
     * @param customer  Customer yang membuat pesanan
     * @param nomorMeja Nomor meja yang dipesan
     * @return Pesanan yang berhasil dibuat, null jika gagal
     */
    public Pesanan buatPesananCustomer(Customer customer, int nomorMeja) {
        Meja meja = findMejaByNomor(nomorMeja);
        if (meja == null || !meja.getStatus().equals("Tersedia")) {
            return null;
        }
        Pesanan p = new Pesanan(getNextPesananId(), meja, customer);
        daftarPesanan.add(p);
        meja.setStatus("Ditempati");
        simpanSemuaDataPesanan();
        return p;
    }

    /**
     * Memproses pembayaran pesanan customer.
     * 
     * @param customer  Customer yang membayar
     * @param idPesanan ID pesanan yang akan dibayar
     * @return true jika pembayaran berhasil, false jika gagal
     */
    public boolean bayarPesananCustomer(Customer customer, int idPesanan) {
        Pesanan p = findPesananById(idPesanan);
        if (p == null) return false;
        if (p.getCustomer() == null || p.getCustomer().getId() != customer.getId()) return false;
        if (p.getStatus().equals("Dibayar")) return false;
        p.setStatus("Dibayar");
        p.getMeja().setStatus("Tersedia");
        simpanSemuaDataPesanan();
        return true;
    }
}