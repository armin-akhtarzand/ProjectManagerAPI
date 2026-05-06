package se.iths.armin.projectmanagerapi.mapper;

public interface EntityMapper<Entity, RequestDto, RespondDto> {


    Entity toEntity(RequestDto requestDto);

    RequestDto toDto(Entity entity);

    void updateEntity(Entity entity, RequestDto requestDto);
}
