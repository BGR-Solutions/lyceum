package com.lyceum.academic.infra.config;

import com.lyceum.academic.infra.api.dto.ApiErrorResponse;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the OpenAPI/Swagger metadata for the academic service.
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI academicOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Academic Service API")
                        .description("""
                                API de gestão acadêmica responsável pelo cadastro de alunos, cursos, disciplinas e turmas,
                                além do controle completo do ciclo de matrículas com regras de negócio e controle de vagas.
                                
                                **Regras de negócio principais:**
                                - Um aluno só pode ser matriculado em turmas abertas.
                                - Uma turma possui limite de vagas; ao confirmar a matrícula a vaga é consumida.
                                - Ao cancelar uma matrícula confirmada, a vaga é liberada.
                                - Um aluno não pode se matricular duas vezes na mesma turma.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Lyceum")
                                .url("https://github.com/lyceum")));
    }

    /**
     * Adds the standard error responses (400, 404, 409, 500) to every operation automatically,
     * so controllers don't need to repeat them for every endpoint.
     */
    @Bean
    public OperationCustomizer globalErrorResponses() {
        return (operation, handlerMethod) -> {
            var responses = operation.getResponses();
            if (responses == null) return operation;

            Schema<?> errorSchema = new Schema<ApiErrorResponse>().$ref("#/components/schemas/ApiErrorResponse");
            var errorContent = new Content().addMediaType(
                    org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                    new MediaType().schema(errorSchema));

            responses.addApiResponse("400", new ApiResponse()
                    .description("Requisição inválida — parâmetro ausente ou mal formatado")
                    .content(errorContent));
            responses.addApiResponse("404", new ApiResponse()
                    .description("Recurso não encontrado")
                    .content(errorContent));
            responses.addApiResponse("409", new ApiResponse()
                    .description("Conflito — violação de regra de negócio")
                    .content(errorContent));
            responses.addApiResponse("500", new ApiResponse()
                    .description("Erro interno inesperado")
                    .content(errorContent));
            return operation;
        };
    }
}
