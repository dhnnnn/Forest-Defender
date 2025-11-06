import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Score here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Score extends Actor
{
    // --- Variabel utama ---
    private int score = 0;           // Menyimpan nilai skor pemain
    private GreenfootImage img;      // Objek gambar yang menampilkan teks skor

    /**
     * Konstruktor Score - dipanggil saat objek skor dibuat di dunia.
     * Langsung memanggil updateImage() untuk menampilkan skor awal (0).
     */
    public Score() {
        updateImage(); // Tampilkan teks awal
    }

    /**
     * Method addScore() menambah nilai skor sesuai dengan parameter "value".
     * Setelah skor berubah, teks di layar diperbarui.
     */
    public void addScore(int value) {
        score += value;     // Tambahkan nilai skor
        updateImage();      // Perbarui tampilan di layar
    }


    /**
     * Method updateImage() memperbarui tampilan teks skor di layar.
     * Menggunakan GreenfootImage untuk menggambar teks baru setiap kali skor berubah.
     */
    private void updateImage() {
        // Membuat gambar teks dengan tulisan "Score: <nilai>", ukuran font 24, warna putih, tanpa latar belakang.
        img = new GreenfootImage("Score: " + score, 24, Color.WHITE, new Color(0,0,0,0));
        
        // Menetapkan gambar ini sebagai tampilan objek Score di layar
        setImage(img);
    }

}

