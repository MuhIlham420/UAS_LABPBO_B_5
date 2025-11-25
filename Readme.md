# 🍽️ Sistem Manajemen Restoran Five — Java  
Aplikasi manajemen restoran berbasis **Java OOP** dan **Swing GUI** dengan dukungan multi-role (Admin, Waiter, Koki, Kasir, Customer).  
Dikembangkan sebagai **Tugas Akhir Praktikum PBO**.

---

## 👥 Anggota Tim

Proyek ini dikembangkan sebagai tugas Ujian Akhir Semester (UAS) oleh *Kelompok 5*:

| Nama | NIM |
| :--- | :--- |
| M. Irfan Qadafi | 2408107010054 |
| M. Ilham | 2408107010043 |
| Muhammad Farhan Alafif | 2408107010075 |
| Putri Sakinatul Maulida | 2108107010062 |

---

## ✨ Fitur Utama

### 👤 1. Autentikasi Pengguna
Setiap pengguna harus login terlebih dahulu. Role menentukan fitur yang muncul:

| Role | Akses |
|------|--------|
| **Admin** | Akses penuh seluruh fitur |
| **Pelayan** | Menu, tambah pesanan |
| **Koki** | Melihat pesanan masuk, update status |
| **Kasir** | Pembayaran, struk, riwayat |
| **Customer** | Melihat menu |

---

### 📋 2. Menu Management
- Menampilkan daftar menu makanan & minuman  
- Menggunakan **JTable + DefaultTableModel**  
- Pelayan dapat menambah ke pesanan  
- Customer dapat melihat menu  

---

### 🧾 3. Order Management
- Pelayan membuat pesanan baru  
- Koki memantau pesanan yang masuk  
- Koki mengubah status pesanan menjadi *Selesai Dimasak*  
- Status otomatis diperbarui pada sistem  

---

### 💵 4. Payment Processing
Kasir dapat:
- Memilih metode pembayaran: **Cash / Card / QRIS**  
- Menghitung total pembayaran  
- Memproses transaksi  
- Mencetak struk ke file:
  - `riwayat_transaksi.txt`
  - `riwayat_struk.txt`

---

### 📚 5. Riwayat Transaksi
- Menampilkan seluruh transaksi dan struk  
- Ditampilkan dalam bentuk tabel  
- Tersedia fitur **Refresh Data**  

---

## 🖥️ Teknologi yang Digunakan

| Teknologi | Fungsi |
|----------|--------|
| **Java OOP** | Logika backend sistem restoran |
| **Java Swing** | Pembuatan GUI |
| **CardLayout** | Navigasi antar halaman |
| **JTable + DefaultTableModel** | Menampilkan data dinamis |
| **File I/O** | Menyimpan transaksi & struk |
| **ArrayList, HashMap** | Struktur data |

---

## 🚀 Cara Menjalankan Program

### 1. Clone repository
```bash
git clone https://github.com/MuhIlham420/UAS_LABPBO_B_5.git
```

### 2. Compile semua file
```bash
javac *.java
```
### 3. Compile semua file
```bash
java MainGui
```