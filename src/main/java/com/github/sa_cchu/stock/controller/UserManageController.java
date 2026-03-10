package com.github.sa_cchu.stock.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.sa_cchu.stock.dto.UserListDTO;
import com.github.sa_cchu.stock.entity.User;
import com.github.sa_cchu.stock.service.CustomUserDetailsService;
import com.github.sa_cchu.stock.service.ShopService;
import com.github.sa_cchu.stock.service.WarehouseService;

@Controller
@RequestMapping("/user/manage")
public class UserManageController {

    private final CustomUserDetailsService userDetailsService;
    private final ShopService shopService;
    private final WarehouseService warehouseService;

    // コンストラクタインジェクション（これがないと final フィールドが初期化されずエラーになります）
    public UserManageController(CustomUserDetailsService userDetailsService,
                                ShopService shopService,
                                WarehouseService warehouseService) {
        this.userDetailsService = userDetailsService;
        this.shopService = shopService;
        this.warehouseService = warehouseService;
    }

    /**
     * 一般管理者（SHOP/WAREHOUSE）専用のユーザー一覧画面
     */
    @GetMapping
    public String list(@AuthenticationPrincipal User operator,
                       @RequestParam(name = "belongingId", required = false) Integer belongingId,
                       Model model) {

        // 1. サービス層で「自分と同じ権限」のユーザーだけを DTO のリストで取得
        // operator を渡すことで、Service側で権限によるフィルタリングを行います
        List<UserListDTO> dtoList = userDetailsService.getMyTeamUserList(operator, belongingId);

        // 2. プルダウン用の「所属リスト」を準備
        // HTML側で ${belongings} という共通の名前で扱えるように Model に積みます
        String authName = operator.getAuthority().getAuthorityName();
        
        if (authName.contains("SHOP")) {
            // 店舗権限なら店舗リストを取得
            model.addAttribute("belongings", shopService.getAllShopDTOs());
        } else if (authName.contains("WAREHOUSE")) {
            // 倉庫権限なら倉庫リストを取得（これで倉庫時もプルダウンが表示されます）
            model.addAttribute("belongings", warehouseService.getAllWarehouseDTOs());
        }

        // 3. 画面に渡すデータをセット
        model.addAttribute("userList", dtoList);      // テーブル用
        model.addAttribute("selectedId", belongingId); // 選択状態の保持用
        
        // ※ loginUser は GlobalControllerAdvice が自動でセットするため、ここでは書きません

        return "myTeamMG"; // resources/templates/myTeamMG.html を呼び出す
    }
}