public class MainGui {
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(() -> {
			new RestaurantGUI(new RestaurantSystem());
		});
	}
}
