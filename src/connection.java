import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.*;

import static java.lang.String.*;

public class connection {
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, ClassNotFoundException, SQLException {


        String myURL = "http://www.cbr.ru/DailyInfoWebServ/DailyInfo.asmx";
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String request = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "  <soap:Body>\n" +
                "    <GetCursOnDate xmlns=\"http://web.cbr.ru/\">\n" +
                "      <On_date>" + date + "</On_date>\n" +
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
        connection.setRequestProperty("Content-Length", valueOf(request.length()));
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
        //System.out.println(respond);

        Connection sqlconnection = null;
        //URL к базе состоит из протокола:подпротокола://[хоста]:[порта_СУБД]/[БД] и других_сведений
        String sqlurl = "jdbc:postgresql://127.0.0.1:5432/postgres";
        //Имя пользователя БД
        String name = "postgres";
        //Пароль
        String password = "admin";
        Class.forName("org.postgresql.Driver");
        sqlconnection = DriverManager.getConnection(sqlurl, name, password);
        Statement statement = null;
        statement = sqlconnection.createStatement();
        /*ResultSet result1 = statement.executeQuery("SELECT id,fname FROM test ");
        while (result1.next()) {
            System.out.println("Номер в выборке #" + result1.getRow() + "\t Номер в базе #" + result1.getInt("id") + "\t" + result1.getString("fname"));*/
        try{
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = (Document) documentBuilder.parse(new InputSource(new StringReader(respond)));
            document.getDocumentElement().normalize();
            NodeList nodeList = document.getElementsByTagName("ValuteCursOnDate");
            System.out.println(nodeList.getLength());

           for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
               /* System.out.println();
                System.out.println("Текущий элемент: " + node.getNodeName());*/
                if (Node.ELEMENT_NODE == node.getNodeType()) {
                    Element element = (Element) node;
                    ResultSet result1 = statement.executeQuery("INSERT INTO public.\"ValuteCurse\" (date, Vname, Vnom, VCurse, Vcode, Vchcode)" +
                            "    VALUES ('" + date + "', '" + element.getElementsByTagName("Vname").item(0).getTextContent() +"" +
                            "','" + element.getElementsByTagName("Vname").item(0).getTextContent() +
                            "','" + element.getElementsByTagName("Vnom").item(0).getTextContent()+
                            "','" + element.getElementsByTagName("Vcode").item(0).getTextContent()+
                            "','" + element.getElementsByTagName("VchCode").item(0).getTextContent()+";");
                    /*System.out.println("Валюта: " + element
                            .getElementsByTagName("Vname").item(0)
                            .getTextContent());
                    System.out.println("Номинал: " + element
                            .getElementsByTagName("Vnom").item(0)
                            .getTextContent());
                    System.out.println("Курс: " + element
                            .getElementsByTagName("Vcurs").item(0)
                            .getTextContent());
                    System.out.println("Сокращение: " + element
                            .getElementsByTagName("VchCode").item(0)
                            .getTextContent());*/
                }
            }
        } catch (Exception e) {
            System.out.print("Problem parsing the file: "+e.getMessage());
        }
    }

}
