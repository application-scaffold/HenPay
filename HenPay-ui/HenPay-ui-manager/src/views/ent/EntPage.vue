<template>
    <a-card>
      <div class="table-page-search-wrapper">
        <a-form layout="inline" style="margin-bottom: 30px">
          <a-row :gutter="16">
            <a-col :sm="18">
              <a-row :gutter="16">
                <a-col :md="6">
                  <a-form-item label="">
                    <a-select
                      v-model:value="vdata.querySysType"
                      placeholder="选择系统菜单"
                      @change="refTable"
                      class="table-head-layout"
                    >
                      <a-select-option value="MGR">显示菜单：运营平台</a-select-option>
                      <a-select-option value="MCH">显示菜单：商户系统</a-select-option>
                    </a-select>
                  </a-form-item>
                </a-col>
              </a-row>
            </a-col>
          </a-row>
        </a-form>
      </div>

      <henpay-table
        ref="infoTable"
        :init-data="false"
        :table-columns="vdata.tableColumns"
        :req-table-data-func="reqTableDataFunc"
        :pagination="false"
        :loading="vdata.loading"
        rowKey="entId"
        :scroll="{ x: 1450 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'state'">
            <henpay-table-col-state
              :state="record.state"
              :showSwitchType="$access('ENT_UR_ROLE_ENT_EDIT')"
              :onChange="
                (state) => {
                  return updateState(record.entId, state)
                }
              "
            />
          </template>
          <template v-if="column.key === 'op'">
            <!-- 操作列插槽 -->
            <henpay-table-columns>
              <a-button
                v-if="$access('ENT_UR_ROLE_ENT_EDIT')"
                type="link"
                @click="editFunc(record.entId)"
              >
                修改
              </a-button>
            </henpay-table-columns>
          </template>
        </template>
      </henpay-table>
    </a-card>

    <!-- 新增 / 修改 页面组件  -->
    <InfoAddOrEdit ref="infoAddOrEdit" :callbackFunc="refTable" />
</template>
<script setup lang="ts">
import { getEntTree, API_URL_ENT_LIST, reqLoad } from '@/api/manage'
import InfoAddOrEdit from './AddOrEdit.vue'
import { reactive, getCurrentInstance, ref, onMounted } from 'vue'
import HenpayTable from '@/components/HenpayTable/HenpayTable.vue'
import HenpayTableColumns from '@/components/HenpayTable/HenpayTableColumns.vue'

const { $infoBox, $access } = getCurrentInstance()!.appContext.config.globalProperties

const tableColumns = [
  { title: '资源权限ID', dataIndex: 'entId', width:100 }, // key为必填项，用于标志该列的唯一
  { title: '资源名称', dataIndex: 'entName', width:100 },
  { title: '图标', dataIndex: 'menuIcon', width:100 },
  { title: '路径', dataIndex: 'menuUri', width:100 },
  { title: '组件名称', dataIndex: 'componentName', width:100 },
  { title: '类型', dataIndex: 'entType', width:100 },
  { title: '状态', key: 'state', align: 'center', width:100 },
  { title: '排序', dataIndex: 'entSort', width:100 },
  { title: '修改时间', dataIndex: 'updatedAt', width:100 },
  {
    title: '操作',
    width: 100,
    fixed: 'right',
    align: 'center',
    key: 'op',
  },
]

const infoAddOrEdit = ref()
const infoTable = ref()

const vdata = reactive({
  querySysType: 'MGR', // 默认查询运营平台
  tableColumns: tableColumns,
  dataSource: [],
  loading: false,
})

function refTable() {
  vdata.loading = true
  getEntTree(vdata.querySysType).then((res) => {
    vdata.dataSource = res
    vdata.loading = false

    infoTable.value.refTable(true)
  })
}

function reqTableDataFunc() {
  return Promise.resolve({
    current: 0,
    total: 0,
    records: vdata.dataSource,
    hasNext: false,
  })
}

onMounted(() => refTable())

function updateState(recordId, state) {
  return reqLoad
    .updateById(API_URL_ENT_LIST, recordId, { state: state, sysType: vdata.querySysType })
    .then((res) => {
      $infoBox.message.success('更新成功')
      refTable() // 刷新页面
    })
}

function editFunc(recordId) {
  // 业务通用【修改】 函数
  infoAddOrEdit.value.show(recordId, vdata.querySysType)
}
</script>
