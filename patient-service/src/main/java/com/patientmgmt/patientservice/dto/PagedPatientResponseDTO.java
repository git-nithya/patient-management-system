package com.patientmgmt.patientservice.dto;

import java.util.List;

public class PagedPatientResponseDTO {

    private List<PatientResponseDTO> patientResponseDTOList;
    private int page;
    private int size;
    private int totalPages;
    private int totalElements;

    public PagedPatientResponseDTO() {
    }

    public PagedPatientResponseDTO(List<PatientResponseDTO> patientResponseDTOList, int page, int size, int totalPages, int totalElements) {
        this.patientResponseDTOList = patientResponseDTOList;
        this.page = page;
        this.size = size;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }

    public List<PatientResponseDTO> getPatientResponseDTOList() {
        return patientResponseDTOList;
    }

    public void setPatientResponseDTOList(List<PatientResponseDTO> patientResponseDTOList) {
        this.patientResponseDTOList = patientResponseDTOList;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }
}
