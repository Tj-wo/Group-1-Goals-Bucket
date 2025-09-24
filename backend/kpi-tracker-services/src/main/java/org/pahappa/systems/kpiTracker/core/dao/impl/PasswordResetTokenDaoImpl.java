package org.pahappa.systems.kpiTracker.core.dao.impl;

import com.googlecode.genericdao.search.Search;
import org.pahappa.systems.kpiTracker.core.dao.impl.BaseDAOImpl;
import org.pahappa.systems.kpiTracker.core.dao.PasswordResetTokenDao;
import org.pahappa.systems.kpiTracker.models.security.PasswordResetToken;
import org.sers.webutils.model.security.User;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * DAO implementation for PasswordResetToken operations
 */
@Repository("passwordResetTokenDao")
public class PasswordResetTokenDaoImpl extends BaseDAOImpl<PasswordResetToken> implements PasswordResetTokenDao {

    @Override
    public PasswordResetToken findByToken(String token) {
        Search search = new Search();
        search.addFilterEqual("token", token);
        return (PasswordResetToken) searchUnique(search);
    }

    @Override
    public PasswordResetToken findByUser(User user) {
        Search search = new Search();
        search.addFilterEqual("user", user);
        return (PasswordResetToken) searchUnique(search);
    }

    @Override
    public void deleteExpiredTokens() {
        Search search = new Search();
        search.addFilterLessThan("expiryDate", new Date());
        List<PasswordResetToken> expiredTokens = search(search);

        for (PasswordResetToken token : expiredTokens) {
            remove(token);
        }
    }
}
