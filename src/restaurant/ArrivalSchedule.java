package restaurant;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** Which customer arrives on which turn. Fixed, so every run can be reproduced. */
public class ArrivalSchedule {
    private final Map<Integer, Customer> arrivals;

    public ArrivalSchedule(Map<Integer, Customer> arrivals) {
        this.arrivals = new HashMap<>(arrivals);
    }

    public Optional<Customer> arrivingOn(int turn) {
        return Optional.ofNullable(arrivals.get(turn));
    }

    /** Stage 1: six Regular customers on turns 1, 4, 7, 10, 13 and 16. */
    public static ArrivalSchedule allRegular() {
        Map<Integer, Customer> map = new HashMap<>();
        int id = 1;
        for (int turn : GameConfig.ARRIVAL_TURNS) {
            map.put(turn, new RegularCustomer(id++));
        }
        return new ArrivalSchedule(map);
    }
}
