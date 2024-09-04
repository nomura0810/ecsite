package jp.co.internous.ecsite.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.co.internous.ecsite.model.domain.MstGoods;
import jp.co.internous.ecsite.model.domain.MstUser;
import jp.co.internous.ecsite.model.form.GoodsForm;
import jp.co.internous.ecsite.model.form.LoginForm;
import jp.co.internous.ecsite.model.mapper.MstGoodsMapper;
import jp.co.internous.ecsite.model.mapper.MstUserMapper;

@Controller
@RequestMapping("/ecsite/admin")
public class AdminController {
	
	@Autowired
	private MstUserMapper userMapper;
	
	@Autowired
	private MstGoodsMapper goodsMapper;
	
	@RequestMapping("/")
	public String index() {
		return "admintop";
	}
	
	@PostMapping("/welcome")
	//welcomメソッドは「ログイン時」に呼び出される
	
	public String welcome(LoginForm form, Model model) {
	//ブラウザで入力された項目が自動的にFormクラスの各フィールドに格納される	
		
		MstUser user = userMapper.findByUserNameAndPassword(form);
		//MstUserMapperのfindByUserNameAndPasswordメソッドを指す
		
		//ユーザー検索結果を判定。null(ヒットしなかった)場合、ブロック内の処理が実行され、forwordによりトップページに遷移
		if (user == null) {
			model.addAttribute("errMessage","ユーザー名またはパスワードが違います。");
			return "forward:/ecsite/admin/";
		}
		
		//ログインしたユーザーが管理者かどうかを判定
		if (user.getIsAdmin() == 0) {
			model.addAttribute("errMessage","管理者ではありません。");
			return "forword:/ecsite/admin/";
		}
		
		//ここに到達した時点で「ログイン成功」「管理者でログイン」の条件が保証されている
		//MstGoodsMapperのfindAllメソッドによって、商品情報を全て検索し、HtMLに渡す情報をmodelに登録している
		List<MstGoods> goods = goodsMapper.findAll();
		model.addAttribute("userName",user.getUserName());
		model.addAttribute("password",user.getPassword());
		model.addAttribute("goods",goods);
		
		return "welcome";
		//welcom.htmlに遷移させる
	}
	
	
	@PostMapping("/goodsMst")
	public String goodsMst(LoginForm f, Model m) {
		m.addAttribute("userName", f.getUserName());
		m.addAttribute("password",f.getPassword());
		
	return "goodsmst";
	}
	
	
	@PostMapping("/addGoods")
	public String addGoods(GoodsForm goodsForm, LoginForm loginForm, Model m) {
		m.addAttribute("userName",loginForm.getUserName());
		m.addAttribute("password",loginForm.getPassword());
		
		MstGoods goods = new MstGoods();
		goods.setGoodsName(goodsForm.getGoodsName());
		goods.setPrice(goodsForm.getPrice());
		
		goodsMapper.insert(goods);
		
		return "forward:/ecsite/admin/welcome";
	}
	
	@ResponseBody
	@PostMapping("/api/deleteGoods")
	public String deleteApi(@RequestBody GoodsForm f,Model m) {
		try {
			goodsMapper.deleteById(f.getId());
		} catch(IllegalArgumentException e) {
			return "-1"; //例外がcatchされた場合には、「処理が失敗した」印として"－1"を返却
		}
		
		return "1";  //例外が起きず、ここまで到達できれば「処理が成功した」印として"1"を返却
	}
	
	
	
}
