package lk.ijse.pos.controller.servlet;

import lk.ijse.pos.bo.BOFactory;
import lk.ijse.pos.bo.custom.OrderDetailBO;
import lk.ijse.pos.dto.OrderDetailDTO;
import lk.ijse.pos.util.MessageUtil;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet(urlPatterns = "/order_detail")
public class OrderDetailServlet extends HttpServlet {
    OrderDetailBO orderDetailBO = (OrderDetailBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.ORDER_DETAILS);
    MessageUtil messageUtil = new MessageUtil();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonArrayBuilder allOrderDetails = Json.createArrayBuilder();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/company", "root", "1234");

            ArrayList<OrderDetailDTO> all = orderDetailBO.getAllOrderDetails(connection);
            for (OrderDetailDTO detailDTO : all) {
                JsonObjectBuilder detail = Json.createObjectBuilder();
                detail.add("orderId", detailDTO.getOrderId());
                detail.add("code", detailDTO.getItemCode());
                detail.add("price", detailDTO.getPrice());
                detail.add("qty", detailDTO.getQty());
                allOrderDetails.add(detail.build());
            }

            resp.setStatus(200);
            resp.getWriter().print(messageUtil.buildJsonObject("OK", "Successfully Loaded", allOrderDetails).build());

        } catch (SQLException | ClassNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print(messageUtil.buildJsonObject("Error", e.getLocalizedMessage(), "").build());
        }
    }
}