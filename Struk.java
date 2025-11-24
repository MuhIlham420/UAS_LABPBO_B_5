import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Struk {
    public static String cetak(Transaksi transaksi) {
        StringBuilder sb = new StringBuilder();
        
        LocalDateTime now = transaksi.getTimestamp(); 
        if (now == null) {
            now = LocalDateTime.now(); 
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        
        sb.append("\n===== STRUK PEMBAYARAN =====");
        sb.append("\n         Restoran Five");
        sb.append("\n       Jl. Unsyiah No. 123");
        sb.append("\n==============================");
        sb.append("\nWaktu: ").append(now.format(formatter));
        sb.append("\nID Transaksi: ").append(transaksi.getIdTransaksi());
        
        Pesanan pesanan = transaksi.getPesanan();
        sb.append("\nMeja: ").append(pesanan.getMeja().getNomor());
        sb.append("\n------------------------------");
        sb.append("\nItem:");

        for (DetailPesanan detail : pesanan.getDaftarItem()) {
            sb.append(String.format("\n- %s (%d x Rp%.0f)",
                    detail.getItem().getNama(),
                    detail.getJumlah(),
                    detail.getItem().getHarga()));
            sb.append(String.format("\n  %-15s Rp%.0f", "", detail.getSubtotal())); 
            
            if (detail.getCatatan() != null && !detail.getCatatan().isEmpty()) {
                sb.append("\n  Catatan: ").append(detail.getCatatan());
            }
        }
        
        sb.append("\n------------------------------");
        sb.append(String.format("\nTotal Harga:     Rp%.2f", pesanan.getTotalHarga()));
        
        String metode = transaksi.getMetodePembayaran().getClass().getSimpleName()
                            .replace("Payment", "");
        sb.append("\nMetode Bayar: ").append(metode);
        
        sb.append("\n\n===== TERIMA KASIH =====");
        sb.append("\n    Selamat Menikmati!\n");

        String strukText = sb.toString();
        
        System.out.println(strukText);
        
        return strukText; 
    }
}