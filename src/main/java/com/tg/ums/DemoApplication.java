package com.tg.ums;

import com.tg.ums.common.RoleConstants;
import com.tg.ums.entity.base.Role;
import com.tg.ums.service.base.RoleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initRoles(RoleService roleService) {
		return args -> {
			System.out.println("开始初始化角色...");
			
			// 检查并创建systemAdmin角色
			Role systemAdmin = roleService.getRoleByRoleCode(RoleConstants.SYSTEM_ADMIN);
			if (systemAdmin == null) {
				systemAdmin = new Role();
				systemAdmin.setRoleCode(RoleConstants.SYSTEM_ADMIN);
				systemAdmin.setRoleName("系统管理员");
				systemAdmin.setRoleDescription("拥有系统所有权限");
				roleService.saveRole(systemAdmin);
				System.out.println("系统管理员角色创建成功");
			} else {
				System.out.println("系统管理员角色已存在");
			}

			// 检查并创建majorDirector角色
			Role majorDirector = roleService.getRoleByRoleCode(RoleConstants.MAJOR_DIRECTOR);
			if (majorDirector == null) {
				majorDirector = new Role();
				majorDirector.setRoleCode(RoleConstants.MAJOR_DIRECTOR);
				majorDirector.setRoleName("专业主任");
				majorDirector.setRoleDescription("管理课程、章节、培养方案");
				roleService.saveRole(majorDirector);
				System.out.println("专业主任角色创建成功");
			} else {
				System.out.println("专业主任角色已存在");
			}

			// 检查并创建teacher角色
			Role teacher = roleService.getRoleByRoleCode(RoleConstants.TEACHER);
			if (teacher == null) {
				teacher = new Role();
				teacher.setRoleCode(RoleConstants.TEACHER);
				teacher.setRoleName("授课教师");
				teacher.setRoleDescription("管理知识点、查看教学计划");
				roleService.saveRole(teacher);
				System.out.println("授课教师角色创建成功");
			} else {
				System.out.println("授课教师角色已存在");
			}
			
			System.out.println("角色初始化完成");
		};
	}

}
