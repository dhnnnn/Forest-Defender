import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;

/**
 * Karakter dengan gerak kiri/kanan, animasi, lompat, dan serangan pedang.
 */
public class Karakter extends Actor {
    private int speed = 4;

    // Animasi kanan/kiri
    private GreenfootImage standRight;
    private GreenfootImage standLeft;
    private GreenfootImage[] walkRight;
    private GreenfootImage[] walkLeft;
    private GreenfootImage jumpRight;
    private GreenfootImage jumpLeft;
    
    // ⚔️ Animasi serangan pedang (3 frame untuk smooth animation)
    private GreenfootImage[] attackRight;
    private GreenfootImage[] attackLeft;

    // Animasi control
    private int animationCounter = 0;
    private int frame = 0;
    private int animationSpeed = 20; // semakin kecil -> animasi lebih cepat

    // Fisika lompat
    private int yVelocity = 0;
    private int gravity = 1;
    private boolean isJumping = false;

    // Arah hadap
    private boolean facingRight = true;
    
    // Offset kaki karakter dari bottom PNG (sesuaikan dengan sprite mu!)
    private int feetOffset = 25;

    // ⚔️ Sistem serangan
    private boolean isAttacking = false;
    private int attackDuration = 18; // berapa frame animasi attack berlangsung (6 frame per sprite)
    private int attackTimer = 0;
    private int attackFrame = 0; // frame attack saat ini
    private int attackAnimSpeed = 6; // kecepatan ganti frame attack
    private int attackRange = 60; // jarak serangan pedang (pixel)
    
    private String inputSequence = "";
    private boolean superMarioMode = false;
    private int originalSpeed;

    public Karakter() {
        originalSpeed = speed;
        
        // muat gambar
        standRight = new GreenfootImage("karakter-standRight.png");
        standLeft = new GreenfootImage(standRight);
        standLeft.mirrorHorizontally();

        walkRight = new GreenfootImage[2];
        walkRight[0] = new GreenfootImage("karakter-walk1Right.png");
        walkRight[1] = new GreenfootImage("karakter-walk2Right.png");

        // buat versi kiri dari tiap frame jalan
        walkLeft = new GreenfootImage[walkRight.length];
        for (int i = 0; i < walkRight.length; i++) {
            walkLeft[i] = new GreenfootImage(walkRight[i]);
            walkLeft[i].mirrorHorizontally();
        }

        // gambar lompat (hanya satu file), buat mirror untuk kiri
        jumpRight = new GreenfootImage("karakter-jump.png");
        jumpLeft = new GreenfootImage(jumpRight);
        jumpLeft.mirrorHorizontally();
        
        // ⚔️ gambar attack - 3 frame untuk animasi smooth
        // Pastikan file ini ada: karakter-attack1.png, karakter-attack2.png, karakter-attack3.png
        attackRight = new GreenfootImage[3];
        attackRight[0] = new GreenfootImage("karakter-attack1.png"); // awal swing
        attackRight[1] = new GreenfootImage("karakter-attack2.png"); // tengah swing
        attackRight[2] = new GreenfootImage("karakter-attack3.png"); // akhir swing
        
        // Buat versi kiri dari tiap frame attack
        attackLeft = new GreenfootImage[3];
        for (int i = 0; i < 3; i++) {
            attackLeft[i] = new GreenfootImage(attackRight[i]);
            attackLeft[i].mirrorHorizontally();
        }

        // set image awal
        setImage(standRight);
    }

    public void act() {
        handleInput();
        handleAttack(); // ⚔️ Proses serangan
        applyGravity();
        updateAnimation();
        checkEnemyCollisionAndPassing();
    }

    private void handleInput() {
        boolean moving = false;

        // ⚔️ Cegah gerakan saat sedang menyerang
        if (!isAttacking) {
            if (Greenfoot.isKeyDown("right")) {
                setLocation(getX() + speed, getY());
                facingRight = true;
                moving = true;
            } 
            if (Greenfoot.isKeyDown("left")) {
                setLocation(getX() - speed, getY());
                facingRight = false;
                moving = true;
            }

            // lompat — hanya jika menyentuh tanah (bukan sedang lompat)
            if (Greenfoot.isKeyDown("up") && !isJumping) {
                yVelocity = superMarioMode ? -20 : -15;
                isJumping = true;
                if (facingRight) setImage(jumpRight);
                else setImage(jumpLeft);
            }
        }
        
        // ⚔️ Tombol serangan - tekan 'X' atau 'Z' untuk menyerang
        if ((Greenfoot.isKeyDown("x") || Greenfoot.isKeyDown("z")) && !isAttacking) {
            isAttacking = true;
            attackTimer = attackDuration;
            // Greenfoot.playSound("sword-slash.wav"); // suara pedang (opsional)
        }

        // reset animasi berjalan ketika tidak bergerak
        if (!moving && !isJumping && !isAttacking) {
            if (facingRight) setImage(standRight);
            else setImage(standLeft);
            animationCounter = 0;
            frame = 0;
        }
    }
    
    // ⚔️ Handle serangan pedang
    private void handleAttack() {
        if (isAttacking) {
            attackTimer--;
            
            // Hitung frame attack yang sedang ditampilkan (0, 1, atau 2)
            attackFrame = (attackDuration - attackTimer) / attackAnimSpeed;
            if (attackFrame >= attackRight.length) {
                attackFrame = attackRight.length - 1; // stay di frame terakhir
            }
            
            // Tampilkan sprite attack sesuai frame
            if (facingRight) setImage(attackRight[attackFrame]);
            else setImage(attackLeft[attackFrame]);
            
            // Hit detection: cek di tengah animasi (frame 1) untuk timing yang pas
            if (attackFrame == 1) {
                List<Enemy> enemies = getObjectsInRange(attackRange, Enemy.class);
                
                for (Enemy enemy : enemies) {
                    // Cek apakah enemy ada di depan karakter (sesuai arah hadap)
                    if ((facingRight && enemy.getX() > getX()) || 
                        (!facingRight && enemy.getX() < getX())) {
                        // BUNUH ENEMY! 💀
                        getWorld().removeObject(enemy);
                        // Greenfoot.playSound("enemy-death.wav"); // suara musuh mati (opsional)
                        
                        // Tambah skor
                        Score score = (Score)getWorld().getObjects(Score.class).get(0);
                        if (score != null) score.addScore(50); // lebih banyak dari lewatin enemy
                    }
                }
            }
            
            // Selesai menyerang
            if (attackTimer <= 0) {
                isAttacking = false;
                attackFrame = 0;
            }
        }
    }

    private void applyGravity() {
        // terapkan gravitasi
        yVelocity += gravity;
        setLocation(getX(), getY() + yVelocity);
    
        // 🔧 FIX: Cek Ground dari posisi kaki karakter (bukan center PNG)
        int halfHeight = getImage().getHeight() / 2;
        int checkY = halfHeight - feetOffset; // cek dari kaki, bukan dari center
        
        Actor ground = getOneObjectAtOffset(0, checkY, Ground.class);
    
        // Jika menyentuh ground DAN sedang jatuh
        if (ground != null && yVelocity > 0) {
            // Hitung posisi top ground
            int groundHalfHeight = ground.getImage().getHeight() / 2;
            int groundTop = ground.getY() - groundHalfHeight;
            
            // Posisikan karakter: ground top - jarak kaki dari center
            int newY = groundTop - (halfHeight - feetOffset);
            
            setLocation(getX(), newY);
            yVelocity = 0;
            isJumping = false;
        } else if (ground == null && yVelocity == 0) {
            // Jika tidak ada ground di bawah, mulai jatuh
            isJumping = true;
        }
    }

    private void updateAnimation() {
        // ⚔️ Prioritas tertinggi: animasi attack
        if (isAttacking) {
            if (facingRight) setImage(attackRight[attackFrame]);
            else setImage(attackLeft[attackFrame]);
            return;
        }
        
        // Jika sedang lompat: tampilkan gambar lompat sesuai arah
        if (isJumping) {
            if (facingRight) setImage(jumpRight);
            else setImage(jumpLeft);
            return;
        }

        // Jika sedang bergerak (tombol kiri/kanan ditekan), lakukan animasi jalan
        if (Greenfoot.isKeyDown("right") || Greenfoot.isKeyDown("left")) {
            animationCounter++;
            if (animationCounter >= animationSpeed) {
                frame = (frame + 1) % walkRight.length;
                if (facingRight) setImage(walkRight[frame]);
                else setImage(walkLeft[frame]);
                animationCounter = 0;
            }
        }
    }
    
    private void checkEnemyCollisionAndPassing() {
        // Cek tabrakan langsung dengan musuh (HANYA kalau tidak sedang menyerang)
        if (!isAttacking) {
            Enemy collided = (Enemy)getOneIntersectingObject(Enemy.class);
            if (collided != null) {
                // Tampilkan Game Over screen
                GameOver gameOver = new GameOver();
                getWorld().addObject(gameOver, getWorld().getWidth()/2, getWorld().getHeight()/2);
                
                // Greenfoot.playSound("gameover.wav"); // Suara game over (opsional)
                Greenfoot.stop();                       // Hentikan game
                return;
            }
        }
    
        // Cek apakah Karakter sudah melewati musuh tanpa menabrak
        List enemies = getWorld().getObjects(Enemy.class);
        for (Object o : enemies) {
            Enemy e = (Enemy)o;
            // Jika belum dihitung dan posisi Karakter lebih besar dari musuh
            if (!e.isCounted() && getX() > e.getX()) {
                e.setCounted(true); // Tandai musuh sudah dilewati
                // Tambah skor
                Score score = (Score)getWorld().getObjects(Score.class).get(0);
                if (score != null) score.addScore(10);
                //Greenfoot.playSound("coin.wav"); // Suara koin
            }
        }
    }
}