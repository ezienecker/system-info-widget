package com.manuzid.systeminfowidget.category;

/**
 * Builder Klasse für die Informationen die auf den Widget angezeigt werden.
 *
 * Created by Emanuel Zienecker on 21.09.17.
 * Copyright (c) 2017 Emanuel Zienecker. All rights reserved.
 */
public class Informationen {
    // Id für das Label
    final String firstLabel;
    final String secondLabel;
    final String thirdLabel;
    final String fourthLabel;
    final String fifthLabel;
    final String sixthLabel;
    final String seventhLabel;

    // Wert der Eigenschaft
    final String firstValue;
    final String secondValue;
    final String thirdValue;
    final String fourthValue;
    final String fifthValue;
    final String sixthValue;
    final String seventhValue;

    /**
     * Innere Klasse für Kontruktion die Informationen
     */
    public static class Builder {
        // Id für das Label
        private String firstLabel;
        private String secondLabel;
        private String thirdLabel;
        private String fourthLabel;
        private String fifthLabel;
        private String sixthLabel;
        private String seventhLabel;

        // Wert der Eigenschaft
        private String firstValue;
        private String secondValue;
        private String thirdValue;
        private String fourthValue;
        private String fifthValue;
        private String sixthValue;
        private String seventhValue;

        public Builder first(String firstLabel, String firstValue) {
            this.firstLabel = firstLabel;
            this.firstValue = firstValue;
            return this;
        }

        public Builder second(String secondLabel, String secondValue) {
            this.secondLabel = secondLabel;
            this.secondValue = secondValue;
            return this;
        }

        public Builder third(String thirdLabel, String thirdValue) {
            this.thirdLabel = thirdLabel;
            this.thirdValue = thirdValue;
            return this;
        }

        public Builder fourth(String fourthLabel, String fourthValue) {
            this.fourthLabel = fourthLabel;
            this.fourthValue = fourthValue;
            return this;
        }

        public Builder fifth(String fifthLabel, String fifthValue) {
            this.fifthLabel = fifthLabel;
            this.fifthValue = fifthValue;
            return this;
        }

        public Builder sixth(String sixthLabel, String sixthValue) {
            this.sixthLabel = sixthLabel;
            this.sixthValue = sixthValue;
            return this;
        }

        public Builder seventh(String seventhLabel, String seventhValue) {
            this.seventhLabel = seventhLabel;
            this.seventhValue = seventhValue;
            return this;
        }

        /**
         * Ruft den eigentlichen Konstruktor für die Informationen auf
         *
         * @return die neuen {@link Informationen}
         */
        public Informationen build() {
            return new Informationen(this);
        }
    }

    private Informationen(Builder builder) {
        this.firstLabel = builder.firstLabel;
        this.secondLabel = builder.secondLabel;
        this.thirdLabel = builder.thirdLabel;
        this.fourthLabel = builder.fourthLabel;
        this.fifthLabel = builder.fifthLabel;
        this.sixthLabel = builder.sixthLabel;
        this.seventhLabel = builder.seventhLabel;
        this.firstValue = builder.firstValue;
        this.secondValue = builder.secondValue;
        this.thirdValue = builder.thirdValue;
        this.fourthValue = builder.fourthValue;
        this.fifthValue = builder.fifthValue;
        this.sixthValue = builder.sixthValue;
        this.seventhValue = builder.seventhValue;
    }

}