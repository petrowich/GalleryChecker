package org.petrowich.gallerychecker.processig.external.redtube.reader;

import org.petrowich.gallerychecker.dto.external.ExTubeGalleryDto;
import org.petrowich.gallerychecker.utils.filereaders.AbstractTubeGalleryFileReader;
import org.petrowich.gallerychecker.utils.filereaders.exceptions.TubeGalleryReaderException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import static java.util.Locale.ROOT;

@Component
public class RedTubeNewReader extends AbstractTubeGalleryFileReader {

    private static final String TAG_SEPARATOR = "\\d+::";
    private static final String DRAFT_EMBED_CODE = "<iframe src=\"https://embed.redtube.com/?id=#\" frameborder=\"0\" width=\"560\" height=\"340\" scrolling=\"no\" allowfullscreen></iframe>";

    @Override
    protected ExTubeGalleryDto lineToGallery(String line) throws TubeGalleryReaderException {
        List<String> values = splitLine(line);

        return new ExTubeGalleryDto()
                .setId(values.get(0))
                .setThumbUrl(extractThumbUrl(values.get(1)))
                .setUrl(values.get(2))
                .setEmbedCode(generateEmbedCode(values.get(0)))
                .setDescription(values.get(3))
                .setNiche(extractTags(values.get(4)))
                .setTags(extractTags(values.get(5)))
                .setModel(values.get(6))
                .setDuration(extractDuration(values.get(8)))
                .setDate(values.get(9));
    }

    private List<String> splitLine(String line) {
        List<String> result = new ArrayList<>();

        String galleryId = readSimple(line);
        result.add(galleryId);
        line = line.substring(galleryId.length() + 1);

        String galleryThumbUrl = readSimple(line);
        result.add(galleryThumbUrl);
        line = line.substring(galleryThumbUrl.length() + 1);

        String galleryUrl = readSimple(line);
        result.add(galleryUrl);
        line = line.substring(galleryUrl.length() + 1);

        String galleryTitle = readQuoted(line);
        result.add(galleryTitle);
        line = line.substring(galleryTitle.length() + 1);

        String galleryNiche = readSimple(line);
        result.add(galleryNiche);
        line = line.substring(galleryNiche.length() + 1);

        String galleryTags = readSimple(line);
        result.add(galleryTags);
        line = line.substring(galleryTags.length() + 1);

        String galleryModel = readSimple(line);
        result.add(galleryModel);
        line = line.substring(galleryModel.length() + 1);

        String galleryRating = readSimple(line);
        result.add(galleryRating);
        line = line.substring(galleryRating.length() + 1);

        String galleryDuration = readSimple(line);
        result.add(galleryDuration);
        line = line.substring(galleryDuration.length() + 1);

        String galleryDate = readSimple(line);
        result.add(galleryDate);

        return result;
    }

    private static String readSimple(String line) {
        char[] chars = line.toCharArray();
        char currentChar;
        char nextChar;
        String result = "";

        if (chars[0] == '|') {
            return result;
        }

        for (int i = 0; i < chars.length; i++) {
            currentChar = chars[i];
            nextChar = chars[i + 1];
            result = result.concat(String.valueOf(currentChar));
            if (nextChar == '|') {
                break;
            }
        }
        return result;
    }

    private static String readQuoted(String line) {
        char[] chars = line.toCharArray();
        char currentChar;
        char nextChar;
        String result = "";

        if (chars[0] == '#') {
            return readSimple(line);
        }

        for (int i = 0; i < chars.length; i++) {
            currentChar = chars[i];
            nextChar = chars[i + 1];
            result = result.concat(String.valueOf(currentChar));
            if (currentChar == '\'' && nextChar == '|' && i > 0) {
                break;
            }
        }
        return result;
    }

    private String extractThumbUrl(String thumbUrl) {
        if (thumbUrl.endsWith("1.jpg")) {
            return thumbUrl.replace("1.jpg", "5.jpg");
        }
        return thumbUrl;
    }

    private String extractTags(String stringTags) {
        String result = "";
        for (String tag : stringTags.split(TAG_SEPARATOR)) {
            tag = tag.replace(",", ";");
            result = result.concat(tag);
        }
        return result;
    }

    private String extractDuration(String duration) {
        return duration.toLowerCase(ROOT)
                .replace("s", "")
                .trim();
    }

    private String generateEmbedCode(String galleryId) {
        return DRAFT_EMBED_CODE.replace("?id=#", "?id=".concat(galleryId));
    }
}
