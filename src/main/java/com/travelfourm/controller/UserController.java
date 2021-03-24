package com.travelfourm.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.travelfourm.Util.CommunityConstant;
import com.travelfourm.Util.CommunityUtil;
import com.travelfourm.Util.HostHolder;
import com.travelfourm.annotation.LoginRequired;
import com.travelfourm.entity.*;
import com.travelfourm.service.*;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.omg.CORBA.OBJ_ADAPTER;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    private static final Logger logger= LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private CommentService commentService;

    @Value("${qiniu.key.access}")
    private String accessKey;

    @Value("${qiniu.key.secret}")
    private String secretKey;

    @Value("${qiniu.bucket.header.name}")
    private String headerBucketName;

    @Value("${qiniu.bucket.header.url}")
    private String headerBucketUrl;

    @LoginRequired
    @GetMapping("/setting")
    public String getSettingPage(Model model){
        //上传名称
        String fileName = CommunityUtil.generateUUID();

        //设置响应信息
        StringMap policy = new StringMap();
        policy.put("returnBody",CommunityUtil.getJsonString(0));

        //生成上传凭证
        Auth auth = Auth.create(accessKey,secretKey);
        String uploadToken = auth.uploadToken(headerBucketName,fileName,3600,policy);

        model.addAttribute("uploadToken",uploadToken);
        model.addAttribute("fileName",fileName);

        return "/site/setting";
    }


    //更新头像路径
    @PostMapping("/header/url")
    @ResponseBody
    public String updateHeaderUrl(String fileName){
        if (StringUtils.isBlank(fileName)){
            return CommunityUtil.getJsonString(1,"文件名不能为空！");
        }

        String url = headerBucketUrl + "/" + fileName;
        userService.updateHeader(hostHolder.getUser().getId(),url);

        return CommunityUtil.getJsonString(0);
    }


    /**
     * upload到本地头像的方法暂时废弃*/
    @LoginRequired
    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if (headerImage == null){
            model.addAttribute("error","您还没有选择图片,请选择图片");
            return "/site/setting";
        }

        String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)){
            model.addAttribute("error","请选择文件格式为jpeg、png、jpg的图片");
            return "/site/setting";
        }

        //生成随机文件名
        filename = CommunityUtil.generateUUID() + suffix;
        //确定文件存放位置
        File destination = new File(uploadPath + "/" + filename);
        try {
            headerImage.transferTo(destination);
        }catch (IOException e){
            logger.error("上传文件失败：" + e.getMessage() );
            throw new RuntimeException("上传文件失败,服务器发生异常!",e);
        }

        //更新当前用户的头像的路径（web访问的路径）
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + filename;
        userService.updateHeader(user.getId(),headerUrl);

        return "redirect:/index";
    }

    //上传文件到七牛云服务器中
    @GetMapping("/header/{filename}")
    public void getHeader(@PathVariable("filename")String filename, HttpServletResponse response) {
        //服务器存放的位置
        filename = uploadPath + "/" + filename;
        //文件后缀
        String suffix = filename.substring(filename.lastIndexOf("."));
        //响应图片
        response.setContentType("image/" + suffix);
        try (OutputStream outputStream = response.getOutputStream();
             FileInputStream fileInputStream = new FileInputStream(filename);){
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fileInputStream.read(buffer)) != -1){
                outputStream.write(buffer,0,b);
            }
        }catch (IOException e){
            logger.error("读取头像失败:" + e.getMessage());
        }
    }

    //个人主页
    @GetMapping("/profile/{userId}")
    public String getProfilePage(@PathVariable("userId")int userId,Model model){
        User user = userService.findUserById(userId);
        if (user == null){
            throw new RuntimeException("该用户不存在！");
        }

        //用户
        model.addAttribute("user",user);

        //点赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",likeCount);

        //关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount",followeeCount);

        //粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount",followerCount);

        //是否已关注
        boolean hasFollowed = false;
            if( hostHolder.getUser() != null){
                hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);
            }
            model.addAttribute("hasFollowed",hasFollowed);

        return "/site/profile";
    }

    //我的帖子
    @GetMapping("/profile/{userId}/minepost")
    public String getProfileMinePost(@PathVariable("userId")int userId, Model model, Page page){

        //先找出这个user的信息
        User user = userService.findUserById(userId);

        if (user == null){
            throw new RuntimeException("该用户不存在！");
        }

        //如果session中存取的holder的user不等于从数据库取出来的user信息，则跳转到index页面
        if (hostHolder.getUser() == null && userId != hostHolder.getUser().getId()){
            return "redirect:/index";
        }

        //查找我的帖子的总数
        int postCount = discussPostService.findDiscussPostRows(userId);
        page.setRows(postCount);

        //分页信息
        page.setPath("/user/profile/" + userId + "/minepost");
        List<DiscussPost> list = discussPostService.findDiscussPosts(userId,page.getOffset(),page.getLimit(),0);

        List<Map<String , Object>> discussPosts = new ArrayList<>();
        if (list != null){
            for (DiscussPost post :list){
                Map<String ,Object> map = new HashMap<>();
                map.put("post",post);

                user = userService.findUserById(post.getUserId());
                map.put("user",user);

                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId());
                map.put("likeCount",likeCount);
                discussPosts.add(map);
            }
        }

        model.addAttribute("postCount",postCount);
        model.addAttribute("userId",userId);
        model.addAttribute("discussPosts",discussPosts);
        return "/site/my-post";
    }

    //我回复的帖子
    @GetMapping("/profile/{userId}/minereply")
    public String getProfileMineReply(@PathVariable("userId")int userId,Model model,Page page){
        User user = userService.findUserById(userId);

        if (user == null){
            throw new RuntimeException("该用户不存在！");
        }

        if (hostHolder.getUser() == null || userId != hostHolder.getUser().getId()){
            return "redirect:/index";
        }

        page.setPath("/user/profile/" + userId + "/minereply");

        //我的回复数量统计
        page.setRows(commentService.findCommentCount(userId));

        List<Comment> commentList = commentService.findCommentsByUserId(userId,page.getOffset(),page.getLimit());

        //回复列表
        List<Map<String ,Object>> commentVoList = new ArrayList<>();
        if (commentList != null){
            for (Comment comment:commentList){
                //评论VO
                Map<String,Object> commentVo = new HashMap<>();

                //评论
                if (comment.getEntityType() == ENTITY_TYPE_POST){
                    commentVo.put("comment",comment);
                    DiscussPost discussPost = discussPostService.findDiscussPostById(comment.getEntityId());
                    commentVo.put("post",discussPost);
                    commentVoList.add(commentVo);
                }
            }
        }
        model.addAttribute("comments",commentVoList);
        model.addAttribute("commentCount",commentService.findCommentCount(userId));
        return "/site/my-reply";
    }

    //表格跳转页面
    @GetMapping("/showLayui")
    public String showAllUserLayui(){
        return "/backup/showAllUser";
    }

    //用户信息接口
    @GetMapping("/showAllUser")
    @ResponseBody
    public Map<String ,Object>  showAllUser(@RequestParam(required = false,defaultValue = "0")String type,
                                            @RequestParam(required = false,defaultValue = "")String content,
                                            @RequestParam(required = false,defaultValue = "1")int page,
                                            @RequestParam(required = false,defaultValue = "10")int limit){
        //开始分页
        PageHelper.startPage(page,limit);

        //分页查询
        List<User> list = new ArrayList<>();
        if (type.equals("0")){
            list = userService.findAllUser();
        }
        //封装数据
        PageInfo pageInfo = new PageInfo(list);
        Map<String ,Object> map = new HashMap<>();
        map.put("code",0);
        map.put("msg","查询成功");
        map.put("count",pageInfo.getTotal());
        map.put("data",pageInfo.getList());
        return map;
    }

}
