package ua.c0de4fun.backend.util.random;

import lombok.experimental.UtilityClass;

import java.util.Random;
import java.util.UUID;

@UtilityClass
public class RandomUtil {

    public final static Random RANDOM = new Random();

    public int generateAccountNumber() {
        int bound = 1000;
        return bound * RANDOM.nextInt(bound);
    }

    public String generateToken() {
        return UUID.randomUUID().toString();
    }
}