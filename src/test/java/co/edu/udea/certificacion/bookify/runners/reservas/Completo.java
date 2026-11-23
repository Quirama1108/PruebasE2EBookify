package co.edu.udea.certificacion.bookify.runners.reservas;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
        features = "src/test/resources/features/reservas",
        glue = "co.edu.udea.certificacion.bookify.stepdefinitions",
        snippets = CucumberOptions.SnippetType.CAMELCASE
)
public class Completo {}
