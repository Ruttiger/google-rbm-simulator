package win.agus4the.rbm.simulator.service.pcm;

import org.springframework.stereotype.Service;
import win.agus4the.rbm.simulator.model.pcm.PCMEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PCMEventParser {

    private static final Pattern EVENT_PATTERN =
            Pattern.compile("#(DELIVERED|REJECTED|EXPIRED)(?:\\(delay=(\\d+)\\))?");

    public List<PCMEvent> parse(String smsText) {
        List<PCMEvent> list = new ArrayList<>();

        Matcher m = EVENT_PATTERN.matcher(smsText);

        while (m.find()) {
            String type = m.group(1);
            long delay = m.group(2) != null ? Long.parseLong(m.group(2)) : 0;
            list.add(new PCMEvent(type, delay));
        }

        return list;
    }

}
