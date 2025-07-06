package at.fhtw.tourplannerserver.mapper;

import at.fhtw.tourplannercommon.dto.TourLogDto;
import at.fhtw.tourplannerserver.entity.TourLogEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TourLogMapper {
    TourLogMapper INSTANCE = Mappers.getMapper(TourLogMapper.class);

    TourLogDto   toDto(TourLogEntity entity);
    TourLogEntity toEntity(TourLogDto dto);
}