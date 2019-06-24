package com.intuit.fuzzymatcher.component;

import com.intuit.fuzzymatcher.domain.Document;
import com.intuit.fuzzymatcher.domain.Element;
import com.intuit.fuzzymatcher.domain.Match;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.intuit.fuzzymatcher.domain.ElementType.*;

/**
 *
 */
public class MatchServicePerfTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchServiceTest.class);

    private MatchService matchService = new MatchService();

    private static final int ELEM_PER_DOC = 4;

    @Test
    public void itShouldApplyMatchForBigData() throws IOException {
        applyMatch(getBigDataDocuments().limit(500).collect(Collectors.toList()));

        applyMatch(getBigDataDocuments().limit(1000).collect(Collectors.toList()));

        applyMatch(getBigDataDocuments().limit(1500).collect(Collectors.toList()));

        applyMatch(getBigDataDocuments().limit(2000).collect(Collectors.toList()));

        applyMatch(getBigDataDocuments().limit(2500).collect(Collectors.toList()));

        applyMatch(getBigDataDocuments().limit(3000).collect(Collectors.toList()));
    }

    private void applyMatch(List<Document> documentList) {
        long startTime = System.nanoTime();
        Map<String, List<Match<Document>>> result = matchService.applyMatchByDocId(documentList);
        long endTime = System.nanoTime();
        //Assert.assertEquals(116, result.size());
        long duration = (endTime - startTime) / 1000000;
        System.out.println("Execution time (ms) for + " + documentList.size() * ELEM_PER_DOC + " count : " + duration);
    }

    public Stream<Document> getBigDataDocuments() throws FileNotFoundException {
        AtomicInteger index = new AtomicInteger();
        return StreamSupport.stream(MatchServiceTest.getCSVReader("Sample-Big-Data.csv").spliterator(), false).map(csv -> {
            return new Document.Builder(index.incrementAndGet() + "")
                    .addElement(new Element.Builder().setType(NAME).setValue(csv[0]).createElement())
                    .addElement(new Element.Builder().setType(ADDRESS).setValue(getAddress(csv)).createElement())
                    .addElement(new Element.Builder().setType(PHONE).setValue(csv[5]).createElement())
                    .addElement(new Element.Builder().setType(EMAIL).setValue(csv[6]).createElement())
                    .createDocument();
        });
    }

    static String getAddress(String[] csv) {
        String address= StringUtils.EMPTY;
        if(csv!=null){
            StringJoiner addressBuilder = new StringJoiner(" ");
            addressBuilder.add(csv[1]);
            addressBuilder.add(csv[2]);
            addressBuilder.add(csv[3]);
            addressBuilder.add(csv[4]);
            address =  StringUtils.trimToEmpty(addressBuilder.toString());
        }
        return address;
    }
}
