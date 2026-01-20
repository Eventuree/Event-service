package eventure.event_service.dto.mapper;

import eventure.event_service.dto.EventCreateDto;
import eventure.event_service.dto.EventResponseDto;
import eventure.event_service.dto.EventUpdateDto;
import eventure.event_service.model.entity.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    Event toEntityCreate(EventCreateDto createDto);

    @Mapping(target = "organizerId", ignore = true)
    void updateEntityFromDto(EventUpdateDto dto, @MappingTarget Event entity);

    EventResponseDto toDto(Event event);
}
