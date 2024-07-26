package no.nav.levendearbeidsforholdscheduler.utils;

public class Utils {

    /**
     * Formatterer en cron-expression for å kjøre en jobb hver x. time
     * @param intervallet Heltall som representerer antall timer forsinkelse
     * @return Ferdig formattert og gyldig cron-expression
     */
    public static String lagCronExpression(String intervallet) {
        return "0 0 */" + intervallet + " ? * MON-SAT";
    }


    /**
     * Funksjon som validerer om intervall er et positivt heltall. Brukes kun i this.rescheduleTask() metoden.
     * @param intervall Heltall som representerer antall timer forsinkelse for job-scheduleren
     * @return true hvis intervallet er et positivt heltall og false hvis ikke
     */
    public static boolean intervallErHeltall(String intervall) {
        try {
            Integer.parseInt(intervall);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
