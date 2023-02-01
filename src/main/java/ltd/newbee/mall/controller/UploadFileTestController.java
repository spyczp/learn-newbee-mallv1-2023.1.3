package ltd.newbee.mall.controller;

import ltd.newbee.mall.util.ImageData;
import ltd.newbee.mall.util.ResponseGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;

@Controller
public class UploadFileTestController {

    private static final String FILE_UPLOAD_PATH = "D:\\upload\\";

    @Resource
    private MultipartResolver multipartResolver;

    /**
     * 上传多个文件，且前端标签名不相同
     * 1.判断是否是文件上传请求
     * 2.获取文件
     *   转换请求为MultiPartHttpServletRequest
     *   使用getFileNames获取所有文件的文件名
     *   getFile（文件名）获取文件
     * 3.保存文件
     * @return
     */
    @PostMapping("/uploadFilesByDifferentName")
    @ResponseBody
    public String uploadFilesByDifferentName(HttpServletRequest request){
        //1.判断是否是文件上传请求
        if(!multipartResolver.isMultipart(request)){
            return "请选择文件";
        }
        //转换请求为MultiPartHttpServletRequest
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        Iterator<String> fileNames = multipartHttpServletRequest.getFileNames();
        //创建集合，保存文件
        ArrayList<MultipartFile> multipartFiles = new ArrayList<>();
        //限制每次最多只能上传5个文件
        while (fileNames.hasNext()){
            MultipartFile file = multipartHttpServletRequest.getFile(fileNames.next());
            multipartFiles.add(file);
            if(CollectionUtils.isEmpty(multipartFiles)){
                return "请选择文件";
            }
            if(multipartFiles != null && multipartFiles.size() > 5)
                return "一次最多只能上传5个文件";
        }
        String resultMsg = "上传成功,文件路径如下: <br>";
        //  2.生成文件名的通用方法
        //   ① 先获取文件后缀名
        //   ② 生成 日期格式+随机数+文件后缀名 文件名
        //  3.保存文件
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Random r = new Random();
        for(MultipartFile file: multipartFiles){
            String fileName = file.getOriginalFilename();
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(sdf.format(new Date())).append(r.nextInt(100)).append(suffixName);
            String newFileName = stringBuilder.toString();
            //3.保存文件
            try {
                byte[] bytes = file.getBytes();
                Path path = Paths.get(FILE_UPLOAD_PATH, newFileName);
                Files.write(path, bytes);
                resultMsg += fileName + "--->/upload/" + newFileName + "<br>";
            } catch (IOException e) {
                e.printStackTrace();
                resultMsg += fileName + "文件上传失败 <br>";
            }
        }
        return resultMsg;
    }

    /**
     * 上传多个文件，且前端标签名相同
     * 1.判断上传的文件是否为空
     * 2.判断是否超过文件数量上传限制
     * 3.生成随机文件名（同uploadFile方法）
     * 4.保存文件（同uploadFile方法）
     * @param files
     * @return
     */
    @PostMapping("/uploadFilesBySameName")
    @ResponseBody
    public String uploadFilesBySameName(@RequestPart("files") MultipartFile[] files){
        //1.判断上传的文件是否为空
        if(files == null || files.length == 0){
            return "文件上传异常";
        }
        //2.判断是否超过文件数量上传限制
        if(files.length > 5){
            return "最多上传5个文件";
        }
        //3.生成随机文件名（同uploadFile方法）
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Random r = new Random();
        String newFileName = "";
        //响应内容
        String resultMsg = "文件上传成功, 路径如下: <br>";

        for(MultipartFile file: files){
            //判断文件名是否为空
            String fileName = file.getOriginalFilename();
            if(!StringUtils.hasText(fileName)){
                //表示无文件信息，跳出当前循环
                continue;
            }
            //获取文件后缀名
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            StringBuilder tempName = new StringBuilder();
            tempName.append(sdf.format(new Date())).append(r.nextInt(100)).append(suffixName);
            //生成新文件名
            newFileName = tempName.toString();

            try {
                //保存文件
                byte[] bytes = file.getBytes();
                Path path = Paths.get(FILE_UPLOAD_PATH, newFileName);
                Files.write(path, bytes);
                resultMsg += fileName + " ---> /upload/" + newFileName + "<br>";
            } catch (IOException e) {
                e.printStackTrace();
                resultMsg += fileName + "文件保存失败 <br>";
            }
        }
        return resultMsg;
    }

    /**
     * 文件上传测试
     * 1.判断前端上传的文件是否为空
     * 2.生成文件名的通用方法
     *  ① 先获取文件后缀名
     *  ② 生成 日期格式+随机数+文件后缀名 文件名
     * 3.保存文件
     * @param file
     * @return
     */
    @PostMapping("/uploadFile")
    @ResponseBody
    public String uploadFile(@RequestParam("file")MultipartFile file){

        //1.判断前端上传的文件是否为空
        if(file.isEmpty()){
            return "文件上传失败";
        }
        //① 先获取文件后缀名
        String fileName = file.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        //② 生成 日期格式+随机数+文件后缀名 文件名
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Random r = new Random();
        StringBuilder tempName = new StringBuilder();
        tempName.append(sdf.format(new Date())).append(r.nextInt(100)).append(suffixName);
        String newFileName = tempName.toString();

        try {
            //3.保存文件
            byte[] bytes = file.getBytes();
            Path path = Paths.get(FILE_UPLOAD_PATH, newFileName);
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return "文件上传失败";
        }

        return "文件上传成功, 地址为: /upload/" + newFileName;
    }

    @PostMapping("/uploadImageTest")
    @ResponseBody
    public Object uploadImageTest(@RequestParam("file")MultipartFile file,
                              HttpServletRequest request){

        //1.判断前端上传的文件是否为空
        if(file.isEmpty()){
            return ResponseGenerator.genFailResponse();
        }
        //① 先获取文件后缀名
        String fileName = file.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        //② 生成 日期格式+随机数+文件后缀名 文件名
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Random r = new Random();
        StringBuilder tempName = new StringBuilder();
        tempName.append(sdf.format(new Date())).append(r.nextInt(100)).append(suffixName);
        String newFileName = tempName.toString();

        try {
            //3.保存文件
            byte[] bytes = file.getBytes();
            Path path = Paths.get(FILE_UPLOAD_PATH, newFileName);
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseGenerator.genFailResponse();
        }

        ImageData imageData = new ImageData();
        imageData.setUrl(request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/upload/" + newFileName);

        return ResponseGenerator.genSuccessResponse(imageData);
    }
}
