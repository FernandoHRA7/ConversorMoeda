
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ConversorMoedas {
    public void CurrencyExchangeApp() throws IOException {
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner inputScanner = new Scanner(System.in);

        Gson gsonConfig = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .create();

        String apiUrl = "https://v6.exchangerate-api.com/v6/caec236f4b7c85927bd2503d/latest/USD";
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();
        HttpResponse<String> apiResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        JsonObject exchangeRates = gsonConfig.fromJson(apiResponse.body(), JsonObject.class);

        System.out.println("""
                Bem-vindo ao conversor de moedas!
                Siga as instruções abaixo para realizar a conversão.
                Para sair, digite "sair" a qualquer momento.
                """);

        List<Map<String, Object>> conversionHistory = new ArrayList<>();

        while (true) {
            try {
                System.out.println("""
                        Digite a sigla da moeda de origem (ex.: USD, EUR, BRL, ARS, GBP):
                        """);

                String sourceCurrency = inputScanner.nextLine();
                if (sourceCurrency.equalsIgnoreCase("sair")) {
                    System.out.println("Obrigado e até mais!");
                    break;
                }

                double sourceRate = exchangeRates.getAsJsonObject("conversion_rates").get(sourceCurrency).getAsDouble();

                System.out.println("""
                        Digite a sigla da moeda de destino (ex.: USD, EUR, BRL, ARS, GBP):
                        """);

                String targetCurrency = inputScanner.nextLine();
                if (targetCurrency.equalsIgnoreCase("sair")) {
                    System.out.println("Obrigado e até mais!");
                    break;
                }

                double targetRate = exchangeRates.getAsJsonObject("conversion_rates").get(targetCurrency).getAsDouble();

                System.out.println("Digite o valor a ser convertido:");
                double amount = inputScanner.nextDouble();
                performConversion(amount, sourceRate, targetRate, sourceCurrency, targetCurrency);

                LocalDateTime currentDateTime = LocalDateTime.now();
                String formattedDate = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                Map<String, Object> conversionRecord = new HashMap<>();
                conversionRecord.put("data", formattedDate);
                conversionRecord.put("moeda_origem", sourceCurrency);
                conversionRecord.put("valor_origem", amount);
                conversionRecord.put("moeda_destino", targetCurrency);
                conversionRecord.put("valor_destino", Math.round(amount * (targetRate / sourceRate) * 100.0) / 100.0);

                conversionHistory.add(conversionRecord);

            } catch (NullPointerException e) {
                System.out.println("""
                        Erro: entrada inválida.
                        Verifique os seguintes problemas:
                        - Moeda não reconhecida (use letras maiúsculas, ex.: USD).
                        - Valor inválido (use "," e "." corretamente).
                        Exemplo: 5,50 (correto), 5.50 (incorreto).
                        """);
            }
            inputScanner.nextLine();
        }
    }

    public static void performConversion(double amount, double sourceRate, double targetRate, String sourceCurrency, String targetCurrency) {
        System.out.println(amount + " " + sourceCurrency + " equivale a " + (amount * (targetRate / sourceRate)) + " " + targetCurrency);
    }
}