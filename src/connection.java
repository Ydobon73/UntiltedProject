import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class connection {
    public static void main(String[] args) {

        String myURL = "http://www.cbr.ru/DailyInfoWebServ/DailyInfo.asmx";
        String request = "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">\n" +
                "  <soap12:Body>\n" +
                "    <Coins_base xmlns=\"http://web.cbr.ru/\">\n" +
                "      <fromDate>2017-09-25</fromDate>\n" +
                "      <ToDate>2017-09-27</ToDate>\n" +
                "    </Coins_base>\n" +
                "  </soap12:Body>\n" +
                "</soap12:Envelope>";

        URL url = null;


        url = new URL(myURL);
        HttpURLConnection connection = null;
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Length", String.valueOf(request.length()));
        connection.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("SoapAction", "http://web.cbr.ru/GetCursOnDate");
        connection.setDoOutput(true);
        PrintWriter pw = null;

        pw = new PrintWriter(connection.getOutputStream());
        pw.write(request);
        pw.flush();
        connection.connect();
        BufferedReader rd = null;
        rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        String respond = "";
            respond = rd.readLine();
            while ((line = rd.readLine()) != null)
                respond = line;
        System.out.println(respond);

    }
}