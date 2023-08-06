package com.pjt.petmily.domain.curation.service;

import com.pjt.petmily.domain.curation.dto.CurationBookmarkDto;
import com.pjt.petmily.domain.curation.dto.NewsCurationDto;
import com.pjt.petmily.domain.curation.entity.Curationbookmark;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public interface CurationService {



    void crawlAndSaveNews(String species, String category) throws IOException;


//    List<NewsCurationDto> getNewsData(String spices);
    Map<String, List<NewsCurationDto>> getNewsData(String species);

    void curationBookmark(String userEmail, Long cId);

    Long emailToId (String userEmail);

//    List<Long> userBookmark(CurationBookmarkDto curationbookmarkDto);
}
