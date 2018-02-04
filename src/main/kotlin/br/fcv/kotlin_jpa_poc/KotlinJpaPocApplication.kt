package br.fcv.kotlin_jpa_poc

import com.fasterxml.jackson.databind.ObjectMapper
import org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.ParameterizedTypeReference
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.QueryHints
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.http.HttpOutputMessage
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.http.server.ServletServerHttpResponse
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.io.OutputStream
import java.time.LocalDate
import java.util.stream.Stream
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.QueryHint
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@EnableSwagger2
@SpringBootApplication
// @Import(SpringDataRestConfiguration::class)
// throws a NoSuchMethodError on Spring Boot 2
//   > Caused by: java.lang.NoSuchMethodError: org.springframework.data.repository.support.Repositories.getRepositoryInformationFor(Ljava/lang/Class;)Lorg/springframework/data/repository/core/RepositoryInformation;
//   >     at springfox.documentation.spring.data.rest.EntityServicesProvider.requestHandlers(EntityServicesProvider.java:81) ~[springfox-data-rest-2.8.0.jar:2.8.0]
class KotlinJpaPocApplication {

    @Bean
    fun populate(personRepository: PersonRepository) = CommandLineRunner {

        listOf(Person(name = "Ozzy Osbourne", birthday = LocalDate.of(1948, 12, 3)),
                Person(name = "John Lennon", birthday = LocalDate.of(1940, 10, 9)))
                .map { personRepository.save(it) }
                .forEach { logger.info("Just saved {}", it) }
    }

    companion object {
        val logger = LoggerFactory.getLogger(KotlinJpaPocApplication::class.java)
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(KotlinJpaPocApplication::class.java, *args)
}

@RestController
@RequestMapping("my-controller/people")
class PersonController(val repo: PersonRepository, val mapper: ObjectMapper) {

    @GetMapping
    fun findAll(): List<Person> {
        logger.debug("findAll with List");
        return repo.findAll().toList()
    }

    @Transactional(readOnly = true)
    @GetMapping(params = ["stream"])
    fun _findAll(resp: HttpServletResponse) {

        logger.debug("findAll with Stream")

        val stream = repo.readAll()

        stream.use {

            val converter = MappingJackson2HttpMessageConverter(mapper)

            val t = object : ParameterizedTypeReference<Stream<Person>>() {};
            val type = t.type

            val output: HttpOutputMessage = ServletServerHttpResponse(resp);

            converter.write(stream, type, MediaType.APPLICATION_JSON, output);
        }
    }

    companion object {
        val logger = LoggerFactory.getLogger(PersonController::class.java)
    }

}



@RepositoryRestResource(collectionResourceRel = "people", path = "people")
interface PersonRepository : JpaSpecificationExecutor<Person>, CrudRepository<Person, Long> {

    // based on https://knes1.github.io/blog/2015/2015-10-19-streaming-mysql-results-using-java8-streams-and-spring-data.html
    // see also https://www.airpair.com/java/posts/spring-streams-memory-efficiency
    @Transactional(readOnly = true)
    @QueryHints(value = [(QueryHint(name = HINT_FETCH_SIZE, value = "1000"))])
    @Query("select p from Person p")
    fun readAll(): Stream<Person>

}

@Entity
data class Person(@Id @GeneratedValue val id: Long? = null, var name: String, var birthday: LocalDate, var noise: String = "")