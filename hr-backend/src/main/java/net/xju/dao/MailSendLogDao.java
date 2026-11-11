package net.xju.dao;

import net.xju.common.utils.JDBCUtils;
import net.xju.entity.MailSendLog;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class MailSendLogDao {

    private QueryRunner runner = new QueryRunner(JDBCUtils.getDataSource());

    public int updateStatus(String msgId, Integer status) throws SQLException {
        String sql = "UPDATE mail_send_log SET status = ? WHERE msgId = ?";
        return runner.update(sql, status, msgId);
    }

    public int insert(MailSendLog mailSendLog) throws SQLException {
        String sql = "INSERT INTO mail_send_log (msgId, empId, status, routeKey, exchange, count, tryTime, createTime, updateTime) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return runner.update(sql,
                mailSendLog.getMsgId(),
                mailSendLog.getEmpId(),
                mailSendLog.getStatus(),
                mailSendLog.getRouteKey(),
                mailSendLog.getExchange(),
                mailSendLog.getCount(),
                mailSendLog.getTryTime(),
                mailSendLog.getCreateTime(),
                mailSendLog.getUpdateTime());
    }

    public List<MailSendLog> findByStatus(int status) throws SQLException {
        String sql = "SELECT * FROM mail_send_log WHERE status = ?";
        return runner.query(sql, new BeanListHandler<>(MailSendLog.class), status);
    }

    public int updateCount(String msgId, Date date) throws SQLException {
        String sql = "UPDATE mail_send_log SET count = count + 1, tryTime = ? WHERE msgId = ?";
        return runner.update(sql, date, msgId);
    }

    public MailSendLog getByMsgId(String msgId) throws SQLException {
        String sql = "SELECT * FROM mail_send_log WHERE msgId = ?";
        return runner.query(sql, new BeanHandler<>(MailSendLog.class), msgId);
    }
}
