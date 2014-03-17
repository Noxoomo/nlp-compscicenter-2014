import java.util.HashSet;
import java.util.Set;

/**
 * User: Vasily
 * Date: 16.03.14
 * Time: 15:34
 */
public class Checker<T> {
    private Set<T> real = null;
    private Set<T> classifier = null;
    private Set<T> intersection = null;
    private double tp, fp, fn, tn = 0;
    //TODO: whtat is tn for sentence?

    Checker(Set<T> real, Set<T> classifier) {
        this.real = real;
        this.classifier = classifier;
        this.intersection = new HashSet<>(real);
        this.intersection.retainAll(classifier);

        tp = this.intersection.size();
        Set<T> tmp = new HashSet(classifier);
        tmp.removeAll(real);
        fp = tmp.size();
        tmp = new HashSet<>(real);
        tmp.removeAll(classifier);
        fn = tmp.size();
    }

    public double getAccuracy() {
        return (tp + tn) / (tp + tn + fp + fn);

    }

    public double getPrecession() {
        return 1.0 * intersection.size() / classifier.size();

    }

    public double getRecall() {
        return 1.0 * intersection.size() / real.size();
    }

    public double getF1() {
        double p = getPrecession();
        double r = getRecall();
        return 2 * p * r / (p + r);
    }

}
