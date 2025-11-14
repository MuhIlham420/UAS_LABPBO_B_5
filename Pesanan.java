import java.util.ArrayList;
import java.util.List;

public class Pesanan {
    private int idPesanan;
    private String status;
    private List<DetailPesanan> daftarItem;
    private Meja meja;

    public Pesanan(int idPesanan, Meja meja) {
        this.idPesanan = idPesanan;
        this.meja = meja;
        this.status = "Dipesan";
        this.daftarItem = new ArrayList<>();
    }

    // Getters dan Setters
    public int getIdPesanan() {
        return idPesanan;
    }

    public void setIdPesanan(int idPesanan) {
        this.idPesanan = idPesanan;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<DetailPesanan> getDaftarItem() {
        return daftarItem;
    }

    public Meja getMeja() {
        return meja;
    }

    public void setMeja(Meja meja) {
        this.meja = meja;
    }

    public void tambahDetailPesanan(DetailPesanan detail) {
        this.daftarItem.add(detail);
    }

    public double getTotalHarga() {
        double total = 0;
        for (DetailPesanan detail : daftarItem) {
            total += detail.getSubtotal();
        }
        return total;
    }
}