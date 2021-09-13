package bgu.spl.mics.application;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import java.io.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
    public static void main(String[] args) throws IOException {

        final String filepath = args[0];
        Gson gson = new Gson();
        Json inputJson = gson.fromJson(new FileReader(filepath), Json.class);

        Ewoks ewoks = Ewoks.getInstance();
        ewoks.setEwoks(inputJson.getEwoks());
        MessageBusImpl msgbus = MessageBusImpl.getInstance();

        Diary diary = Diary.getInstance();

        Thread leia = new Thread(new LeiaMicroservice(inputJson.getAttacks()));
        Thread hans = new Thread(new HanSoloMicroservice());
        Thread c3po = new Thread(new C3POMicroservice());
        Thread lando = new Thread(new LandoMicroservice(inputJson.getLando()));
        Thread r2d2 = new Thread(new R2D2Microservice(inputJson.getR2D2()));
        /* starting the microservices */
        hans.start();
        c3po.start();
        lando.start();
        r2d2.start();
        leia.start();
        /* waiting for all the microservices to finish before creating the output json */
        try {
            leia.join();
            hans.join();
            c3po.join();
            lando.join();
            r2d2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        /* output */
        Gson gsonOutput = new GsonBuilder().setPrettyPrinting().create();
        String jsonOutput = gsonOutput.toJson(diary);
        try (FileWriter file = new FileWriter(args[1])) {
            file.write(jsonOutput);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    class Json {
        /* ---------- the fields ----------- */
        Attack[] attacks;
        long R2D2;
        long Lando;
        int Ewoks;

        /* ----------- Constructor ----------- */
        public Json(Attack[] attacks, long r2d2, long lando, int ewoks) {
            this.attacks = attacks;
            this.R2D2 = r2d2;
            this.Lando = lando;
            this.Ewoks = ewoks;
        }

        public Attack[] getAttacks() {
            return attacks;
        }

        public long getR2D2() {
            return R2D2;
        }

        public long getLando() {
            return Lando;
        }

        public int getEwoks() {
            return Ewoks;
        }
    }
}
