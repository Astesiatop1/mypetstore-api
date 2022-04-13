package org.csu.mypetstore.api.controller;


import org.csu.mypetstore.api.common.CommonResponse;
import org.csu.mypetstore.api.common.ResponseCode;
import org.csu.mypetstore.api.service.AccountService;
import org.csu.mypetstore.api.service.CartService;
import org.csu.mypetstore.api.vo.AccountVO;
import org.csu.mypetstore.api.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/account/")
public class AccountController {

    @Autowired
    private AccountService accountService;
    @Autowired
    private CartService cartService;


    @PostMapping("login")
    @ResponseBody
    public CommonResponse<AccountVO> loginAccount(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session){
        CommonResponse<AccountVO> response = accountService.getAccount(username,password);
        if(response.isSuccess()){
            CartVO cartVO = cartService.getCart(username);
            session.setAttribute("login_account",response.getData());
            session.setAttribute("cartVO",cartVO);
        }
        return response;
    }

    @PostMapping("register")
    @ResponseBody
    public CommonResponse RegisterAccount(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String repeatedPassword,
            @RequestParam String firstname,
            @RequestParam String lastname,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String address1,
            @RequestParam String address2,
            @RequestParam String city,
            @RequestParam String state,
            @RequestParam String zip,
            @RequestParam String country,
            @RequestParam String languagePreference,
            @RequestParam String favouriteCategoryId,
            @RequestParam boolean listOption,
            @RequestParam boolean bannerOption,
            HttpSession session){
        if(!password.equals(repeatedPassword)) {
            return CommonResponse.createForError("两次密码不一致");
        }
        else {
            CommonResponse isExist = accountService.usernameExist(username);
            if (isExist.getMsg().equals("用户名不存在")) {
                AccountVO accountVO = new AccountVO();
                accountVO.setUsername(username);
                accountVO.setPassword(password);
                accountVO.setAddress1(address1);
                accountVO.setAddress2(address2);
                accountVO.setEmail(email);
                accountVO.setFirstName(firstname);
                accountVO.setLastName(lastname);
                accountVO.setCity(city);
                accountVO.setCountry(country);
                accountVO.setStatus("");
                accountVO.setState(state);
                accountVO.setZip(zip);
                accountVO.setPhone(phone);

                accountVO.setLanguagePreference(languagePreference);
                accountVO.setFavouriteCategoryId(favouriteCategoryId);
                accountVO.setBannerOption(bannerOption);
                accountVO.setListOption(listOption);
                CommonResponse response = accountService.insertAccount(accountVO);
                return response;
            } else if (isExist.getMsg().equals("用户名已存在")) {
                return CommonResponse.createForError("用户名已存在");
            } else {
                return CommonResponse.createForError("服务器异常");
            }
        }
    }

    @PostMapping("/signoff")
    @ResponseBody
    public CommonResponse signofftAccount(HttpSession session) {
        session.setAttribute("login_account", null);
        // 上线合并购物车就进行了持久化，登录状态下每添一个商品也会进行持久化
        // 退出则无需持久化购物车，只需重置购物车即可

        return CommonResponse.createForSuccessMessage("用户已经登出");
    }

    @PostMapping("/update")
    @ResponseBody
    public CommonResponse UpdateAccount(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String repeatedPassword,
            @RequestParam String firstname,
            @RequestParam String lastname,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String address1,
            @RequestParam String address2,
            @RequestParam String city,
            @RequestParam String state,
            @RequestParam String zip,
            @RequestParam String country,
            @RequestParam String languagePreference,
            @RequestParam String favouriteCategoryId,
            @RequestParam boolean listOption,
            @RequestParam boolean bannerOption,
            HttpSession session){

        if(!password.equals(repeatedPassword)) {
            return CommonResponse.createForError("两次密码不一致");
        }
        AccountVO accountVO = new AccountVO();
        accountVO.setUsername(username);
        accountVO.setPassword(password);
        accountVO.setAddress1(address1);
        accountVO.setAddress2(address2);
        accountVO.setEmail(email);
        accountVO.setFirstName(firstname);
        accountVO.setLastName(lastname);
        accountVO.setCity(city);
        accountVO.setCountry(country);
        accountVO.setStatus("");
        accountVO.setState(state);
        accountVO.setZip(zip);
        accountVO.setPhone(phone);

        accountVO.setLanguagePreference(languagePreference);
        accountVO.setFavouriteCategoryId(favouriteCategoryId);
        accountVO.setBannerOption(bannerOption);
        accountVO.setListOption(listOption);
        CommonResponse response = accountService.updateAccount(accountVO);

        session.setAttribute("loginAccount",accountVO);

        return response;
    }


    @PostMapping("get_login_account_info")
    @ResponseBody
    public CommonResponse<AccountVO> getLoginAccountInfo(HttpSession session){
        AccountVO loginAccount = (AccountVO)session.getAttribute("login_account");
        if(loginAccount != null){
            return  CommonResponse.createForSuccess(loginAccount);
        }
        return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(),"用户为登录，不饿能获取用户信息");
    }

}
