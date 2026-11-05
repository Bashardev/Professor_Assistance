package com.progressoft;

public enum Category {
       ELITE(),
       PASSED(),
       FAILED();

       public static Category of(double zScore, double eliteLimit, double failedLimit) {
              if (zScore >= eliteLimit) {
                     return ELITE;
              }
              if (zScore <= failedLimit) {
                     return FAILED;
              }
              return PASSED;
       }
}
