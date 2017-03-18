/**
 * Created by Noelle on 3/14/2017.
 */

public class Singleton {
    private static Singleton instance;

    private Singleton(){}

    public static Singleton Instance()
    {
        if(instance == null)
        {
            instance = new Singleton();
        }
        return instance;
    }
}
