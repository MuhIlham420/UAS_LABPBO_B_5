public class CardPayment implements Pembayaran {

    @Override
    public void prosesBayar(double total) {
        System.out.println("Memproses pembayaran kartu (Card) sebesar Rp" + total);
    }
}