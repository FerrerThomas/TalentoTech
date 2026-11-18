package com.techlab.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class PedidoRequestDTO {

    @NotNull(message = "El usuarioId es obligatorio")
    private Long usuarioId;

    @NotEmpty(message = "El pedido debe contener al menos un item")
    @Valid
    private List<ItemPedidoDTO> itemsPedido;

    public PedidoRequestDTO() {
    }

    public PedidoRequestDTO(Long usuarioId, List<ItemPedidoDTO> itemsPedido) {
        this.usuarioId = usuarioId;
        this.itemsPedido = itemsPedido;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public List<ItemPedidoDTO> getItemsPedido() {
        return itemsPedido;
    }

    public void setItemsPedido(List<ItemPedidoDTO> itemsPedido) {
        this.itemsPedido = itemsPedido;
    }
}
