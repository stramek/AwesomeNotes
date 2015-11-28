package pl.xdcodes.stramek.awesomenotes.parse;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(NoteParse.class);
        Parse.initialize(this, "y1lH6C1d4exKCixVIi2mLDP2aQJeX2PscKySEEB7",
                "LUWG18j8O3Wwvtk6BKDi1RsIGAyatkPOdrnmI9LB");
    }
}
