package br.fcv.kotlin_jpa_poc

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

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

@RepositoryRestResource(collectionResourceRel = "people", path = "people")
interface PersonRepository : JpaSpecificationExecutor<Person>, CrudRepository<Person, Long>

@Entity
data class Person(@Id @GeneratedValue val id: Long? = null, var name: String, var birthday: LocalDate, var noise: String = "")