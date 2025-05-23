<template>
    <a-card>
      <div v-if="$access('ENT_UR_USER_SEARCH')" class="table-page-search-wrapper">
        <a-form layout="inline" class="table-head-ground">
          <div class="table-layer">
            <henpay-text-up :placeholder="'用户ID'" v-model:value="vdata.searchData.sysUserId" />
            <henpay-text-up :placeholder="'用户姓名'" v-model:value="vdata.searchData.realname" />

            <span class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchFunc" :loading="vdata.btnLoading">
                查询
              </a-button>
              <a-button style="margin-left: 8px" @click="() => (vdata.searchData = {})">
                重置
              </a-button>
            </span>
          </div>
        </a-form>
      </div>

      <!-- 列表渲染 -->
      <henpay-table
        @btnLoadClose="vdata.btnLoading = false"
        ref="infoTable"
        :initData="true"
        :reqTableDataFunc="reqTableDataFunc"
        :tableColumns="vdata.tableColumns"
        :searchData="vdata.searchData"
        rowKey="sysUserId"
      >
        <template #opRow>
          <a-button v-if="$access('ENT_UR_USER_ADD')" type="primary" @click="addFunc">
            新建
          </a-button>
        </template>

        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'avatar'">
            <a-avatar size="default" :src="record.avatarUrl" />
          </template>

          <template v-if="column.key === 'state'">
            <henpay-table-col-state
              :state="record.state"
              :showSwitchType="$access('ENT_UR_USER_EDIT')"
              :onChange="
                (state) => {
                  return updateState(record.sysUserId, state)
                }
              "
            />
          </template>

          <template slot="opSlot" v-if="column.key === 'op'">
            <!-- 操作列插槽 -->
            <henpay-table-columns>
              <a-button
                type="link"
                v-if="$access('ENT_UR_USER_UPD_ROLE')"
                @click="roleDist(record.sysUserId)"
              >
                变更角色
              </a-button>
              <a-button
                type="link"
                v-if="$access('ENT_UR_USER_EDIT')"
                @click="editFunc(record.sysUserId)"
              >
                修改
              </a-button>
              <a-button
                v-if="$access('ENT_UR_USER_DELETE')"
                type="link"
                danger
                @click="delFunc(record.sysUserId)"
              >
                删除
              </a-button>
            </henpay-table-columns>
          </template>
        </template>
      </henpay-table>
    </a-card>

    <!-- 新增 / 修改 页面组件  -->
    <InfoAddOrEdit ref="infoAddOrEdit" :callbackFunc="searchFunc" />

    <!-- 分配角色 页面组件  -->
    <RoleDist ref="roleDistRef" />
</template>
<script setup lang="ts">
import { API_URL_SYS_USER_LIST, req, reqLoad } from '@/api/manage'
import InfoAddOrEdit from './AddOrEdit.vue'
import RoleDist from './RoleDist.vue'
import { reactive, getCurrentInstance, ref } from 'vue'

const tableColumns = [
  { title: '用户ID', dataIndex: 'sysUserId', fixed: 'left', width: 200 },
  { title: '姓名', dataIndex: 'realname', width: 200 },
  {
    title: '性别',
    dataIndex: 'sex',
    customRender: ({ record }) => {
      return record.sex === 1 ? '男' : record.sex === 2 ? '女' : '未知'
    },
    width: 200
  },
  { title: '头像', key: 'avatar', scopedSlots: { customRender: 'avatarSlot' }, width: 200 },
  { title: '编号', dataIndex: 'userNo', width: 200 },
  { title: '手机号', dataIndex: 'telphone', width: 200 },
  {
    title: '超管',
    dataIndex: 'isAdmin',
    customRender: ({ record }) => {
      return record.isAdmin === 1 ? '是' : '否'
    },
    width: 200
  },
  { title: '状态', key: 'state', scopedSlots: { customRender: 'stateSlot' }, align: 'center', width: 200 },
  { title: '创建时间', dataIndex: 'createdAt', width: 200 },
  { title: '修改时间', dataIndex: 'updatedAt', width: 200 },
  {
    key: 'op',
    title: '操作',
    width: 200,
    fixed: 'right',
    align: 'center',
    scopedSlots: { customRender: 'opSlot' },
  },
]

const { $infoBox, $access } = getCurrentInstance()!.appContext.config.globalProperties

const vdata: any = reactive({
  tableColumns: tableColumns,
  searchData: {},
  btnLoading: false,
})

const infoTable = ref()
const infoAddOrEdit = ref()
const roleDistRef = ref()

// 请求table接口数据
function reqTableDataFunc(params) {
  return req.list(API_URL_SYS_USER_LIST, params)
}

function searchFunc() {
  // 点击【查询】按钮点击事件
  vdata.btnLoading = true // 打开查询按钮的loading
  infoTable.value.refTable(true)
}

function addFunc() {
  // 业务通用【新增】 函数
  infoAddOrEdit.value.show()
}

function delFunc(recordId) {
  // 业务通用【删除】 函数
  $infoBox.confirmDanger('确认删除？', '', () => {
    return req.delById(API_URL_SYS_USER_LIST, recordId).then((res) => {
      $infoBox.message.success('删除成功！')
      infoTable.value.refTable(false)
    })
  })
}

function editFunc(recordId) {
  // 业务通用【修改】 函数
  infoAddOrEdit.value.show(recordId)
}

function roleDist(recordId) {
  // 【分配权限】 按钮点击事件
  roleDistRef.value.show(recordId)
}
function updateState(recordId, state) {
  // 【更新状态】
  const title = state === 1 ? '确认[启用]该用户？' : '确认[停用]该用户？'
  const content =
    state === 1 ? '启用后用户可进行登陆等一系列操作' : '停用后该用户将立即退出系统并不可再次登陆'

  return new Promise((resolve, reject) => {
    $infoBox.confirmDanger(
      title,
      content,
      () => {
        return reqLoad
          .updateById(API_URL_SYS_USER_LIST, recordId, { state: state })
          .then((res) => {
            searchFunc()
            resolve(res)
          })
          .catch((err) => reject(err))
      },
      () => {
        reject(new Error())
      }
    )
  })
}
</script>
