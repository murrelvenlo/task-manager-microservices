package fact.it.teamservice.config;

import fact.it.teamservice.dto.TeamResponse;
import fact.it.teamservice.model.Team;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();

        // Configure the mapping for Team to TeamResponse
        TypeMap<Team, TeamResponse> teamTypeMap = modelMapper.createTypeMap(Team.class, TeamResponse.class);
        teamTypeMap.addMappings(mapper -> mapper.map(src -> src.getMembers(), TeamResponse::setMembers));

        return modelMapper;
    }
}
