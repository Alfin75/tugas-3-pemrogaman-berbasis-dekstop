import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

abstract class MenuItem {
    private String nama;
    private double harga;
    private String kategori;

    public MenuItem(String nama, double harga, String kategori) {
        this.nama = nama;
        this.harga = harga;
        this.kategori = kategori;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public double getHarga() {
        return harga;
    }

    public void setHarga(double harga) {
        this.harga = harga;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public abstract void tampilMenu();
}

class Makanan extends MenuItem {
    private String jenisMakanan;

    public Makanan(String nama, double harga, String kategori, String jenisMakanan) {
        super(nama, harga, kategori);
        this.jenisMakanan = jenisMakanan;
    }

    public String getJenisMakanan() {
        return jenisMakanan;
    }

    @Override
    public void tampilMenu() {
        System.out.printf("[Makanan] %-18s | Kategori: %-10s | Jenis: %-10s | Rp%.0f%n",
                getNama(), getKategori(), jenisMakanan, getHarga());
    }
}

class Minuman extends MenuItem {
    private String jenisMinuman;

    public Minuman(String nama, double harga, String kategori, String jenisMinuman) {
        super(nama, harga, kategori);
        this.jenisMinuman = jenisMinuman;
    }

    public String getJenisMinuman() {
        return jenisMinuman;
    }

    @Override
    public void tampilMenu() {
        System.out.printf("[Minuman] %-18s | Kategori: %-10s | Jenis: %-10s | Rp%.0f%n",
                getNama(), getKategori(), jenisMinuman, getHarga());
    }
}

class Diskon extends MenuItem {
    private double diskon;

    public Diskon(String nama, double diskon) {
        super(nama, 0, "Promo");
        this.diskon = diskon;
    }

    public double getDiskon() {
        return diskon;
    }

    @Override
    public void tampilMenu() {
        System.out.printf("[Diskon]  %-18s | Kategori: %-10s | Potongan: %.0f%%%n",
                getNama(), getKategori(), (diskon * 100));
    }
}

class MenuTidakDitemukanException extends Exception {
    public MenuTidakDitemukanException(String pesan) {
        super(pesan);
    }
}

class Menu {
    private ArrayList<MenuItem> daftarMenu;

    public Menu() {
        this.daftarMenu = new ArrayList<>();
    }

    public void tambahItem(MenuItem item) {
        daftarMenu.add(item);
    }

    public void tampilkanSemuaMenu() {
        if (daftarMenu.isEmpty()) {
            System.out.println("Daftar menu masih kosong.");
            return;
        }
        for (int i = 0; i < daftarMenu.size(); i++) {
            System.out.print((i + 1) + ". ");
            daftarMenu.get(i).tampilMenu();
        }
    }

    public MenuItem ambilItem(int indeks) throws MenuTidakDitemukanException {
        if (indeks < 0 || indeks >= daftarMenu.size()) {
            throw new MenuTidakDitemukanException("Peringatan: Nomor menu yang dipilih tidak tersedia dalam daftar.");
        }
        return daftarMenu.get(indeks);
    }

    public void simpanMenuKeFile(String namaFile) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(namaFile))) {
            for (MenuItem item : daftarMenu) {
                if (item instanceof Makanan) {
                    Makanan m = (Makanan) item;
                    writer.println("MAKANAN;" + m.getNama() + ";" + m.getHarga() + ";" + m.getKategori() + ";" + m.getJenisMakanan());
                } else if (item instanceof Minuman) {
                    Minuman m = (Minuman) item;
                    writer.println("MINUMAN;" + m.getNama() + ";" + m.getHarga() + ";" + m.getKategori() + ";" + m.getJenisMinuman());
                } else if (item instanceof Diskon) {
                    Diskon d = (Diskon) item;
                    writer.println("DISKON;" + d.getNama() + ";" + d.getDiskon());
                }
            }
        } catch (IOException e) {
            System.out.println("Terjadi kesalahan saat menyimpan file menu: " + e.getMessage());
        }
    }

    public void muatMenuDariFile(String namaFile) {
        File file = new File(namaFile);
        if (!file.exists()) {
            return;
        }
        daftarMenu.clear();
        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String baris = fileScanner.nextLine();
                String[] data = baris.split(";");
                if (data[0].equals("MAKANAN")) {
                    tambahItem(new Makanan(data[1], Double.parseDouble(data[2]), data[3], data[4]));
                } else if (data[0].equals("MINUMAN")) {
                    tambahItem(new Minuman(data[1], Double.parseDouble(data[2]), data[3], data[4]));
                } else if (data[0].equals("DISKON")) {
                    tambahItem(new Diskon(data[1], Double.parseDouble(data[2])));
                }
            }
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan saat memuat file menu: " + e.getMessage());
        }
    }
}

class Pesanan {
    private ArrayList<MenuItem> daftarPesanan;

    public Pesanan() {
        this.daftarPesanan = new ArrayList<>();
    }

    public void tambahPesanan(MenuItem item) {
        daftarPesanan.add(item);
    }

    public void tambahPesanan(MenuItem item, int jumlah) {
        for (int i = 0; i < jumlah; i++) {
            daftarPesanan.add(item);
        }
    }

    public boolean isKosong() {
        return daftarPesanan.isEmpty();
    }

    public double hitungTotalBiaya() {
        double subtotal = 0;
        double totalPersenDiskon = 0;

        for (MenuItem item : daftarPesanan) {
            if (item instanceof Diskon) {
                totalPersenDiskon += ((Diskon) item).getDiskon();
            } else {
                subtotal += item.getHarga();
            }
        }

        if (totalPersenDiskon > 1.0) {
            totalPersenDiskon = 1.0;
        }

        double potongan = subtotal * totalPersenDiskon;
        return subtotal - potongan;
    }

    public String buatTeksStruk() {
        StringBuilder sb = new StringBuilder();
        sb.append("=========================================\n");
        sb.append("         STRUK PEMBAYARAN RESTORAN       \n");
        sb.append("=========================================\n");

        double subtotal = 0;
        for (MenuItem item : daftarPesanan) {
            if (!(item instanceof Diskon)) {
                sb.append(String.format("- %-22s : Rp%.0f%n", item.getNama(), item.getHarga()));
                subtotal += item.getHarga();
            }
        }

        sb.append("-----------------------------------------\n");
        sb.append(String.format("Subtotal                 : Rp%.0f%n", subtotal));

        for (MenuItem item : daftarPesanan) {
            if (item instanceof Diskon) {
                double persen = ((Diskon) item).getDiskon() * 100;
                sb.append(String.format("Promo (%-15s)  : -%.0f%%%n", item.getNama(), persen));
            }
        }

        sb.append("-----------------------------------------\n");
        sb.append(String.format("TOTAL BAYAR              : Rp%.0f%n", hitungTotalBiaya()));
        sb.append("=========================================\n");
        return sb.toString();
    }

    public void tampilkanStruk() {
        System.out.println("\n" + buatTeksStruk());
    }

    public void simpanStrukKeFile(String namaFile) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(namaFile))) {
            writer.print(buatTeksStruk());
            System.out.println("Struk pesanan berhasil disimpan ke file '" + namaFile + "'.");
        } catch (IOException e) {
            System.out.println("Gagal menyimpan struk: " + e.getMessage());
        }
    }

    public static void muatStrukDariFile(String namaFile) {
        File file = new File(namaFile);
        if (!file.exists()) {
            System.out.println("File struk belum tersedia.");
            return;
        }
        try (Scanner fileScanner = new Scanner(file)) {
            System.out.println("\n--- RIWAYAT STRUK TERSIMPAN ---");
            while (fileScanner.hasNextLine()) {
                System.out.println(fileScanner.nextLine());
            }
        } catch (IOException e) {
            System.out.println("Gagal membaca file struk: " + e.getMessage());
        }
    }
}

public class AplikasiRestoran {
    private static final String FILE_MENU = "daftar_menu.txt";
    private static final String FILE_STRUK = "struk_pesanan.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Menu menuRestoran = new Menu();
        Pesanan pesananSaatIni = new Pesanan();

        menuRestoran.muatMenuDariFile(FILE_MENU);

        boolean berjalan = true;
        while (berjalan) {
            System.out.println("\n=== SISTEM MANAJEMEN RESTORAN ===");
            System.out.println("1. Tambah Item Baru ke Menu");
            System.out.println("2. Tampilkan Daftar Menu Restoran");
            System.out.println("3. Buat Pesanan Pelanggan");
            System.out.println("4. Hitung Total & Cetak Struk Pesanan");
            System.out.println("5. Muat Struk Terakhir dari File");
            System.out.println("6. Keluar dari Program");
            System.out.print("Pilih menu (1-6): ");

            try {
                int pilihan = scanner.nextInt();
                scanner.nextLine();

                switch (pilihan) {
                    case 1:
                        System.out.println("\n--- Tambah Item Menu ---");
                        System.out.println("1. Makanan");
                        System.out.println("2. Minuman");
                        System.out.println("3. Diskon");
                        System.out.print("Pilih jenis item (1-3): ");
                        int jenis = scanner.nextInt();
                        scanner.nextLine();

                        if (jenis == 1) {
                            System.out.print("Nama Makanan: ");
                            String nama = scanner.nextLine();
                            System.out.print("Harga: ");
                            double harga = scanner.nextDouble();
                            scanner.nextLine();
                            System.out.print("Kategori (contoh: Utama/Penutup): ");
                            String kategori = scanner.nextLine();
                            System.out.print("Jenis Makanan (contoh: Goreng/Kuah): ");
                            String jenisMakanan = scanner.nextLine();

                            menuRestoran.tambahItem(new Makanan(nama, harga, kategori, jenisMakanan));
                            menuRestoran.simpanMenuKeFile(FILE_MENU);
                            System.out.println("Item makanan berhasil ditambahkan.");

                        } else if (jenis == 2) {
                            System.out.print("Nama Minuman: ");
                            String nama = scanner.nextLine();
                            System.out.print("Harga: ");
                            double harga = scanner.nextDouble();
                            scanner.nextLine();
                            System.out.print("Kategori (contoh: Dingin/Panas): ");
                            String kategori = scanner.nextLine();
                            System.out.print("Jenis Minuman (contoh: Jus/Kopi/Teh): ");
                            String jenisMinuman = scanner.nextLine();

                            menuRestoran.tambahItem(new Minuman(nama, harga, kategori, jenisMinuman));
                            menuRestoran.simpanMenuKeFile(FILE_MENU);
                            System.out.println("Item minuman berhasil ditambahkan.");

                        } else if (jenis == 3) {
                            System.out.print("Nama Promo Diskon: ");
                            String nama = scanner.nextLine();
                            System.out.print("Besar Diskon dalam persen (contoh: 10 untuk 10%): ");
                            double persen = scanner.nextDouble();
                            scanner.nextLine();

                            menuRestoran.tambahItem(new Diskon(nama, persen / 100.0));
                            menuRestoran.simpanMenuKeFile(FILE_MENU);
                            System.out.println("Item diskon berhasil ditambahkan.");

                        } else {
                            System.out.println("Jenis item tidak valid.");
                        }
                        break;

                    case 2:
                        System.out.println("\n--- DAFTAR MENU RESTORAN ---");
                        menuRestoran.tampilkanSemuaMenu();
                        break;

                    case 3:
                        System.out.println("\n--- PEMESANAN MENU ---");
                        menuRestoran.tampilkanSemuaMenu();
                        System.out.print("Masukkan nomor menu yang dipesan: ");
                        int nomorMenu = scanner.nextInt();
                        scanner.nextLine();

                        MenuItem itemDipilih = menuRestoran.ambilItem(nomorMenu - 1);

                        if (itemDipilih instanceof Diskon) {
                            pesananSaatIni.tambahPesanan(itemDipilih);
                            System.out.println("Promo '" + itemDipilih.getNama() + "' berhasil diterapkan.");
                        } else {
                            System.out.print("Masukkan jumlah porsi: ");
                            int jumlah = scanner.nextInt();
                            scanner.nextLine();
                            if (jumlah > 0) {
                                pesananSaatIni.tambahPesanan(itemDipilih, jumlah);
                                System.out.println(jumlah + "x " + itemDipilih.getNama() + " ditambahkan ke pesanan.");
                            } else {
                                System.out.println("Jumlah porsi minimal 1.");
                            }
                        }
                        break;

                    case 4:
                        if (pesananSaatIni.isKosong()) {
                            System.out.println("Belum ada pesanan yang dicatat.");
                        } else {
                            pesananSaatIni.tampilkanStruk();
                            pesananSaatIni.simpanStrukKeFile(FILE_STRUK);
                            pesananSaatIni = new Pesanan();
                        }
                        break;

                    case 5:
                        Pesanan.muatStrukDariFile(FILE_STRUK);
                        break;

                    case 6:
                        menuRestoran.simpanMenuKeFile(FILE_MENU);
                        System.out.println("Data telah disimpan. Terima kasih telah menggunakan program ini.");
                        berjalan = false;
                        break;

                    default:
                        System.out.println("Pilihan tidak tersedia. Silakan masukkan angka 1 sampai 6.");
                }
            } catch (MenuTidakDitemukanException e) {
                System.out.println(e.getMessage());
            } catch (InputMismatchException e) {
                System.out.println("Kesalahan input: Harap masukkan angka yang sesuai.");
                scanner.nextLine();
            }
        }
        scanner.close();
    }
}