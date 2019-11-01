package myproject.mylaundry.module;

import java.util.List;

/**
 * Created by Mai Thanh Hiep on 4/3/2016.
 */
public interface DirectionKobalListener {
    void onDirectionKobalStart();
    void onDirectionKobalSuccess(List<Route> route);

}
