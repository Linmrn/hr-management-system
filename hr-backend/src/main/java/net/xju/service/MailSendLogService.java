package net.xju.service;

import net.xju.dao.MailSendLogDao;
import net.xju.entity.MailSendLog;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class MailSendLogService {

    private MailSendLogDao mailSendLogDao = new MailSendLogDao();

    // 更新邮件发送状态
    public Integer updateMailSendLogStatus(String msgId, Integer status) throws SQLException {
        return mailSendLogDao.updateStatus(msgId, status);
    }

    // 插入邮件发送日志
    public Integer insert(MailSendLog mailSendLog) throws SQLException {
        return mailSendLogDao.insert(mailSendLog);
    }

    // 获取所有状态为发送中的日志（原 getMailSendLogsByStatus，通常用于定时任务重发）
    public List<MailSendLog> getMailSendLogsByStatus() throws SQLException {
        return mailSendLogDao.findByStatus(0);
    }

    // 更新重试次数
    public Integer updateCount(String msgId, Date date) throws SQLException {
        return mailSendLogDao.updateCount(msgId, date);
    }

    // 根据 msgId 查询单条记录（辅助方法，可选择性添加）
    public MailSendLog getMailSendLogById(String msgId) throws SQLException {
        return mailSendLogDao.getByMsgId(msgId);
    }
}
