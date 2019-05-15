package com.leyou.item.web.controller;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.ISpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("spec")
public class SpecificationController {
    @Autowired
    private ISpecificationService specificationService;

    /**
     * 根据分类id查询规格组
     * @param cid
     * @return
     */
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryByCid(@PathVariable("cid") Long cid) {
        return ResponseEntity.ok(specificationService.queryByCid(cid));
    }

    /**
     * 根据组id查询参数
     * @param gid
     * @return
     */
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> queryParamByGid(@RequestParam("gid")Long gid){
        return ResponseEntity.ok(specificationService.queryParamByGid(gid));
    }

    /**
     * 新增规格组
     * @param cid
     * @param name
     * @return
     */
    @PostMapping("group")
    public ResponseEntity<Void> addGroup(@RequestParam("cid")Long cid,@RequestParam("name")String name){
        specificationService.addGroup(name,cid);
        return ResponseEntity.ok().build();
    }

    /**
     * 新增规格参数
     * @param specParam
     * @return
     */
    @PostMapping("param")
    public ResponseEntity<Void> addParam(SpecParam specParam){
        specificationService.addParam(specParam);
        return ResponseEntity.ok().build();
    }

    /**
     * 删除商品规格组
     * @param id
     * @return
     */
    @DeleteMapping("group/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable("id")Long id){
        specificationService.deleteGroup(id);
        return ResponseEntity.ok().build();
    }
}
