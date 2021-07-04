package com.adamluptakosice.usercrdmin.dao;

import com.adamluptakosice.usercrdmin.domain.User;
import com.adamluptakosice.usercrdmin.exception.UnableFindResourceException;
import com.adamluptakosice.usercrdmin.exception.UnableToDeleteResourceException;
import com.adamluptakosice.usercrdmin.exception.UnableToSaveResourceException;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDaoH2 implements UserDao {
    private static final Logger LOGGER = Logger.getLogger(UserDaoH2.class);
    private static final String SQL_INSERT = "INSERT INTO SUSERS (ID, GUID, NAME) VALUES (?,?,?)";
    private static final String SQL_DELETE_ALL = "DELETE FROM SUSERS";
    private static final String SQL_FIND_ONE = "SELECT * FROM SUSERS WHERE ID = ?";
    private static final String SQL_FIND_ALL = "SELECT * FROM SUSERS";

    private final DataSource dataSource;

    public UserDaoH2(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public User save(User user) {
        try (var conn = dataSource.getConnection();
             var preparedStatement = conn.prepareStatement(SQL_INSERT)) {

            preparedStatement.setLong(1, user.getId());
            preparedStatement.setString(2, user.getGuid());
            preparedStatement.setString(3, user.getName());
            preparedStatement.executeUpdate();

            return user;
        } catch (Exception e) {
            throw new UnableToSaveResourceException("Unable to store %s".formatted(user.toString()), e);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        Optional<User> result = Optional.empty();

        try (var con = dataSource.getConnection();
             var pst = con.prepareStatement(SQL_FIND_ONE)) {
            pst.setLong(1, id);

            var rs = pst.executeQuery();
            while (rs.next()) {
                var user = new User();
                user.setId(rs.getLong("id"));
                user.setGuid(rs.getString("guid"));
                user.setName(rs.getString("name"));
                result = Optional.of(user);
            }
        } catch (Exception ex) {
            throw new UnableFindResourceException("Can't find user cause", ex);
        }
        return result;
    }

    @Override
    public Integer deleteAll() {
        try (var conn = dataSource.getConnection();
             var preparedStatement = conn.prepareStatement(SQL_DELETE_ALL)) {
            var deletedCount = preparedStatement.executeUpdate();
            return deletedCount;
        } catch (Exception ex) {
            throw new UnableToDeleteResourceException("Unable to delete users", ex);
        }
    }

    @Override
    public List<User> findAll() {
        var userList = new ArrayList<User>();
        try (var con = dataSource.getConnection();
             var pst = con.prepareStatement(SQL_FIND_ALL);
             var rs = pst.executeQuery()) {
            User user;
            while (rs.next()) {
                user = new User();
                user.setId(rs.getLong("id"));
                user.setGuid(rs.getString("guid"));
                user.setName(rs.getString("name"));
                userList.add(user);
            }
        } catch (Exception ex) {
            throw new UnableFindResourceException("Unable to find resource", ex);
        }
        return userList;
    }
}
