package eventure.event_service.dto.mapper;

import eventure.event_service.dto.EventCreateDto;
import eventure.event_service.dto.EventUpdateDto;
import eventure.event_service.model.entity.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "isAlive", ignore = true)
    @Mapping(target = "viewCount", ignore = true)
    @Mapping(target = "bannerPhotoUrl", ignore = true)
    @Mapping(target = "status", ignore = true)
    Event toEntityCreate(EventCreateDto createDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "organizerId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "bannerPhotoUrl", ignore = true)
    void updateEntityFromDto(EventUpdateDto dto, @MappingTarget Event entity);
}
