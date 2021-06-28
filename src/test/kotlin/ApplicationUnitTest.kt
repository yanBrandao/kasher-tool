import br.com.brandao.ApplicationFactory
import br.com.brandao.ApplicationMode
import br.com.brandao.parameter.CommandApplication
import br.com.brandao.rest.RestApplication
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ApplicationUnitTest {

    @Test
    fun `applicationFactory#create producer RestApplication when applicationMode is REST`() {
        val application = ApplicationFactory.create(ApplicationMode.REST)
        Assertions.assertEquals(RestApplication::class.java, application.javaClass)
    }

    @Test
    fun `applicationFactory#create producer CommandApplication when applicationMode is COMMAND_LINE`() {
        val application = ApplicationFactory.create(ApplicationMode.COMMAND_LINE)
        Assertions.assertEquals(CommandApplication::class.java, application.javaClass)
    }
}