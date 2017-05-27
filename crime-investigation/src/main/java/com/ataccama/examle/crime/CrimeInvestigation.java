package com.ataccama.examle.crime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import com.google.common.base.Optional;

import scala.Tuple2;
import scala.Tuple3;

public class CrimeInvestigation {
    private static final String LOCAL = "local[*]";
    private static final int FACTOR = 10;

    private CrimeInvestigation() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Provide data path prefix!");
        }
        String pathPrefix = args[0];

        SparkConf config = new SparkConf();
        config.setAppName("CrimeInvestigation");
        config.setIfMissing("spark.master", LOCAL);
        config.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");

        try (JavaSparkContext sc = new JavaSparkContext(config)) {
            sc.setLogLevel("WARN");

            JavaRDD<Crime> crimes = readCrimes(sc, pathPrefix + "data/crimes/", 30);
            JavaRDD<IUCR> iucr = readIUCR(sc, pathPrefix + "data/iucr.csv", 1);

            info(crimes, iucr);

            JavaPairRDD<String, Crime> kc = crimes
                    .mapPartitionsToPair(it -> {
                        Random r = new Random();
                        List<Tuple2<String, Crime>> pairs = new ArrayList<>();
                        it.forEachRemaining(c -> pairs.add(
                                new Tuple2<>(c.getIucr() + "-" + r.nextInt(FACTOR), c)));
                        return pairs;
                    });
            JavaPairRDD<String, IUCR> ki = iucr.flatMapToPair(i -> IntStream.range(0, FACTOR)
                    .mapToObj(f -> new Tuple2<>(i.getCode() + "-" + f, i))
                    .collect(Collectors.toList()));

            joined(kc, ki);

            location(crimes, kc, ki);

            if (LOCAL.equals(config.get("spark.master"))) {
                // System.in.read();
            }
        }
    }

    private static JavaRDD<Crime> readCrimes(JavaSparkContext sc, String path, int parts) {
        return sc.wholeTextFiles(path, parts)
                .flatMap(f -> Arrays.asList(f._2.split("\n")))
                .map(l -> new Crime(Utils.split(l, ",", "\"")));
    }

    private static JavaRDD<IUCR> readIUCR(JavaSparkContext sc, String path, int parts) {
        return sc.textFile(path, parts).map(l -> new IUCR(Utils.split(l, ",", "\"")));
    }

    private static void info(JavaRDD<Crime> crimes, JavaRDD<IUCR> iucr) {
        System.out.println("\tNumber of all crime records:");
        System.out.println(crimes.count()); // 6338495

        Map<String, Long> iucrCounts = crimes.map(Crime::getIucr).countByValue();
        System.out.println("\tNumber of records per IUCR:");
        Utils.printCountMap(iucrCounts);

        System.out.println("\tNumber of all IUCR records:");
        System.out.println(iucr.count()); // 401

        JavaRDD<IUCR> indexedIucr = iucr.filter(IUCR::isIndex);
        System.out.println("\tNumber of all indexed IUCR records:");
        System.out.println(indexedIucr.count()); // 100

        System.out.println("\tList of all indexed IUCR records:");
        indexedIucr.collect().forEach(System.out::println);
    }

    private static void joined(JavaPairRDD<String, Crime> kc, JavaPairRDD<String, IUCR> ki) {
        JavaPairRDD<String, Tuple2<Crime, IUCR>> joined = kc.join(ki);
        System.out.println("\tNumber of records which successfully joined:");
        System.out.println(joined.count()); // 6271018

        missing(kc, ki);
    }

    private static void missing(JavaPairRDD<String, Crime> kc, JavaPairRDD<String, IUCR> ki) {
        JavaPairRDD<String, Tuple2<Crime, Optional<IUCR>>> notJoined = kc.leftOuterJoin(ki)
                .filter(ci -> !ci._2._2.isPresent());
        notJoined.cache();

        Map<String, Long> notJoinedCounts = notJoined.map(ci -> ci._2._1.getIucr()).countByValue();
        System.out.println("\tMissing IUCR codes and number of their occurrences:");
        Utils.printCountMap(notJoinedCounts); // 16

        JavaRDD<IUCR> missingIUCRs = notJoined.map(ci -> {
            Crime c = ci._2._1;
            return IUCR.builder()
                    .code(c.getIucr())
                    .primary(c.getPrimaryType())
                    .secondary(c.getDescription())
                    .build();
        }).distinct();

        System.out.println("\tCandidates of missing IUCR:");
        missingIUCRs.collect().forEach(System.out::println);
    }

    private static void location(JavaRDD<Crime> crimes, JavaPairRDD<String, Crime> kc,
            JavaPairRDD<String, IUCR> ki) {
        JavaRDD<Crime> located = crimes.filter(c -> c.getLatitude() != null && c.getLongitude() != null);
        Tuple3<Double, Double, Integer> aggregate = located
                .aggregate(new Tuple3<>(0.0, 0.0, 0),
                        (t, c) -> new Tuple3<>(t._1() + c.getLatitude(), t._2() + c.getLongitude(), t._3() + 1),
                        (t1, t2) -> new Tuple3<>(t1._1() + t2._1(), t1._2() + t2._2(), t1._3() + t2._3()));
        double lat = aggregate._1() / aggregate._3();
        double lng = aggregate._2() / aggregate._3();
        System.out.println("\tCrime centroid:");
        System.out.println(lat + ", " + lng); // 41.84179546422172, -87.67194930074608
        System.out.println("https://www.google.cz/maps/search/41.84179546422172,-87.67194930074608");

        distribution(kc, ki, lat, lng);
    }

    private static void distribution(JavaPairRDD<String, Crime> kc, JavaPairRDD<String, IUCR> ki, double lat,
            double lng) {
        JavaRDD<Crime> locatedIndexed = kc.join(ki)
                .filter(ci -> ci._2._2.isIndex())
                .map(ci -> ci._2._1)
                .filter(c -> c.getLatitude() != null && c.getLongitude() != null);

        JavaPairRDD<Integer, Double> distribution = locatedIndexed
                .map(c -> Utils.distance(lat, lng, c.getLatitude(), c.getLongitude()))
                .mapToPair(d -> new Tuple2<>(d.intValue(), Utils.distancePriority(d.intValue())))
                .reduceByKey((p1, p2) -> p1 + p2, 1);

        List<Tuple2<Integer, Double>> distr = new ArrayList<>(distribution.collect());
        distr.sort((d1, d2) -> Integer.compare(d1._1, d2._1));

        System.out.println("\tCrimes per km^2 in distance:");
        distr.forEach(System.out::println);
    }
}
