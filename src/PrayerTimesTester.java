import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class PrayerTimesTester {
    public static void main(String [] args){
        // format dates to the given pattern of am and pm
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        LocalDate localDate = LocalDate.of(2017, 6, 25);
        PrayerTimesPerDay prayerTimesPerDay = new PrayerTimesPerDay(4, 57.1306229, 21.305724, localDate, "omani".trim(), "not hanafi");

        System.out.println("Stop here!");
        LocalTime[] dayPrayerTimes = prayerTimesPerDay.getDayPrayerTimes();


        for (int i = 0; i < dayPrayerTimes.length; i++){
            System.out.println(formatter.format(dayPrayerTimes[i]));
        }
    }
}
