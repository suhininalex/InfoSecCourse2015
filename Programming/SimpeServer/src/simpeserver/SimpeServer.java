package simpeserver;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

class SampleServer extends Thread
{
    Socket s;
    int num;

    public static void main(String args[])
    {
        try
        {
            int i = 0; // счётчик подключений

            ServerSocket server = new ServerSocket(1879, 0,
                    InetAddress.getByName("192.168.1.25"));

            System.out.println("server is started");

            // слушаем порт
            while(true)
            {
                // ждём нового подключения, после чего запускаем обработку клиента
                // в новый вычислительный поток и увеличиваем счётчик на единичку
                new SampleServer(i, server.accept());
                i++;
            }
        }
        catch(Exception e)
        {System.out.println("init error: "+e);} // вывод исключений
    }

    public SampleServer(int num, Socket s)
    {
        // копируем данные
        this.num = num;
        this.s = s;

        // и запускаем новый вычислительный поток (см. ф-ю run())
        setDaemon(true);
        setPriority(NORM_PRIORITY);
        start();
    }

    public void run()
    {
        try
        {
            // из сокета клиента берём поток входящих данных
            InputStream is = s.getInputStream();
            // и оттуда же - поток данных от сервера к клиенту
            OutputStream os = s.getOutputStream();

            // буффер данных в 64 килобайта
            byte buf[] = new byte[64*1024];
            // читаем 64кб от клиента, результат - кол-во реально принятых данных
            int r = is.read(buf);
            
            String in = new String(buf);
            
            System.out.println(in);
            
            String result = "Sey hello!";
            if (in.contains("Hi, you!")) {
                result = "Hi!\r\nI'm Simple Server version 1.0";
            }
            
            
            // выводим данные:
            os.write(result.getBytes());

            // завершаем соединение
            s.close();
        }
        catch(Exception e)
        {System.out.println("init error: "+e);} // вывод исключений
    }
}