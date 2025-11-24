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

    // Standar status meja dan pesanan (gunakan konstanta agar konsisten di seluruh aplikasi)
    public static final String MEJA_KOSONG = "Kosong";
    public static final String MEJA_DITEMPATI = "Ditempati";

    public static final String STATUS_MENUNGGU = "Menunggu";
    public static final String STATUS_DIPROSES = "Diproses";
    public static final String STATUS_SELESAI = "Selesai";
    public static final String STATUS_DIBAYAR = "Dibayar";

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

        // Meja (standar: mulai sebagai "Kosong")
        for (int i = 1; i <= 10; i++) {
            daftarMeja.add(new Meja(i, MEJA_KOSONG));
        }

        // Tambahkan akun pegawai contoh (agar bisa login sebagai pelayan/koki/kasir tanpa mengubah file)
        // Format konstruktor: new Pegawai(id, nama, password, role)
        daftarPegawai.add(new Pegawai(1, "pelayan1", "pelayan123", "pelayan"));
        daftarPegawai.add(new Pegawai(2, "koki1", "koki123", "koki"));
        daftarPegawai.add(new Pegawai(3, "kasir1", "kasir123", "kasir"));
    }

    private void muatDataPegawaiDariFile() {
        File file = new File(PEGAWAI_FILE_PATH);
        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (line.isEmpty())
                    continue;
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    daftarPegawai.add(new Pegawai(Integer.parseInt(parts[0]), parts[1], parts[2], parts[3]));
                }
            }
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
        }
    }

    private void muatDataCustomerDariFile() {
        File file = new File(CUSTOMER_FILE_PATH);
        int maxId = 200;
        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (line.isEmpty())
                    continue;
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    int id = Integer.parseInt(parts[0]);
                    daftarCustomer.add(new Customer(id, parts[1], parts[2]));
                    if (id > maxId)
                        maxId = id;
                }
            }
            customerCounter = maxId + 1;
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
        }
    }

    private void simpanCustomerBaruKeFile(Customer customer) {
        try (FileWriter fw = new FileWriter(CUSTOMER_FILE_PATH, true);
                PrintWriter out = new PrintWriter(fw)) {
            out.printf("%d,%s,%s\n", customer.getId(), customer.getNama(), customer.getPassword());
        } catch (IOException e) {
            System.err.println("Error saat menyimpan customer baru ke file: " + e.getMessage());
        }
    }

    public Customer registerCustomer(String nama, String password) {
        int idBaru = customerCounter++;
        Customer customerBaru = new Customer(idBaru, nama, password);
        this.daftarCustomer.add(customerBaru);
        simpanCustomerBaruKeFile(customerBaru);
        return customerBaru;
    }

    public boolean cekNamaPengguna(String nama) {
        for (Pegawai p : daftarPegawai) {
            if (p.getNama().equalsIgnoreCase(nama))
                return true;
        }
        for (Customer c : daftarCustomer) {
            if (c.getNama().equalsIgnoreCase(nama))
                return true;
        }
        return false;
    }

    public Akun login(String nama, String password) {
        for (Pegawai p : daftarPegawai) {
            if (p.getNama().equals(nama) && p.getPassword().equals(password))
                return p;
        }
        for (Customer c : daftarCustomer) {
            if (c.getNama().equals(nama) && c.getPassword().equals(password))
                return c;
        }
        return null;
    }

    private void muatDataPesanan() {
        File filePesanan = new File(PESANAN_FILE_PATH);
        File fileDetail = new File(DETAIL_FILE_PATH);
        int maxId = 0;
        try (Scanner fileScanner = new Scanner(filePesanan)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (line.isEmpty())
                    continue;
                String[] parts = line.split("\\" + DELIMITER);
                if (parts.length == 3) {
                    int idPesanan = Integer.parseInt(parts[0]);
                    int idMeja = Integer.parseInt(parts[1]);
                    String status = parts[2];
                    Meja meja = findMejaByNomor(idMeja);
                    if (meja != null) {
                        Pesanan p = new Pesanan(idPesanan, meja);
                        p.setStatus(status);
                        meja.setStatus("Ditempati");
                        this.daftarPesanan.add(p);
                        if (idPesanan > maxId)
                            maxId = idPesanan;
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
                if (line.isEmpty())
                    continue;
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

    public void simpanSemuaDataPesanan() {
        try (PrintWriter outPesanan = new PrintWriter(new FileWriter(PESANAN_FILE_PATH, false));
                PrintWriter outDetail = new PrintWriter(new FileWriter(DETAIL_FILE_PATH, false))) {
            for (Pesanan p : daftarPesanan) {
                if (p.getStatus().equals("Dibayar")) {
                    continue;
                }
                outPesanan.println(p.getIdPesanan() + DELIMITER + p.getMeja().getNomor() + DELIMITER + p.getStatus());
                for (DetailPesanan d : p.getDaftarItem()) {
                    String catatanAman = d.getCatatan().replace(DELIMITER, " ");
                    outDetail.println(p.getIdPesanan() + DELIMITER + d.getItem().getId() + DELIMITER + d.getJumlah()
                            + DELIMITER + catatanAman);
                }
            }
        } catch (IOException e) {
            System.err.println("Error fatal saat menyimpan data pesanan: " + e.getMessage());
        }
    }

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
                    metode);
        } catch (IOException e) {
            System.err.println("Error saat menyimpan riwayat transaksi: " + e.getMessage());
        }
    }

    public void simpanStrukKeFile(String strukText) {
        try (FileWriter fw = new FileWriter(RIWAYAT_STRUK_FILE_PATH, true);
                PrintWriter out = new PrintWriter(fw)) {
            out.println(strukText);
            out.println("\n==============================================\n");
        } catch (IOException e) {
            System.err.println("Error saat menyimpan arsip struk: " + e.getMessage());
        }
    }

    public void lihatMenu() {
        System.out.println("\n------------------------------------ DAFTAR MENU ------------------------------------");
        System.out.printf("%-5s | %-10s | %-20s | %-12s | %-15s | %-7s | %-10s | %-10s\n",
                "ID", "Tipe", "Nama", "Harga", "Kategori", "Pedas", "Ukuran", "Suhu");
        System.out.println(
                "---------------------------------------------------------------------------------------------------------");

        for (MenuItem item : daftarMenu) {
            System.out.println(item.getInfo());
        }
        System.out.println(
                "---------------------------------------------------------------------------------------------------------");
    }

    public void tambahPesanan(Pesanan pesanan) {
        this.daftarPesanan.add(pesanan);
        simpanSemuaDataPesanan();
    }

    public void tambahTransaksi(Transaksi transaksi) {
        this.daftarTransaksi.add(transaksi);
        simpanRiwayatTransaksiKeFile(transaksi);
    }

    public Pesanan findPesananById(int id) {
        for (Pesanan p : daftarPesanan) {
            if (p.getIdPesanan() == id) {
                return p;
            }
        }
        return null;
    }

    public Meja findMejaByNomor(int nomor) {
        for (Meja m : daftarMeja) {
            if (m.getNomor() == nomor) {
                return m;
            }
        }
        return null;
    }

    public MenuItem findMenuItemByName(String nama) {
        for (MenuItem item : daftarMenu) {
            if (item.getNama().equalsIgnoreCase(nama)) {
                return item;
            }
        }
        return null;
    }

    public MenuItem findMenuItemById(int id) {
        for (MenuItem item : daftarMenu) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    public List<Pesanan> getDaftarPesanan() {
        return daftarPesanan;
    }

    public List<Pesanan> getPesananByStatus(String status) {
        return daftarPesanan.stream()
                .filter(p -> p.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    public List<Meja> getDaftarMeja() {
        return daftarMeja;
    }

    public int getNextPesananId() {
        return pesananCounter++;
    }

    public int getNextTransaksiId() {
        return transaksiCounter++;
    }

    // Getter tunggal untuk mengakses daftar menu (hindari duplikat getter meja)
    public List<MenuItem> getDaftarMenu() {
        return daftarMenu;
    }

    // Buat pesanan terpusat: set status Menunggu, tandai meja, simpan ke daftar & file
    public Pesanan buatPesanan(Meja meja, List<DetailPesanan> daftarDetail) {
        if (meja == null) return null;
        int id = getNextPesananId();
        Pesanan pes = new Pesanan(id, meja);
        pes.setStatus(STATUS_MENUNGGU);
        if (daftarDetail != null) {
            for (DetailPesanan d : daftarDetail) {
                pes.tambahDetailPesanan(d);
            }
        }
        meja.setStatus(MEJA_DITEMPATI);
        this.daftarPesanan.add(pes);
        simpanSemuaDataPesanan();
        return pes;
    }

    // Ubah status pesanan dan persist
    public boolean updateStatusPesanan(int idPesanan, String status) {
        Pesanan p = findPesananById(idPesanan);
        if (p == null) return false;
        p.setStatus(status);
        // jika pesanan selesai/dibayar, Anda mungkin ingin membuka meja
        if (STATUS_DIBAYAR.equals(status) || STATUS_SELESAI.equals(status)) {
            Meja m = p.getMeja();
            if (m != null && STATUS_DIBAYAR.equals(status)) {
                m.setStatus(MEJA_KOSONG);
            }
        }
        simpanSemuaDataPesanan();
        return true;
    }

    // Ambil pesanan yang statusnya termasuk salah satu dari statuses
    public List<Pesanan> getPesananByStatuses(String... statuses) {
        if (statuses == null || statuses.length == 0) return new ArrayList<>();
        java.util.List<String> s = java.util.Arrays.asList(statuses);
        return daftarPesanan.stream()
                .filter(p -> s.contains(p.getStatus()))
                .collect(Collectors.toList());
    }

    // Set status meja helper
    public boolean setMejaStatus(int nomor, String status) {
        Meja m = findMejaByNomor(nomor);
        if (m == null) return false;
        m.setStatus(status);
        return true;
    }
}