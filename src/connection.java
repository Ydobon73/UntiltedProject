import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class connection {
    public static void main(String[] args) throws IOException {

        String myURL = "http://www.cbr.ru/DailyInfoWebServ/DailyInfo.asmx";
        String request = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "  <soap:Body>\n" +
                "    <GetCursOnDate xmlns=\"http://web.cbr.ru/\">\n" +
                "      <On_date>2017-09-27</On_date>\n" +
                "    </GetCursOnDate>\n" +
                "  </soap:Body>\n" +
                "</soap:Envelope>";

        URL url = null;


        try {
            url = new URL(myURL);
        } catch (MalformedURLException e1){
            e1.printStackTrace();
        }
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        connection.setRequestProperty("Content-Length", String.valueOf(request.length()));
        connection.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("SoapAction", "http://web.cbr.ru/GetCursOnDate");
        connection.setDoOutput(true);
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(connection.getOutputStream());
        } catch (IOException e2){
            e2.printStackTrace();
        }
        pw.write(request);
        pw.flush();
        try {
            connection.connect();
        } catch (IOException e1){
            e1.printStackTrace();
        }
        BufferedReader rd = null;
        try {
            rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } catch (IOException e1){
            e1.printStackTrace();
        }
        String line;
        String respond = "";
        try {
            respond = rd.readLine();
            while ((line = rd.readLine()) != null)
                respond = line;
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        System.out.println(respond);
    }

}
