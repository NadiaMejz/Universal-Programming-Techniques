package zad1;

import java.util.HashMap;
import java.util.Map;

public class KrajWalut {
    static HashMap<String, String> currencyMap = new HashMap<>();

    static {
        currencyMap.put("Poland", "PLN");
        currencyMap.put("United Arab Emirates", "AED");
        currencyMap.put("Afghanistan", "AFN");
        currencyMap.put("Albania", "ALL");
        currencyMap.put("Armenia", "AMD");
        currencyMap.put("Netherlands Antilles (former)", "ANG");
        currencyMap.put("Angola", "AOA");
        currencyMap.put("Argentina", "ARS");
        currencyMap.put("Australia", "AUD");
        currencyMap.put("Aruba", "AWG");
        currencyMap.put("Azerbaijan", "AZN");
        currencyMap.put("Bosnia and Herzegovina", "BAM");
        currencyMap.put("Barbados", "BBD");
        currencyMap.put("Bangladesh", "BDT");
        currencyMap.put("Bulgaria", "BGN");
        currencyMap.put("Bahrain", "BHD");
        currencyMap.put("Burundi", "BIF");
        currencyMap.put("Bermuda", "BMD");
        currencyMap.put("Brunei", "BND");
        currencyMap.put("Bolivia", "BOB");
        currencyMap.put("Brazil", "BRL");
        currencyMap.put("The Bahamas", "BSD");
        currencyMap.put("Bhutan", "BTN");
        currencyMap.put("Botswana", "BWP");
        currencyMap.put("Belarus", "BYN");
        currencyMap.put("Belize", "BZD");
        currencyMap.put("Canada", "CAD");
        currencyMap.put("Democratic Republic of the Congo", "CDF");
        currencyMap.put("Switzerland", "CHF");
        currencyMap.put("Chile", "CLP");
        currencyMap.put("China", "CNY");
        currencyMap.put("Colombia", "COP");
        currencyMap.put("Costa Rica", "CRC");
        currencyMap.put("Cuba", "CUP");
        currencyMap.put("Cabo Verde", "CVE");
        currencyMap.put("Czech Republic", "CZK");
        currencyMap.put("Djibouti", "DJF");
        currencyMap.put("Denmark", "DKK");
        currencyMap.put("Dominican Republic", "DOP");
        currencyMap.put("Algeria", "DZD");
        currencyMap.put("Egypt", "EGP");
        currencyMap.put("Eritrea", "ERN");
        currencyMap.put("Ethiopia", "ETB");
        currencyMap.put("Eurozone", "EUR");
        currencyMap.put("Fiji", "FJD");
        currencyMap.put("Falkland Islands", "FKP");
        currencyMap.put("Faroe Islands", "FOK");
        currencyMap.put("United Kingdom", "GBP");
        currencyMap.put("Georgia", "GEL");
        currencyMap.put("Guernsey", "GGP");
        currencyMap.put("Ghana", "GHS");
        currencyMap.put("Gibraltar", "GIP");
        currencyMap.put("Gambia", "GMD");
        currencyMap.put("Guinea", "GNF");
        currencyMap.put("Guatemala", "GTQ");
        currencyMap.put("Guyana", "GYD");
        currencyMap.put("Hong Kong", "HKD");
        currencyMap.put("Honduras", "HNL");
        currencyMap.put("Croatia", "HRK");
        currencyMap.put("Haiti", "HTG");
        currencyMap.put("Hungary", "HUF");
        currencyMap.put("Indonesia", "IDR");
        currencyMap.put("Israel", "ILS");
        currencyMap.put("Isle of Man", "IMP");
        currencyMap.put("India", "INR");
        currencyMap.put("Iraq", "IQD");
        currencyMap.put("Iran", "IRR");
        currencyMap.put("Iceland", "ISK");
        currencyMap.put("Jersey", "JEP");
        currencyMap.put("Jamaica", "JMD");
        currencyMap.put("Jordan", "JOD");
        currencyMap.put("Japan", "JPY");
        currencyMap.put("Kenya", "KES");
        currencyMap.put("Kyrgyzstan", "KGS");
        currencyMap.put("Cambodia", "KHR");
        currencyMap.put("Kiribati", "KID");
        currencyMap.put("Comoros", "KMF");
        currencyMap.put("South Korea", "KRW");
        currencyMap.put("Kuwait", "KWD");
        currencyMap.put("Cayman Islands", "KYD");
        currencyMap.put("Kazakhstan", "KZT");
        currencyMap.put("Laos", "LAK");
        currencyMap.put("Lebanon", "LBP");
        currencyMap.put("Sri Lanka", "LKR");
        currencyMap.put("Liberia", "LRD");
        currencyMap.put("Lesotho", "LSL");
        currencyMap.put("Libya", "LYD");
        currencyMap.put("Morocco", "MAD");
        currencyMap.put("Moldova", "MDL");
        currencyMap.put("Madagascar", "MGA");
        currencyMap.put("North Macedonia", "MKD");
        currencyMap.put("Myanmar", "MMK");
        currencyMap.put("Mongolia", "MNT");
        currencyMap.put("Macau", "MOP");
        currencyMap.put("Mauritania", "MRU");
        currencyMap.put("Mauritius", "MUR");
        currencyMap.put("Maldives", "MVR");
        currencyMap.put("Malawi", "MWK");
        currencyMap.put("Mexico", "MXN");
        currencyMap.put("Malaysia", "MYR");
        currencyMap.put("Mozambique", "MZN");
        currencyMap.put("Namibia", "NAD");
        currencyMap.put("Nigeria", "NGN");
        currencyMap.put("Nicaragua", "NIO");
        currencyMap.put("Norway", "NOK");
        currencyMap.put("Nepal", "NPR");
        currencyMap.put("New Zealand", "NZD");
        currencyMap.put("Oman", "OMR");
        currencyMap.put("Panama", "PAB");
        currencyMap.put("Peru", "PEN");
        currencyMap.put("Papua New Guinea", "PGK");
        currencyMap.put("Philippines", "PHP");
        currencyMap.put("Pakistan", "PKR");
        currencyMap.put("Paraguay", "PYG");
        currencyMap.put("Qatar", "QAR");
        currencyMap.put("Romania", "RON");
        currencyMap.put("Serbia", "RSD");
        currencyMap.put("Russia", "RUB");
        currencyMap.put("Rwanda", "RWF");
        currencyMap.put("Saudi Arabia", "SAR");
        currencyMap.put("Solomon Islands", "SBD");
        currencyMap.put("Seychelles", "SCR");
        currencyMap.put("Sudan", "SDG");
        currencyMap.put("Sweden", "SEK");
        currencyMap.put("Singapore", "SGD");
        currencyMap.put("Saint Helena", "SHP");
        currencyMap.put("Sierra Leone (new code)", "SLE");
        currencyMap.put("Sierra Leone (older code)", "SLL");
        currencyMap.put("Somalia", "SOS");
        currencyMap.put("Suriname", "SRD");
        currencyMap.put("South Sudan", "SSP");
        currencyMap.put("Sao Tome and Principe", "STN");
        currencyMap.put("Syria", "SYP");
        currencyMap.put("Eswatini", "SZL");
        currencyMap.put("Thailand", "THB");
        currencyMap.put("Tajikistan", "TJS");
        currencyMap.put("Turkmenistan", "TMT");
        currencyMap.put("Tunisia", "TND");
        currencyMap.put("Tonga", "TOP");
        currencyMap.put("Turkey", "TRY");
        currencyMap.put("Trinidad and Tobago", "TTD");
        currencyMap.put("Tuvalu", "TVD");
        currencyMap.put("Taiwan", "TWD");
        currencyMap.put("Tanzania", "TZS");
        currencyMap.put("Ukraine", "UAH");
        currencyMap.put("Uganda", "UGX");
        currencyMap.put("United States", "USD");
        currencyMap.put("Uruguay", "UYU");
        currencyMap.put("Uzbekistan", "UZS");
        currencyMap.put("Venezuela", "VES");
        currencyMap.put("Vietnam", "VND");
        currencyMap.put("Vanuatu", "VUV");
        currencyMap.put("Samoa", "WST");
        currencyMap.put("Central African CFA Franc (multiple countries)", "XAF");
        currencyMap.put("Eastern Caribbean", "XCD");
        currencyMap.put("Curacao", "XCG");
        currencyMap.put("IMF (Special Drawing Rights)", "XDR");
        currencyMap.put("West African CFA Franc (multiple countries)", "XOF");
        currencyMap.put("CFP franc (French territories)", "XPF");
        currencyMap.put("Yemen", "YER");
        currencyMap.put("South Africa", "ZAR");
        currencyMap.put("Zambia", "ZMW");
        currencyMap.put("Zimbabwe", "ZWL");
    }

    public static String getCurrencyCode(String countryName) {
        for (Map.Entry<String, String> entry : currencyMap.entrySet()) {
            if (countryName.equals(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;

    }
        /*
        keySet- zwraca zbior wszystkich kluczy czyli otwiera dostep do pelnych wpisow w mapie
        entrySet-zbior par klucz-wartosc pozniej zeby dosatac tylko klucz uzywamy getKey, a tylko wartosc - getValue
        values()- zwraca kolekcje tylko wartosci
        Jaka jest roznica miedzy keySet a bez tego?
        entrySet vs keySet
        Tylko kluczy (np. krajów)    	keySet()
        Klucz i wartość jednocześnie	entrySet()
        Tylko wartości (np. walut)	    values()



         */

}

