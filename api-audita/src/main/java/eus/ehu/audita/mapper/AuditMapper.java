package eus.ehu.audita.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import eus.ehu.audita.dto.Audit;
import eus.ehu.audita.model.AuditEntity;

@Mapper(componentModel = "spring")
public interface AuditMapper {
	
	AuditMapper INSTANCE = Mappers.getMapper(AuditMapper.class);
	
	public AuditEntity toEntity(Audit source);

	public Audit toDto(AuditEntity source);
    
}
