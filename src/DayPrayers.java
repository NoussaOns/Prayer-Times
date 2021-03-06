import org.omg.CORBA.TIMEOUT;

import java.time.LocalDate;
import java.time.LocalTime;
import static java.lang.Math.*;

public class DayPrayers {

    public enum School{
        OMANI(new double[]{18, 18}),
        MUSLIM_WORLD_LEAGUE(new double[] {18,17}),
        ISNA(new double[] {17.5,15}),
        EGYPT(new double[] {19.5,17.5}),
        UMM_ALQURA(new double[] {18.5,90}),
        KARACHI(new double[] {18,18}),
        NORTH_AMERICA(new double[] {15,15})
        ;

        private final double fajr;
        private final double isha;

        private School(double[] schools) {
            this.fajr = schools[0];
            this.isha = schools[1];
        }
    }

    public enum AsrSchool{
        HANAFI(2),
        STANDARD(1)
        ;

        private final double asrShadow;

        private AsrSchool(double shadow) {
            this.asrShadow = shadow;
        }
    }

    private static final double JDN_FROM_1970 = 2440587.71;
    private static final int PRECAUTION_TIME = 5;

    private School school;
    private AsrSchool asrSchool;
    private LocalDate localDate;

    // solar equations
    private double longitude;
    private double latitude;
    private double jdn;
    private double jc;
    private double ml;
    private double ma;
    private double ee;
    private double c;
    private double stl;
    private double sta;
    private double srv;
    private double sal;
    private double mo;
    private double oc;
    private double sd;
    private double v;
    private double et;
    private double ucl;
    private double ha;
    private double solarNoon;
    private double sunRise;
    private double sunSet;

    /**
     * Constructs PrayerTimesPerDay object and calculates the necessary solar calculations
     * @param ucl the time zone
     * @param location the location's longitude & latitude as an array
     * @param date the date of the calculations
     * @param school the school on which Fajr & Isha prayers are based
     * @param asrSchool the school on which Asr prayer is based
     */
    public DayPrayers(int ucl, double[] location, LocalDate date, School school, AsrSchool asrSchool) {
        this.longitude = location[0];
        this.latitude = location[1];
        localDate = date;
        this.school = school;
        this.asrSchool = asrSchool;
        jdn = JDN_FROM_1970;
        this.ucl = ucl;
        compute();
    }

    /**
     * Return the Fajr hour with decimals
     * @return Fajr hour
     */
    private double getFajrHour() {
        double T = school.fajr;
        double ho = toDegrees(acos((-sin(toRadians(T))) / (cos(toRadians(latitude)) * cos(toRadians(sd))) - tan(toRadians(latitude)) * tan(toRadians(sd))));
        double fajrHour = solarNoon - ho / 15;
        return fajrHour;
    }

    /**
     * Calculates and returns Asr Time depending on the islamic school hanafi or else
     *
     * @return Asr Hour
     */
    private double getAsrHour() {
        double T = toDegrees(atan(1 / (asrSchool.asrShadow + tan(toRadians(latitude - sd)))));
        double ho = toDegrees(acos((sin(toRadians(T))) / (cos(toRadians(latitude)) * cos(toRadians(sd))) - tan(toRadians(latitude)) * tan(toRadians(sd))));
        double asrTime = solarNoon + ho / 15;
        return asrTime + PRECAUTION_TIME / 60.0;
    }

    /**
     * Computes the solar calculations
     */
    private void compute() {
        //solar calculations
        jdn += localDate.toEpochDay();
        jc = (jdn - 2451545) / 36525.0;
        ml = (280.46646 + jc * (36000.76983 + jc * 0.0003032)) % 360;
        ma = 357.52911 + jc * (35999.05029 - 0.0001537 * jc);
        ee = 0.016708634 - jc * (0.000042037 + 0.0000001267 * jc);
        c = sin(toRadians(ma)) * (1.914602 - jc * (0.004817 + 0.000014 * jc)) + sin(toRadians(2 * ma)) * (0.019993 - 0.000101 * jc) + sin(toRadians(3 * ma)) * 0.000289;
        stl = ml + c;
        sta = ma + c;
        srv = (1.000001018 * (1 - pow(ee, 2)) / (1 + ee * cos(toRadians(sta))));
        sal = stl - 0.00569 - 0.00478 * sin(toRadians(125.04 - 1934.136 * jc));
        mo = 23 + (26 + ((21.448 - jc * (46.815 + jc * (0.00059 - jc * 0.001813)))) / 60) / 60;
        oc = mo + 0.00256 * cos(toRadians(125.04 - 1934.136 * jc));
        sd = toDegrees(asin(sin(toRadians(oc)) * sin(toRadians(sal))));
        v = pow(tan(toRadians(oc / 2)), 2);
        et = 4 * toDegrees(v * sin(toRadians(2 * ml)) - 2 * ee * sin(toRadians(ma)) + 4 * ee * v * sin(toRadians(ma)) * cos(toRadians(2 * ml)) - 0.5 * v * v * sin(toRadians(4 * ml)) - 1.25 * ee * ee * sin(toRadians(2 * ma)));

        //Calculate sunrise time hour angle
        ha = toDegrees(acos(cos(toRadians(90.833))/(cos(toRadians(latitude))*cos(toRadians(sd)))-tan(toRadians(latitude))*tan(toRadians(sd))));
        solarNoon = (720 - 4 * longitude - et + ucl * 60) / 60.0;
        sunRise = solarNoon - ha / 15.0;
        sunSet = solarNoon + ha / 15.0;
    }

    /**
     * Calculates and returns Isha Time depending on the islamic school
     *
     * @return Isha Time
     */
    private double getIshaTime() {
        double T;
        if (school.toString().equals("UMM_ALQURA")) {
            return getMaghribHour() + school.isha / 60.0;
        } else {
            T = school.isha;
            double ho = toDegrees(acos((-sin(toRadians(T))) / (cos(toRadians(latitude)) * cos(toRadians(sd))) - tan(toRadians(latitude)) * tan(toRadians(sd))));
            double ishaTime = solarNoon + ho / 15;
            return ishaTime;
        }
    }

    /**
     * Returns dhuhur prayer time in hours with decimals
     * @return dhuhur prayer time in hours with decimals
     */
    private double getDhuhurHour() {
        return solarNoon + PRECAUTION_TIME /60.0;
    }

    /**
     * Calculates and returns Maghrib Time
     *
     * @return Maghrib Time
     */
    private double getMaghribHour() {
        return sunSet + PRECAUTION_TIME / 60.0;
    }

    /**
     * Return the midnight hour with decimal places
     * @return midnight hour
     */
    private double getMidNightHour() {
        return 12 + (getFajrHour() + getSus()) / 2.0;
    }


    /**
     * Computes the local time depending on the time passed as an argument
     * @param hourWithDecimals the time that you want to convert to LocalTime object
     * @return LocalTime object representing the time that is in decimals
     */
    private LocalTime getTime(double hourWithDecimals){
        int hour = (int) floor(hourWithDecimals);
        int minutes = (int) round((hourWithDecimals - hour) * 60);
        return LocalTime.of(hour,0).plusMinutes(minutes);
    }

    /**
     * Returns an array of one day prayer times with sunrise and midnight times
     * @return an array of one day prayer times with sunrise and midnight times
     */
    public LocalTime[] getDayPrayerTimes(){
        LocalTime[] prayerTimes = {getTime(getFajrHour()),getTime(sunRise),getTime(getDhuhurHour()),getTime(getAsrHour()),getTime(getMaghribHour()),getTime(getIshaTime()),getTime(getMidNightHour())};
        return prayerTimes;
    }

    public double getLongitude() { return longitude; }

    public double getLatitude() { return latitude; }

    public double getJdn() { return jdn; }

    public double getJc() {
        return jc;
    }

    public double getMl() {
        return ml;
    }

    public double getMa() {
        return ma;
    }

    public double getEe() {
        return ee;
    }

    public double getC() {
        return c;
    }

    public double getStl() {
        return stl;
    }

    public double getSta() {
        return sta;
    }

    public double getSrv() {
        return srv;
    }

    public double getSal() {
        return sal;
    }

    public double getMo() {
        return mo;
    }

    public double getOc() {
        return oc;
    }

    public double getSd() {
        return sd;
    }

    public double getV() {
        return v;
    }

    public double getEt() {
        return et;
    }

    public double getHa() {
        return ha;
    }

    public double getUcl() {
        return ucl;
    }

    public double getSon() {
        return solarNoon;
    }

    public double getSur() {
        return sunRise;
    }

    public double getSus() {
        return sunSet;
    }

    @Override
    public String toString() {
        return "SolarCalculations{" +
                "jdn=" + getJdn() +
                ", jc=" + getJc() +
                ", ml=" + getMl() +
                ", ma=" + getMa() +
                ", ee=" + getEe() +
                ", c=" + getC() +
                ", stl=" + getStl() +
                ", sta=" + getSta() +
                ", srv=" + getSrv() +
                ", sal=" + getSal() +
                ", mo=" + getMo() +
                ", oc=" + getOc() +
                ", sd=" + getSd() +
                ", v=" + getV() +
                ", et=" + getEt() +
                ", ha=" + getHa() +
                ", ucl=" + ucl +
                ", son=" + getSon() +
                ", sur=" + getSur() +
                ", sus=" + getSus() +
                '}';
    }


}
