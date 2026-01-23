package com.example.moneymanager.service;

import com.example.moneymanager.dto.IncomeDto;
import com.example.moneymanager.entity.CategoryEntity;
import com.example.moneymanager.entity.ExpenseEntity;
import com.example.moneymanager.entity.IncomeEntity;
import com.example.moneymanager.entity.ProfileEntity;
import com.example.moneymanager.repository.CategoryRepository;
import com.example.moneymanager.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {
    private final CategoryRepository categoryRepository;
    private final IncomeRepository incomeRepository;
    private final ProfileService profileService;

    public IncomeDto addIncome(IncomeDto incomeDto) {
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findById(incomeDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category was not found"));
        IncomeEntity newincome = toEntity(incomeDto, profile, category);
        incomeRepository.save(newincome);
        return toDto(newincome);
    }

    public List<IncomeDto> getCurrentMonthIncomeForCurrentMonth() {
        ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<IncomeEntity> list = incomeRepository.findByProfileIdAndDateBetween(profile.getId(), startDate, endDate);
        return list.stream().map(this::toDto).toList();
    }

    public void deleteIncome(Long id) {
        ProfileEntity profile = profileService.getCurrentProfile();
        IncomeEntity income = incomeRepository.findById(id).orElseThrow(() -> new RuntimeException("Income not found"));
        if(!income.getId().equals(profile.getId())) {
            throw new RuntimeException("Unauthorized to delete income");
        }
        incomeRepository.delete(income);
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
