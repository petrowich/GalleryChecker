package org.petrowich.gallerychecker.processig.external.pornhub.readers;

import org.petrowich.gallerychecker.dto.external.ExTubeGalleryDto;
import org.petrowich.gallerychecker.utils.filereaders.AbstractTubeGalleryFileReader;
import org.petrowich.gallerychecker.utils.filereaders.exceptions.TubeGalleryReaderException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PornHubReader extends AbstractTubeGalleryFileReader {

    private static final String LINE_SEPARATOR = "\\|";

    @Override
    protected ExTubeGalleryDto lineToGallery(String line) throws TubeGalleryReaderException {
        String[] values = line.split(LINE_SEPARATOR);
        if (values.length != 8) {
            String message = String.format("mismatch in the number of fields in a row:%n%s", line);
            throw new TubeGalleryReaderException(message);
        }
        List<String> valueList = Arrays.stream(values)
                .map(value -> value.replace("\"\"", "'"))
                .map(value -> value.replace("\"", ""))
                .collect(Collectors.toList());

        return new ExTubeGalleryDto()
                .setId(extractKey(valueList.get(1)))
                .setUrl(valueList.get(1))
                .setDescription(valueList.get(3))
                .setDuration(valueList.get(5))
                .setThumbUrl(valueList.get(7))
                .setEmbedCode(valueList.get(0))
                .setNiche(valueList.get(2))
                .setTags(valueList.get(4))
                .setModel(extractModel(valueList.get(6)))
                .setDate(extractVideoDateFromThumbUrl(valueList.get(7)));
    }

    private String extractKey(String url) {
        return  url.substring(url.indexOf("viewkey=") + 8);
    }

    private String extractVideoDateFromThumbUrl(String url) {
        String dateRaper = "/videos/";
        if(url.contains(dateRaper)) {
            int startIndex = url.indexOf(dateRaper) + dateRaper.length();
            String rawDate = url.substring(startIndex, startIndex + 9);
            String year = rawDate.substring(0, 4);
            String month = rawDate.substring(4, 6);
            String day = rawDate.substring(7, 9);
            return String.format("%s-%s-%s", year, month, day);
        }
        return null;
    }

    private String extractModel(String model) {
        if (model.length()==1) {
            return model.replace("'","");
        }
        return model;
    }
}
