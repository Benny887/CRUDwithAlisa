package com.benny.crud.dao;

import com.benny.crud.models.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Neil Alishev
 */
@Component
public class PersonDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PersonDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

//    private static int PEOPLE_COUNT;
//
//    private static final String URL = "jdbc:postgresql://localhost:5432/first_db";
//    private static final String USERNAME = "postgres";
//    private static final String PASSWORD = "postgres";
//
//    private static Connection connection;
//
//    static {
//        try {
//            Class.forName("org.postgresql.Driver");
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//    }


    public List<Person> index() {
        return jdbcTemplate.query("select * from Person", new PersonMapper());
//        List<Person> people = new ArrayList<>();
//
//        try {
//            Statement statement = connection.createStatement();
//            String SQL = "SELECT * FROM Person";
//            ResultSet resultSet = statement.executeQuery(SQL);
//
//            while (resultSet.next()) {
//                Person person = new Person();
//
//                person.setId(resultSet.getInt("id"));
//                person.setName(resultSet.getString("name"));
//                person.setEmail(resultSet.getString("email"));
//                person.setAge(resultSet.getInt("age"));
//
//                people.add(person);
//            }
//
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//
//        return people;
    }

    public Optional<Person> show(String email){
       return jdbcTemplate.query("select * from Person where email=?",new Object[]{email},
                new PersonMapper()).stream().findAny();
    }

    public Person show(int id) {
        return jdbcTemplate.query("select * from Person where id=?",new Object[]{id},new PersonMapper()).
                stream().findAny().orElse(null);
//        Person person = null;
//
//        try {
//            PreparedStatement preparedStatement =
//                    connection.prepareStatement("SELECT * FROM Person WHERE id=?");
//
//            preparedStatement.setInt(1, id);
//
//            ResultSet resultSet = preparedStatement.executeQuery();
//
//            resultSet.next();
//
//            person = new Person();
//
//            person.setId(resultSet.getInt("id"));
//            person.setName(resultSet.getString("name"));
//            person.setEmail(resultSet.getString("email"));
//            person.setAge(resultSet.getInt("age"));
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//
//        return person;
    }

    public void save(Person person) {
        jdbcTemplate.update("insert into Person(name, age, email) values (?,?,?)",
                person.getName(),person.getAge(),person.getEmail());
//        try {
//            PreparedStatement preparedStatement =
//                    connection.prepareStatement("INSERT INTO Person VALUES(4, ?, ?, ?)");
//
//            preparedStatement.setString(1, person.getName());
//            preparedStatement.setInt(2, person.getAge());
//            preparedStatement.setString(3, person.getEmail());
//
//            preparedStatement.executeUpdate();
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
    }

    public void update(int id, Person updatedPerson) {
        jdbcTemplate.update("update Person set name=?,age=?, email=? where id=?",
                updatedPerson.getName(),updatedPerson.getAge(),updatedPerson.getEmail(),id);
//        try {
//            PreparedStatement preparedStatement =
//                    connection.prepareStatement("UPDATE Person SET name=?, age=?, email=? WHERE id=?");
//
//            preparedStatement.setString(1, updatedPerson.getName());
//            preparedStatement.setInt(2, updatedPerson.getAge());
//            preparedStatement.setString(3, updatedPerson.getEmail());
//            preparedStatement.setInt(4, id);
//
//            preparedStatement.executeUpdate();
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
    }

    public void delete(int id) {
        jdbcTemplate.update("delete from Person where id=?",id);
//        PreparedStatement preparedStatement =
//                null;
//        try {
//            preparedStatement = connection.prepareStatement("DELETE FROM Person WHERE id=?");
//
//            preparedStatement.setInt(1, id);
//
//            preparedStatement.executeUpdate();
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
    }

//    тест производительности пакет вставки

    public void testMultipleUpdate(){
       List<Person> people = create1000people();

       for(Person p : people){
           jdbcTemplate.update("insert into Person values (?,?,?,?)",
                   p.getId(),p.getName(),p.getAge(),p.getEmail());
       }
    }

    public void testBatchUpdate(){
        List<Person> people = create1000people();
        jdbcTemplate.batchUpdate("insert into person values(?,?,?,?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                        preparedStatement.setInt(1, people.get(i).getId());
                        preparedStatement.setString(2,people.get(i).getName());
                        preparedStatement.setInt(3,people.get(i).getAge());
                        preparedStatement.setString(4,people.get(i).getEmail());
                    }

                    @Override
                    public int getBatchSize() {
                        return people.size();
                    }
                });
    }

    private List<Person> create1000people() {
        List<Person> people = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            people.add(new Person(i, "Name"+i, 30, "test@"+i+"mail.ru"));
        }
        return people;
    }


}
