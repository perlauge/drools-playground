Feature: Simpel beregning af dækningsbdrag
  Scenario: 
    Given en "AAR2" ejendom i kommune 1337
    And kommune 1337 med grundskyldspromille 3, som opkræver dækningsafgift for året 2021
    When der genberegnes for år 2021
    Then så er dækningsafgiften 2250