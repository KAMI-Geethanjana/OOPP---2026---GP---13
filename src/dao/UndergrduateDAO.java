package dao;

import common.config.DBConnection;
import common.model.Course;
import common.model.Medical;
import common.model.Result;
import common.model.Timetable;
import model.Undergraduate;
import tg2082.model.Undergraduate;
import tg2088.model.CourseMaterial;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UndergrduateDAO {

    public Undergraduate getUndergraduateByUserId(int userId) {
        String sql = "SELECT ug.student_id, ug.reg_no, ug.batch_year, ug.status, ug.department_id, ug.contact_no, " +
                "d.department_name, u.user_id, u.username, u.password_hash, u.full_name, u.email, u.phone_no " +
                "FROM undergraduate ug " +
                "JOIN users u ON ug.user_id = u.user_id " +
                "JOIN department d ON ug.department_id = d.department_id " +
                "WHERE ug.user_id = ?";

    }
