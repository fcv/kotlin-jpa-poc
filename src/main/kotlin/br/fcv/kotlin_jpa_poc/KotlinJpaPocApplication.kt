package br.fcv.kotlin_jpa_poc

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import springfox.documentation.spring.data.rest.configuration.SpringDataRestConfiguration
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@EnableSwagger2
@SpringBootApplication
@Import(SpringDataRestConfiguration::class)
class KotlinJpaPocApplication {

    @Bean
    fun populate(personRepository: PersonRepository) = CommandLineRunner {

        listOf<Person>(Person(name = "Ozzy Osbourne", birthday = LocalDate.of(1948, 12, 3)),
                    Person(name = "John Lennon", birthday = LocalDate.of(1940, 10, 9)))
                .map { personRepository.save(it) }
                .forEach { println(it) }
    }

}

fun main(args: Array<String>) {
    SpringApplication.run(KotlinJpaPocApplication::class.java, *args)
}

@RepositoryRestResource(collectionResourceRel = "people", path = "people")
interface PersonRepository : JpaSpecificationExecutor<Person>, CrudRepository<Person, Long>

@Entity
data class Person(@Id @GeneratedValue val id: Long? = null, var name: String, var birthday: LocalDate)