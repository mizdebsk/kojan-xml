/*-
 * Copyright (c) 2024 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.kojan.xml;

import java.util.ArrayList;
import java.util.List;

class Engine {
    private String fuel;
    private Integer power;

    public String getFuel() {
        return fuel;
    }

    public void setFuel(String fuel) {
        this.fuel = fuel;
    }

    public Integer getPower() {
        return power;
    }

    public void setPower(Integer power) {
        this.power = power;
    }
}

class Trailer {
    private final String vin;

    public Trailer(String vin) {
        this.vin = vin;
    }

    public String getVin() {
        return vin;
    }

    static class B {
        private String vin;

        public void setVin(String vin) {
            this.vin = vin;
        }

        public Trailer customBuild() {
            return new Trailer(vin);
        }
    }
}

class Wheel {
    private final String tire;

    public Wheel(String tire) {
        this.tire = tire;
    }

    public String getTire() {
        return tire;
    }
}

class Car {
    private final String vin;
    private final Integer year;
    private final Engine engine;
    private final Trailer trailer;
    private final List<String> names;
    private final List<Integer> reviews;
    private final List<Wheel> wheels;

    public Car(
            String vin,
            Integer year,
            Engine engine,
            Trailer trailer,
            List<String> names,
            List<Integer> reviews,
            List<Wheel> wheels) {
        this.vin = vin;
        this.year = year;
        this.engine = engine;
        this.trailer = trailer;
        this.names = names;
        this.reviews = reviews;
        this.wheels = wheels;
    }

    public String getVin() {
        return vin;
    }

    public Integer getYear() {
        return year;
    }

    public Engine getEngine() {
        return engine;
    }

    public Trailer getTrailer() {
        return trailer;
    }

    public List<String> getNames() {
        return names;
    }

    public List<Integer> getReviews() {
        return reviews;
    }

    public List<Wheel> getWheels() {
        return wheels;
    }

    static class B implements Builder<Car> {
        private String vin;
        private Integer year;
        private Engine engine;
        private Trailer trailer;
        private List<String> names = new ArrayList<>();
        private List<Integer> reviews = new ArrayList<>();
        private List<Wheel> wheels = new ArrayList<>();

        public void setVin(String vin) {
            this.vin = vin;
        }

        public void setYear(Integer year) {
            this.year = year;
        }

        public void setEngine(Engine engine) {
            this.engine = engine;
        }

        public void setTrailer(Trailer trailer) {
            this.trailer = trailer;
        }

        public void addName(String name) {
            names.add(name);
        }

        public void addReview(Integer review) {
            reviews.add(review);
        }

        public void addWheel(Wheel wheel) {
            wheels.add(wheel);
        }

        public Car build() {
            return new Car(vin, year, engine, trailer, names, reviews, wheels);
        }
    }
}
