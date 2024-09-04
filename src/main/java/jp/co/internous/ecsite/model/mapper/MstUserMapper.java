package jp.co.internous.ecsite.model.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
//mybatisがデータベースで自動的にデータをやり取りする

import jp.co.internous.ecsite.model.domain.MstUser;
import jp.co.internous.ecsite.model.form.LoginForm;

@Mapper
public interface MstUserMapper {
	
	@Select(value = "select * from mst_user where user_name = #{userName} and password = #{password}")
	MstUser findByUserNameAndPassword(LoginForm form);
	//Mapperのメソッドが呼ばれると上記のSQLが実行される
	//付随してない場合は、同名のXMLに記述されている
	//実行された結果が呼び出し元に返却される(AdminController)　
}

