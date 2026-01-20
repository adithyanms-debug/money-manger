package com.example.moneymanager.service;

import com.example.moneymanager.dto.IncomeDto;
import com.example.moneymanager.entity.CategoryEntity;
import com.example.moneymanager.entity.IncomeEntity;
import com.example.moneymanager.entity.ProfileEntity;
import com.example.moneymanager.repository.CategoryRepository;
import com.example.moneymanager.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IncomeService {
    private final CategoryRepository categoryRepository;
    private final IncomeRepository incomeRepository;
    private final ProfileService profileService;

    public IncomeDto addIncome(IncomeDto incomeDto) {
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findById(incomeDto.getId())
                .orElseThrow(() -> new RuntimeException("Category was not found"));
        IncomeEntity newincome = toEntity(incomeDto, profile, category);
        incomeRepository.save(newincome);
        return toDto(newincome);
    }

    private IncomeEntity toEntity(IncomeDto dto, ProfileEntity profile, CategoryEntity category) {
        return IncomeEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .icon(dto.getIcon())
                .date(dto.getDate())
                .amount(dto.getAmount())
                .category(category)
                .profile(profile)
                .build();
    }

    private IncomeDto toDto(IncomeEntity entity) {
        return IncomeDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .icon(entity.getIcon())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
                .amount(entity.getAmount())
                .date(entity.getDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
