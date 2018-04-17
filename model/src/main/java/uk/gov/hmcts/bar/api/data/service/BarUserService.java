package uk.gov.hmcts.bar.api.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.bar.api.data.model.BarUser;
import uk.gov.hmcts.bar.api.data.repository.BarUserRepository;

import java.util.Optional;

@Service
@Transactional
public class BarUserService {

    private final BarUserRepository barUserRepository;

    @Autowired
    public BarUserService(BarUserRepository barUserRepository){
        this.barUserRepository = barUserRepository;
    }

    public BarUser checkUser(BarUser barUser){
        Optional<BarUser> existingUser = barUserRepository.findBarUserByIdamId(barUser.getIdamId());
        return existingUser.filter(barUser1 -> barUser1.equals(barUser))
            .orElseGet(() -> barUserRepository.save(barUser));
    }

    public Optional<BarUser> findBarUserByIdamId(String idamId) {
        return barUserRepository.findBarUserByIdamId(idamId);
    }
}
