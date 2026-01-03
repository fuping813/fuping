import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpImportClient {
    public static void main(String[] args) {
        String url = "http://localhost:8080/ums/api/import/database-course";
        String filePath = "数据库课程--知识点.xlsx";
        
        try {
            // 创建URL对象
            URL apiUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
            
            // 设置请求方法为POST
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            
            // 设置请求头
            String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            
            // 创建文件输入流
            File file = new File(filePath);
            FileInputStream fileInputStream = new FileInputStream(file);
            
            // 获取输出流
            OutputStream outputStream = connection.getOutputStream();
            
            // 写入文件数据
            writeFilePart(outputStream, boundary, file, fileInputStream);
            
            // 结束边界
            outputStream.write(("--" + boundary + "--\r\n").getBytes());
            
            // 刷新并关闭输出流
            outputStream.flush();
            outputStream.close();
            fileInputStream.close();
            
            // 获取响应
            int responseCode = connection.getResponseCode();
            System.out.println("响应代码: " + responseCode);
            
            // 读取响应内容
            java.io.BufferedReader in = new java.io.BufferedReader(
                new java.io.InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            
            // 打印响应内容
            System.out.println("响应内容: " + response.toString());
            
        } catch (IOException e) {
            System.err.println("导入失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void writeFilePart(OutputStream outputStream, String boundary, File file, FileInputStream fileInputStream) throws IOException {
        // 文件部分开始
        outputStream.write(("--" + boundary + "\r\n").getBytes());
        outputStream.write(("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"\r\n").getBytes());
        outputStream.write(("Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet\r\n").getBytes());
        outputStream.write(("\r\n").getBytes());
        
        // 写入文件内容
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        
        // 文件部分结束
        outputStream.write(("\r\n").getBytes());
    }
}