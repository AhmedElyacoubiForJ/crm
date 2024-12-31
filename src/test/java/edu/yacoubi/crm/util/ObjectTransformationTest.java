package edu.yacoubi.crm.util;

import lombok.Builder;
import lombok.Data;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ObjectTransformationTest {

    @Test
    public void testPersonToPersonDTOTransformation() {
        // Given
        // bereitet die Ausgangssituation vor,
        // indem ein Person-Objekt erstellt und ein Transformer definiert wird.
        Person person = Person.builder()
                .name("John Doe")
                .age(25)
                .build();

        ITransformer<Person, PersonDTO> personToPersonDTO = p -> {
            String ageCategory = person.getAge() > 18 ? "Adult" : "Minor";
            return PersonDTO.builder()
                    .fullName(person.getName())
                    .ageCategory(ageCategory)
                    .build();
        };

        // When
        // führt die eigentliche Transformationsaktion durch
        PersonDTO personDTO = TransformerUtil.transform(personToPersonDTO, person);
        PersonDTO transformedPerson = personToPersonDTO.transform(person);

        // Then
        // überprüft die Ergebnisse, um sicherzustellen,
        // dass die Transformation korrekt durchgeführt wurde
        assertEquals("John Doe", personDTO.getFullName());
        assertEquals("Adult", personDTO.getAgeCategory());
        assertEquals("John Doe", transformedPerson.getFullName());
        assertEquals("Adult", transformedPerson.getAgeCategory());
    }

    @Data
    @Builder
    static class Person {
        private String name;
        private int age;
    }

    @Data
    @Builder
    static class PersonDTO {
        private String fullName;
        private String ageCategory;

        @Override
        public String toString() {
            return "PersonDTO{fullName='" + fullName + "', ageCategory='" + ageCategory + "'}";
        }
    }
}

