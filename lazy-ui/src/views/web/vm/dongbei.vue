<template>
  <div style="max-width: 1500px;margin: 0 auto;padding: 16px;float: right;width: 60%">
    <div><h2>{{ exData.title }}</h2></div>
    <el-divider/>
    <div>
      <el-card style="position: absolute;left: 5px; top: 0; width: 40%;height:100%;overflow-y: auto;">
        <div slot="header" class="clearfix">
          <span>实验步骤</span>
          <span style="float: right;color: #498c5f" v-if="exData.resource==null">暂未上传实验大纲</span>
          <span v-else style="float: right;color: #498c5f"> <a
            :href="exData.resource.url" target="_blank">点击此处下载实验大纲</a></span>
        </div>
        <div style="" v-html="exData.process">

        </div>
      </el-card>
    </div>
    <el-card>
        <div class="user" style="margin-top: 20px;">
          地心地固直角坐标系：
          <el-input v-model="XYZ2ENUDATA.input.X " style="width: 220px"/>
          <el-input v-model="XYZ2ENUDATA.input.Y " style="width: 220px"/>
          <el-input v-model="XYZ2ENUDATA.input.Z " style="width: 220px"/>
        </div>
        <div class="frequency" style="margin-top: 20px;margin-left: 65px;">
          大地坐标系：
          <el-input v-model="XYZ2ENUDATA.input2.B " style="width: 220px"/>
          <el-input v-model="XYZ2ENUDATA.input2.H " style="width: 220px"/>
          <el-input v-model="XYZ2ENUDATA.input2.L " style="width: 220px"/>
          <el-button @click="refresh" class="properties">刷新</el-button>
        </div>
      <!-- <span v-if="isType">地心地固直角坐标系:{{ XYZ2ENUDATA.input.X }},{{ XYZ2ENUDATA.input.Y }},{{ XYZ2ENUDATA.input.Z }}</span>
      <span v-else>大地坐标系:{{ XYZ2ENUDATA.input2.B }},{{ XYZ2ENUDATA.input2.H }},{{ XYZ2ENUDATA.input2.L }}</span> -->
    </el-card>
    <el-card  class="card" style="margin-top: 10px">
      <p style="font-size: 14px;font-weight: 600;">请将地心地固直角坐标系转换成大地坐标系</p>
      <div >
        <div style="padding-top: 25px;">
          <el-form ref="form" size="medium" label-width="100px">
            <el-row :gutter="24">
              <el-col :span="24">
                <el-form-item :disabled="isType" label="大地坐标系" prop="time">
                  <el-input :style="{width: '50%'}" placeholder="大地坐标系" ref="ddzb" v-model="ddzb"></el-input>
                  <el-button type="primary" @click="handle" class="properties">
                    提交
                  </el-button>
                </el-form-item>
                <el-form-item :disabled="plsType" label="东北天坐标" prop="time">
                  <el-input :style="{width: '50%'}" placeholder="东北天坐标系" ref="dbzb" v-model="dbzb"></el-input>
                  <el-button type="primary" @click="handle1" class="properties">
                    提交
                  </el-button>
                </el-form-item>
              </el-col>
            </el-row>
            <!-- <el-row>
              <el-col :span="24" :style="{ textAlign: 'center' }">
                <el-button type="primary" @click="handle">
                  提交
                </el-button>
              </el-col>
            </el-row> -->
          </el-form>
        </div>
      </div>
    </el-card>
    <!-- <el-card v-else class="card" style="margin-top: 10px">
      <p>ps:大地坐标系转换为东北天坐标系</p>
      <div style="text-align: center">
        <div style="padding-top: 25px;">
          <el-form ref="form" size="medium" label-width="100px">
            <el-row :gutter="24">
              <el-col :span="24">
                <el-form-item label="东北天坐标" prop="time">
                  <el-input :style="{width: '100%'}" placeholder="东北天坐标系" ref="dbzb" v-model="dbzb"></el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="24" :style="{ textAlign: 'center' }">
                <el-button type="primary" @click="handle1">
                  提交
                </el-button>
              </el-col>
            </el-row>
          </el-form>
        </div>
      </div>
    </el-card> -->
  </div>
</template>

<script>
import {XYZ2ENU} from "@/api/vm/vmTest";
import {getExperiment} from "@/api/vm/ex";

export default {
  name: "GeocenGeostatiRectan",
  data() {
    return {
      exData: {},
      isType: false,
      plsType:true,
      XYZ2ENUDATA: {},
      ddzb: '',
      dbzb: '',
    }
  },
  created() {
    const query = this.$route.query
    getExperiment(query.id).then(response => {
      this.exData = response.data
    })
    XYZ2ENU().then(response => {
      console.log(response)
      this.XYZ2ENUDATA = response.data
    })
  },
  methods: {
    //刷新按钮
    refresh(){
       XYZ2ENU().then(response => {
        console.log(response)
        this.XYZ2ENUDATA = response.data
      })
      this.ddzb = ''
      this.dbzb = ''
    },
    handle() {
      var v = this.XYZ2ENUDATA.input2.B + "," + this.XYZ2ENUDATA.input2.L + "," + this.XYZ2ENUDATA.input2.H
      if (this.ddzb === v) {
        this.$confirm('转换正确!', '提示', {
          confirmButtonText: '下一题',
          type: 'success'
        }).then(() => {

        })
        this.isType = false
      } else {
         this.$confirm('转换错误，正确答案为：' + v, '提示', {
                confirmButtonText: '确定',
                type: 'warning'
              }).then(()=>{
                this.ddzb = this.XYZ2ENUDATA.input2.B + "," + this.XYZ2ENUDATA.input2.L + "," + this.XYZ2ENUDATA.input2.H
              })
      }
    },
    handle1() {
      var v = this.XYZ2ENUDATA.output.e
      if ((this.dbzb) * 1 === v) {
        this.$confirm('转换正确!', '提示', {
          confirmButtonText: '完成',
          type: 'success'
        }).then(() => {

        })
      } else {
        this.$confirm('转换错误，正确答案为：'+ v, '提示',{
          confirmButtonText: '确定',
          type:'warning'
        }).then(()=>{
          this.dbzb = this.XYZ2ENUDATA.output.e
        })
      }
    }
  }
}
</script>

<style scoped>
  .properties{
    margin-left: 10px;
  }
</style>
