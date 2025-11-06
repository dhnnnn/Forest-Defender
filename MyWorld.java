import greenfoot.*;
import java.util.*;

public class MyWorld extends World
{


    public MyWorld()
    {
        super(800, 400, 1);
        setBackground("background.png");
        prepare();
    }

    private void prepare()
    {
        // Tambahkan 20 tile tanah

        addObject(new Ground(), 400, 360); // posisi X bergeser tiap 50 pixel


        addObject(new Karakter(), 100, 285);
        addObject(new Enemy(), 500, 300);
        addObject(new Score(), 100, 30);
    }
}
