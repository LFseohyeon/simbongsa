package com.app.simbongsa.service.volunteer;

import com.app.simbongsa.domain.FileDTO;
import com.app.simbongsa.domain.InquiryDTO;
import com.app.simbongsa.domain.VolunteerWorkDTO;
import com.app.simbongsa.entity.file.VolunteerWorkFile;
import com.app.simbongsa.entity.inquiry.Inquiry;
import com.app.simbongsa.entity.volunteer.VolunteerWork;
import com.app.simbongsa.repository.volunteer.VolunteerWorkFileRepository;
import com.app.simbongsa.repository.volunteer.VolunteerWorkRepository;
import com.app.simbongsa.search.admin.AdminVolunteerSearch;
import com.app.simbongsa.type.FileRepresentationalType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Qualifier("volunteerWork") @Primary
@Slf4j
public class VolunteerWorkServiceImpl implements VolunteerWorkService {
    private final VolunteerWorkRepository volunteerWorkRepository;
    private final VolunteerWorkFileRepository volunteerWorkFileRepository;

    /*메인페이지 - 봉사 활동 8개 띄우기*/
    @Override
    public List<VolunteerWorkDTO> getVolunteerList() {

        List<VolunteerWork> volunteerWorkList = volunteerWorkRepository.findVolunteerWorkList();

        List<VolunteerWorkDTO> volunteerWorkDTOS = volunteerWorkList.stream()
                .map(volunteerWork -> {
                    VolunteerWorkDTO volunteerWorkDTO = toVolunteerWorkDTO(volunteerWork);
                    VolunteerWorkFile volunteerWorkFile = volunteerWork.getVolunteerWorkFile();
                    FileDTO fileDTO = toFileDTO(volunteerWorkFile);
                    volunteerWorkDTO.setFileDTO(fileDTO);
                    return volunteerWorkDTO;
                }).collect(Collectors.toList());

//        List<VolunteerWorkDTO> volunteerWorkDTOS = volunteerWorkList.stream().map(this::toVolunteerWorkDTO).collect(Collectors.toList());
        volunteerWorkList.stream().map(VolunteerWork::getVolunteerWorkFile).forEach(v -> log.info(v.toString()));
        volunteerWorkDTOS.stream().map(VolunteerWorkDTO::toString).forEach(v -> log.info(v + "여기야!!!!!!!!!!!!!!!!!!!!!!=================="));
        return volunteerWorkDTOS;
    }

    @Override
    public VolunteerWork getCurrentSequence() {
        return volunteerWorkRepository.getCurrentSequence_QueryDSL();
    }


    @Override
    public Page<VolunteerWorkDTO> getVolunteerWork(Integer page, AdminVolunteerSearch adminVolunteerSearch) {
        Page<VolunteerWork> volunteerWorks = volunteerWorkRepository.findAllWithPaging(adminVolunteerSearch, PageRequest.of(page, 5));
        List<VolunteerWorkDTO> volunteerWorkDTOS = volunteerWorks.getContent().stream().map(this::toVolunteerWorkDTO).collect(Collectors.toList());
        return new PageImpl<>(volunteerWorkDTOS, volunteerWorks.getPageable(), volunteerWorks.getTotalElements());
    }

    @Override
    public void saveVolunteerWork(VolunteerWorkDTO volunteerWorkDTO) {
        FileDTO fileDTO = volunteerWorkDTO.getFileDTO();

        volunteerWorkRepository.save(toVolunteerWorkEntity(volunteerWorkDTO));

        fileDTO.setFileRepresentationalType(FileRepresentationalType.REPRESENTATION);
        fileDTO.setVolunteerWork(getCurrentSequence());
        volunteerWorkFileRepository.save(toVolunteerWorkFileEntity(fileDTO));
    }

    @Override
    public VolunteerWorkDTO getVolunteerWorkDetail(Long id) {
        return toVolunteerWorkDTO(volunteerWorkRepository.findById_QueryDSL(id).get());
    }

    @Override
    public void deleteVolunteerWorkByIds(List<Long> ids) {
        volunteerWorkRepository.deleteAllById(ids);
    }

    @Override
    @Transactional
    public void updateVolunteerWork(VolunteerWorkDTO volunteerWorkDTO) {
        FileDTO fileDTO = volunteerWorkDTO.getFileDTO();

        volunteerWorkRepository.updateVolunteerWork(toVolunteerWorkEntity(volunteerWorkDTO));
        volunteerWorkFileRepository.updateVolunteerWorkFile(toVolunteerWorkFileEntity(fileDTO));
    }

}

