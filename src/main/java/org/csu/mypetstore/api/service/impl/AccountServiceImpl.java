package org.csu.mypetstore.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.csu.mypetstore.api.common.CommonResponse;
import org.csu.mypetstore.api.entity.*;
import org.csu.mypetstore.api.persistence.AccountMapper;
import org.csu.mypetstore.api.persistence.BannerDataMapper;
import org.csu.mypetstore.api.persistence.ProfileMapper;
import org.csu.mypetstore.api.persistence.SignOnMapper;
import org.csu.mypetstore.api.service.AccountService;
import org.csu.mypetstore.api.vo.AccountVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;


@Service("accountService")
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private BannerDataMapper bannerDataMapper;
    @Autowired
    private ProfileMapper profileMapper;
    @Autowired
    private SignOnMapper signOnMapper;

    @Override
    public CommonResponse<AccountVO> getAccount(String username, String password) {
        QueryWrapper<SignOn> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        queryWrapper.eq("password",password);

        SignOn signOn = signOnMapper.selectOne(queryWrapper);
        if(signOn == null){
            return CommonResponse.createForError("用户名或密码不正确");
        }
        return getAccount(username);
    }

    @Override
    public CommonResponse usernameExist(String username) {
        try {
            Account account = accountMapper.selectById(username);
            if (account == null) {
                return CommonResponse.createForSuccessMessage("用户名不存在");
            }
            return CommonResponse.createForSuccessMessage("用户名已存在");
        } catch (Exception e) {
            return CommonResponse.createForError("用户名查询出错");
        }
    }

    @Override
    public CommonResponse insertAccount(AccountVO accountVO) {
        Account account = new Account();
        account.setUsername(accountVO.getUsername());
        account.setEmail(accountVO.getEmail());
        account.setFirstName(accountVO.getFirstName());
        account.setLastName(accountVO.getLastName());
        account.setStatus(accountVO.getStatus());
        account.setAddress1(accountVO.getAddress1());
        account.setAddress2(accountVO.getAddress2());
        account.setCity(accountVO.getCity());
        account.setState(accountVO.getState());
        account.setZip(accountVO.getZip());
        account.setCountry(accountVO.getCountry());
        account.setPhone(accountVO.getPhone());

        Profile profile = new Profile();
        profile.setUsername(accountVO.getUsername());
        profile.setLanguagePreference(accountVO.getLanguagePreference());
        profile.setFavouriteCategoryId(accountVO.getFavouriteCategoryId());
        if(accountVO.isBannerOption()) {
            profile.setBannerOption(1);
        }
        else {
            profile.setBannerOption(0);
        }
        if(accountVO.isListOption()) {
            profile.setListOption(1);
        }
        else{
            profile.setBannerOption(0);
        }

        SignOn signOn = new SignOn();
        signOn.setUsername(accountVO.getUsername());
        signOn.setPassword(accountVO.getPassword());

        try {
            accountMapper.insert(account);
            profileMapper.insert(profile);
            signOnMapper.insert(signOn); // 明文密码，未加密
            return CommonResponse.createForSuccess("成功创建用户！", accountVO);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponse.createForError("用户创建失败！");
        }
    }

    @Override
    public CommonResponse updateAccount(AccountVO accountVO) {
        Account account = new Account();
        account.setUsername(accountVO.getUsername());
        account.setEmail(accountVO.getEmail());
        account.setFirstName(accountVO.getFirstName());
        account.setLastName(accountVO.getLastName());
        account.setStatus(accountVO.getStatus());
        account.setAddress1(accountVO.getAddress1());
        account.setAddress2(accountVO.getAddress2());
        account.setCity(accountVO.getCity());
        account.setState(accountVO.getState());
        account.setZip(accountVO.getZip());
        account.setCountry(accountVO.getCountry());
        account.setPhone(accountVO.getPhone());

        accountMapper.updateById(account);

        Profile profile = new Profile();

        profile.setUsername(accountVO.getUsername());
        profile.setLanguagePreference(accountVO.getLanguagePreference());
        profile.setFavouriteCategoryId(accountVO.getFavouriteCategoryId());
        if(accountVO.isBannerOption()) {
            profile.setBannerOption(1);
        }
        else {
            profile.setBannerOption(0);
        }
        if(accountVO.isListOption()) {
            profile.setListOption(1);
        }
        else{
            profile.setBannerOption(0);
        }

        profileMapper.updateById(profile);

        SignOn signOn = new SignOn();
        signOn.setUsername(accountVO.getUsername());
        signOn.setPassword(accountVO.getPassword());

        signOnMapper.updateById(signOn);


        return CommonResponse.createForSuccess("用户信息修改成功！",accountVO);
    }

    @Override
    public CommonResponse<AccountVO> getAccount(String username) {

        Account account = accountMapper.selectById(username);
        Profile profile = profileMapper.selectById(username);
        BannerData bannerData = bannerDataMapper.selectById(profile.getFavouriteCategoryId());

        if(account == null){
            return CommonResponse.createForError("获取用户信息失败");
        }

        AccountVO accountVO = acocuntToAccountVO(account,profile,bannerData);
        return CommonResponse.createForSuccess(accountVO);
    }

    private AccountVO acocuntToAccountVO(Account account , Profile profile , BannerData bannerData){
        AccountVO accountVO = new AccountVO();
        accountVO.setUsername(account.getUsername());
        accountVO.setPassword("");
        accountVO.setAddress1(account.getAddress1());
        accountVO.setAddress2(account.getAddress2());
        accountVO.setEmail(account.getEmail());
        accountVO.setFirstName(account.getFirstName());
        accountVO.setLastName(account.getLastName());
        accountVO.setCity(account.getCity());
        accountVO.setCountry(account.getCountry());
        accountVO.setStatus(account.getStatus());
        accountVO.setState(account.getState());
        accountVO.setZip(account.getZip());
        accountVO.setPhone(account.getPhone());


        accountVO.setLanguagePreference(profile.getLanguagePreference());
        accountVO.setBannerOption(profile.getBannerOption()==1);
        accountVO.setListOption(profile.getListOption()==1);

        if(profile.getBannerOption()==1){
            accountVO.setFavouriteCategoryId(profile.getFavouriteCategoryId());
            accountVO.setBannerName(bannerData.getBannerName());
        }
        else {
            accountVO.setFavouriteCategoryId("");
            accountVO.setBannerName("");
        }

        return accountVO;

    }
}
