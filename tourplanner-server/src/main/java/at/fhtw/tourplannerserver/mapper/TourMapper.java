package at.fhtw.tourplannerserver.mapper;

import at.fhtw.tourplannercommon.dto.TourDto;
import at.fhtw.tourplannerserver.entity.TourEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/*Design-Pattern:
Sorgt für automatische Wandlung Entity <--> DTO
 */
@Mapper(componentModel = "spring")
public interface TourMapper {

    TourMapper INSTANCE = Mappers.getMapper(TourMapper.class);

    TourDto toDto(TourEntity entity);

    TourEntity toEntity(TourDto dto);
}