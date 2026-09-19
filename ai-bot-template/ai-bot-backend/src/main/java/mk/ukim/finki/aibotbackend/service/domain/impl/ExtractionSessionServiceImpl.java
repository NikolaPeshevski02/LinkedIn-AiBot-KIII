package mk.ukim.finki.aibotbackend.service.domain.impl;

import java.util.List;
import java.util.Optional;
import mk.ukim.finki.aibotbackend.model.domain.ExtractionSession;
import mk.ukim.finki.aibotbackend.model.enums.SessionStatus;
import mk.ukim.finki.aibotbackend.model.exception.InvalidSessionStateException;
import mk.ukim.finki.aibotbackend.model.exception.SessionNotFoundException;
import mk.ukim.finki.aibotbackend.repository.ExtractionSessionRepository;
import mk.ukim.finki.aibotbackend.service.domain.ExtractionSessionService;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

@Service
public class ExtractionSessionServiceImpl implements ExtractionSessionService {
    private final ExtractionSessionRepository extractionSessionRepository;

    public ExtractionSessionServiceImpl(ExtractionSessionRepository extractionSessionRepository) {
        this.extractionSessionRepository = extractionSessionRepository;
    }

    @Override
    public List<ExtractionSession> findAll() {
    return extractionSessionRepository.findAll();
    }

    @Override
    public Optional<ExtractionSession> findById(Long id) {
        return  extractionSessionRepository.findById(id);
    }

    @Override
    public ExtractionSession create(ExtractionSession session) {
    return extractionSessionRepository.save(session);
    }

    @Override
    public ExtractionSession start(Long id) {
        // TODO(student): Validate the current status (only CREATED or PAUSED may
        //  start), set the status to RUNNING, stamp startedAt and save.
        ExtractionSession extractionSession = findById(id).orElseThrow (()->new SessionNotFoundException(id));
        if(extractionSession.getStatus()== SessionStatus.CREATED || extractionSession.getStatus()== SessionStatus.PAUSED){
            extractionSession.setStatus(SessionStatus.RUNNING);

        }else{
            throw new InvalidSessionStateException(id,extractionSession.getStatus());
        }
        extractionSession.setStartedAt(java.time.LocalDateTime.now());
        return extractionSessionRepository.save(extractionSession);
    }

    @Override
    public ExtractionSession stop(Long id) {
        ExtractionSession extractionSession = findById(id).orElseThrow (()->new SessionNotFoundException(id));
        if (extractionSession.getStatus()== SessionStatus.RUNNING){
            extractionSession.setStatus(SessionStatus.PAUSED);
        }else{
            throw new InvalidSessionStateException(id,extractionSession.getStatus());
        }

        return extractionSessionRepository.save(extractionSession);

    }

    @Override
    public ExtractionSession complete(Long id) {
        ExtractionSession extractionSession = findById(id).orElseThrow (()->new SessionNotFoundException(id));
        if (extractionSession.getStatus()== SessionStatus.RUNNING){
            extractionSession.setStatus(SessionStatus.COMPLETED);
        }else{
            throw new InvalidSessionStateException(id,extractionSession.getStatus());
        }
        extractionSession.setFinishedAt(java.time.LocalDateTime.now());

        return extractionSessionRepository.save(extractionSession);
    }

    @Override
    public ExtractionSession fail(Long id) {
        ExtractionSession extractionSession = findById(id).orElseThrow (()->new SessionNotFoundException(id));
        extractionSession.setStatus(SessionStatus.FAILED);
        return extractionSessionRepository.save(extractionSession);
    }
}
