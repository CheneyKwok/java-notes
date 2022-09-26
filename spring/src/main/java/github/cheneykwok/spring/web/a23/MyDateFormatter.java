package github.cheneykwok.spring.web.a23;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Slf4j
public class MyDateFormatter implements Formatter<Date> {

    private final String desc;

    public MyDateFormatter(String desc) {
        this.desc = desc;
    }


    @Override
    public Date parse(String text, Locale locale) throws ParseException {
        log.debug(">>>>>> 进入了: {}", desc);
        return new SimpleDateFormat("yyyy|MM|dd").parse(text);
    }

    @Override
    public String print(Date date, Locale locale) {
        return new SimpleDateFormat("yyyy|MM|dd").format(date);
    }
}
