import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PrayerTimesTester {
    public static void main(String [] args){
        // format dates to the given pattern of am and pm
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        LocalDate localDate = LocalDate.of(2017, 6, 25);
        PrayerTimesPerDay solarCalculations = new PrayerTimesPerDay(4, 57.1306229, 21.305724, localDate, "omani".trim(), "not hanafi");

//        //System.out.println(l.minusDays(1).getDayOfYear());


        System.out.println("Sun Rise: " + formatter.format(solarCalculations.getSunRiseTime()));
        System.out.println("Solar Noon: " + formatter.format(solarCalculations.getSolarNoonTime()));
        System.out.println("Sun Set: " + formatter.format(solarCalculations.getSunSetTime()));
        System.out.println("Fajr: " + formatter.format(solarCalculations.getFajrTime()));
        System.out.println("Dhuhur: " + formatter.format(solarCalculations.getDhuhurTime()));
        System.out.println("Asr: " + formatter.format(solarCalculations.getAsrTime()));
        System.out.println("Maghrib: " + formatter.format(solarCalculations.getMaghribTime()));
        System.out.println("Isha: " + formatter.format(solarCalculations.getIshaTime()));
        System.out.println("Mid Night: " + formatter.format(solarCalculations.getMidNight()));
        System.out.println(solarCalculations);
    }
}
