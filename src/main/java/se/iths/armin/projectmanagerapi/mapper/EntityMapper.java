package se.iths.armin.projectmanagerapi.mapper;

public interface EntityMapper<Entity, RequestDto, RespondDto> {


    Entity toEntity(RequestDto requestDto);

    RespondDto toDto(Entity entity);


}
