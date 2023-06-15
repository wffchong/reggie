package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    // 登录
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("账号密码: {}, {}", employee.getUsername(), employee.getPassword());

        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        if (emp == null) {
            return R.error("登录失败");
        }

        if (!emp.getPassword().equals(password)) {
            return R.error("登录失败");
        }

        if (emp.getStatus() == 0) {
            return R.error("该账号已禁用");
        }

        // 登录成功
        request.getSession().setAttribute("employee", emp.getId());

        return R.success(emp);
    }


    // 退出登录
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        // 清理员工的session保存中的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }
}
