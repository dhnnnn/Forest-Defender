import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Enemy here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Enemy extends Actor
{
        // --- Variabel utama musuh ---
    private int speed = 2;         // Kecepatan gerak musuh (positif = ke kanan, negatif = ke kiri)
    private boolean counted = false; // Menandakan apakah musuh sudah dilewati dan dihitung skor-nya

    /**
     * Konstruktor Enemy - dijalankan saat objek musuh dibuat.
     * Di sini gambar musuh ditetapkan.
     */
    public Enemy() {
        setImage("enemy.png");  // Menetapkan sprite (gambar) musuh
    }

    /**
     * Method act() dijalankan berulang kali oleh Greenfoot.
     * Mengatur pergerakan musuh dan pembalikan arah saat mencapai tepi dunia.
     */
    public void act()
    {
        move(speed); // Gerakkan musuh sesuai nilai speed (ke kanan atau ke kiri)


        // Jika musuh mencapai tepi dunia, balik arah dan cermin gambar agar menghadap arah berlawanan
        if (isAtEdge()) {
            speed = -speed; // Balik arah pergerakan
            getImage().mirrorHorizontally(); // Balik gambar agar terlihat berbalik arah
        }
    }

    /**
     * Method getter untuk memeriksa apakah musuh sudah dihitung dalam skor.
     * Digunakan di kelas Mario agar skor tidak bertambah dua kali untuk musuh yang sama.
     */
    public boolean isCounted() {
        return counted;
    }

    /**
     * Method setter untuk menandai bahwa musuh sudah dilewati dan dihitung skor-nya.
     */
    public void setCounted(boolean value) {
        counted = value;
    }

}

