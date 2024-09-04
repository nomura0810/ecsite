package jp.co.internous.ecsite.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import jp.co.internous.ecsite.model.domain.MstGoods;
import jp.co.internous.ecsite.model.domain.MstUser;
import jp.co.internous.ecsite.model.dto.HistoryDto;
import jp.co.internous.ecsite.model.form.CartForm;
import jp.co.internous.ecsite.model.form.HistoryForm;
import jp.co.internous.ecsite.model.form.LoginForm;
import jp.co.internous.ecsite.model.mapper.MstGoodsMapper;
import jp.co.internous.ecsite.model.mapper.MstUserMapper;
import jp.co.internous.ecsite.model.mapper.TblPurchaseMapper;

@Controller
@RequestMapping("/ecsite")
// localhost:0808/ecsite/ のURLでアクセスできるよう設定

public class IndexController {

	@Autowired
	private MstGoodsMapper goodsMapper;
	// MstGoodsを介してmst_goodsテーブルにアクセスするためのmapper(DAO)
	// @Autowiredにより自動インスタンス化

	@Autowired
	private MstUserMapper userMapper;
	
	@Autowired
	private TblPurchaseMapper purchaseMapper;
	
	private Gson gson = new Gson();
	//WebサービスAPIとして作成するため、JSON形式を扱えるようGsonをインスタンス化しておく
	
	@GetMapping("/")
	// トップページ(index.html)に遷移するメソッド
	public String index(Model model) {
		List<MstGoods> goods = goodsMapper.findAll();
		model.addAttribute("goods", goods);
		// ・goodsテーブルから取得した商品エンティティの一覧を、フロントに渡すModel二追加する
		return "index";
		// ・return "index"により、index.htmlに遷移する
	}
	
	
	@ResponseBody
	// 文字列そのものが返却される @ResponseBody
	
	@PostMapping("/api/login")
	public String loginApi(@RequestBody LoginForm f) {
		MstUser user = userMapper.findByUserNameAndPassword(f);
		// mst_userテーブルからユーザ名とパスワードによって検索し、結果を取得
		
		if(user == null) {
			user = new MstUser();
			user.setFullName("ゲスト");
		}
		
		return gson.toJson(user);
		//MstUser型のuserをJSON形式の文字列に変換し、画面側に返却
	}
	
	
	@ResponseBody
	@PostMapping("/api/purchase")
	public int purchaseApi(@RequestBody CartForm f) {
		
		f.getCartList().forEach((c) -> {
			int total = c.getPrice() * c.getCount();
			purchaseMapper.insert(f.getUserId(), c.getId(), c.getGoodsName(), c.getCount(), total);
		});
		
		return f.getCartList().size();
	}

	
	@ResponseBody
	@PostMapping("/api/history")
	public String HistoryApi(@RequestBody HistoryForm f) {
		int userId = f.getUserId();
		List<HistoryDto> history = purchaseMapper.findHistory(userId);
		
		return gson.toJson(history);
	}
}