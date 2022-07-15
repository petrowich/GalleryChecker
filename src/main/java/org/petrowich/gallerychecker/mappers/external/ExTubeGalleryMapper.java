package org.petrowich.gallerychecker.mappers.external;

import lombok.extern.log4j.Log4j2;
import org.petrowich.gallerychecker.dto.external.ExTubeGalleryDto;
import org.petrowich.gallerychecker.models.galleries.TubeGallery;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.StringJoiner;

import static java.util.Locale.ROOT;
import static org.petrowich.gallerychecker.utils.EmbedCodeUrlExtractor.extractUrlFromEmbedCode;

@Log4j2
@Component
public class ExTubeGalleryMapper {

    private static final Integer TAGS_NUMBER_LIMIT = 10;
    private static final Integer TAG_LENGTH_LIMIT = 25;
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public TubeGallery exportedToTubeGallery(ExTubeGalleryDto exTubeGalleryDto) {
        return new TubeGallery()
                .setUrl(exTubeGalleryDto.getUrl())
                .setDescription(exTubeGalleryDto.getDescription())
                .setDuration(durationToInteger(exTubeGalleryDto))
                .setThumbUrl(exTubeGalleryDto.getThumbUrl())
                .setEmbedCode(exTubeGalleryDto.getEmbedCode())
                .setVideoUrl(extractUrlFromEmbedCode(exTubeGalleryDto.getEmbedCode()))
                .setTags(extractTags(exTubeGalleryDto))
                .setModel(exTubeGalleryDto.getModel())
                .setExternalId(exTubeGalleryDto.getId())
                .setVideoDate(convertToDate(exTubeGalleryDto.getDate()))
                .setActive(true);
    }

    public TubeGallery deletedToTubeGallery(ExTubeGalleryDto exTubeGalleryDto) {
        return new TubeGallery()
                .setExternalId(exTubeGalleryDto.getId())
                .setActive(false);
    }

    private Integer durationToInteger(ExTubeGalleryDto exTubeGalleryDto) {
        String stringDuration = exTubeGalleryDto.getDuration();
        try {
            return Integer.valueOf(stringDuration);
        } catch (NumberFormatException exception) {
            log.error("problem in parsing duration from '{}': {}",
                    stringDuration, exception.getMessage(), exception);
            return 0;
        }
    }

    private String extractTags(ExTubeGalleryDto exTubeGalleryDto) {
        List<String> tagList = new ArrayList<>();
        String niche = exTubeGalleryDto.getNiche();

        if (niche != null && niche.length() > 0 && !niche.toLowerCase(ROOT).equals("unknown")) {
            tagList.addAll(Arrays.asList(niche.split(";")));
            tagList.add(niche);
        }

        String[] tags = exTubeGalleryDto.getTags().split(",");

        Arrays.stream(tags)
                .filter(tag -> tag.length() <= TAG_LENGTH_LIMIT)
                .limit(TAGS_NUMBER_LIMIT)
                .forEach(tagList::add);

        tagList.forEach(tag -> tag = tag
                .replace("_", " ")
                .replace("-", " ")
                .trim());

        return distinctTags(String.join(";", tagList));
    }

    private String distinctTags(String tags) {
        StringJoiner stringJoiner = new StringJoiner(";");

        Arrays.stream(tags.split(";"))
                .distinct()
                .map(this::checkTeen)
                .forEach(stringJoiner::add);
        return stringJoiner.toString();
    }

    private String checkTeen(String tag) {
        if (tag.toLowerCase(ROOT).startsWith("teen") && !tag.contains("18")) {
            return String.format("%s (18+)", tag);
        }
        return tag;
    }

    private LocalDate convertToDate(String dateString) {
        if(dateString == null || dateString.isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateString, dateFormatter);
    }
}
