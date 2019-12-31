package com.dangdang.cymbal.web.object.converter;

import com.dangdang.cymbal.web.object.exception.ConvertException;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Base converter.
 * Include base method to convert between PO and DTO by copy same name field.
 *
 * @auther GeZhen
 */
public abstract class BaseConverter<PO, DTO> {

    /**
     * Convert PO to DTO.
     *
     * @param po PO
     * @return DTO
     */
    public DTO poToDto(final PO po) {
        DTO dto = newInstanceOfDto();
        BeanUtils.copyProperties(po, dto);
        poToDto(po, dto);
        return dto;
    }

    /**
     * Allow sub classes do some custom thing when convert PO to DTO.
     *
     * @param po PO
     * @param dto DTO
     */
    abstract void poToDto(PO po, DTO dto);

    /**
     * Convert POs to DTOs.
     *
     * @param pos POs
     * @return DTOs
     */
    public List<DTO> posToDtos(final List<PO> pos) {
        List<DTO> dtos = new ArrayList<>();
        for (PO po : pos) {
            dtos.add(poToDto(po));
        }
        return dtos;
    }

    /**
     * Convert DTO to PO.
     *
     * @param dto DTO
     * @return PO
     */
    public PO dtoToPo(final DTO dto) {
        PO po = newInstanceOfPo();
        BeanUtils.copyProperties(dto, po);
        dtoToPo(dto, po);
        return po;
    }

    /**
     * Allow sub classes do some custom thing when convert DTO to PO.
     *
     * @param dto DTO
     * @param po PO
     */
    abstract void dtoToPo(DTO dto, PO po);

    /**
     * Convert DTOs to POs.
     *
     * @param dtos DTOs
     * @return POs
     */
    public List<PO> dtosToPos(final List<DTO> dtos) {
        List<PO> pos = new ArrayList<>();
        for (DTO dto : dtos) {
            pos.add(dtoToPo(dto));
        }
        return pos;
    }

    private DTO newInstanceOfDto() {
        Type type = getTypeOfIndex(1);
        return this.<DTO>newInstance(type.getTypeName());
    }

    private PO newInstanceOfPo() {
        Type type = getTypeOfIndex(0);
        return this.<PO>newInstance(type.getTypeName());
    }

    private Type getTypeOfIndex(final int index) {
        Type superClass = getClass().getGenericSuperclass();
        return ((ParameterizedType) superClass).getActualTypeArguments()[index];
    }

    private <T> T newInstance(final String className) {
        try {
            return (T) Class.forName(className).newInstance();
        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            throw new ConvertException(e);
        }
    }
}
