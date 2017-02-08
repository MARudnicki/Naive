package naive.pure;

import javafx.util.Pair;
import naive.ComponentFactory;
import naive.NaiveBayesEngine;
import naive.TestAbstract;
import naive.classifiers.ReviewClassfier;
import org.junit.Test;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static naive.ComponentFactory.getDataset;
import static naive.ComponentFactory.getEngine;
import static org.junit.Assert.assertTrue;

/**
 * Created by Maciej Rudnicki on 03/02/2017.
 */
public class ReviewTest extends TestAbstract {

    @Test
    public void runReviewTest() throws Exception{
        long summaryCorrectAnswers = 0;
        long summaryRecords = 0;

        for(int i=0 ; i< NMBER_OF_TEST_RUNS; i++){
            Pair<Long, Long> pair = singleRun();

            summaryCorrectAnswers += pair.getKey();
            summaryRecords += pair.getValue();

            System.out.println(String.join(" ", "Loop nr",String.valueOf(i+1),
                    "Reviews correct answers :",String.valueOf(pair.getKey()),
                    "on ",String.valueOf(pair.getValue()),
                    "what gives", String.valueOf((double)pair.getKey()*100/pair.getValue()),
                    "efficiency"
                    ));
        }

        System.out.println(String.join(" ","Summary recognised", String.valueOf(summaryCorrectAnswers),
                "in set of",String.valueOf(summaryRecords),"records.",
                "Spam. Summary efficiency is ",
                String.valueOf((double)summaryCorrectAnswers*100/summaryRecords),"%"));

    }

    private Pair<Long, Long> singleRun() throws Exception{
        dataset = getDataset(ReviewClassfier.class);
        engine = getEngine(dataset);

        data = new HashMap<>();

        data.putAll(prepareDeceptiveReviewsNegative());
        data.putAll(prepateDeceptiveReviewsPositive());
        data.putAll(prepareThuthfullReviewsNegative());
        data.putAll(prepareThuthfullReviewsPositive());

        Pair<
                Map<URL,Enum>,
                Map<URL,Enum>
                > pair = splitDataIntoTrainingAndVerificationSet(data);

        Map<URL, Enum> trainingSet = pair.getKey();
        Map<URL, Enum> verificationSet = pair.getValue();

        dataset.train(trainingSet);

        long correctValues = verificationSet.entrySet().stream()
                .filter(this::isPredictionCorrect)
                .count();
        long allValues = (long) verificationSet.entrySet().size();

        return new Pair<>(correctValues, allValues);
    }

}
