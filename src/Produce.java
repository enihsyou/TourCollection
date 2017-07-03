import java.io.IOException;

@FunctionalInterface
interface Produce<K> {
    K produce() throws IOException;
}
