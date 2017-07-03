import java.io.IOException;

interface Produce<K> {
    K produce() throws IOException;
}
