<template>
  <div class="jee-text-up table-head-layout">
    <a-input :value="value" required="required" @change="onChange($event)" />
    <label>{{ placeholder }}</label>
  </div>
</template>

<script lang="ts" setup>

  const props = defineProps({
    value: { type: String, default: undefined },
    placeholder: { type: String, default: '' }
  })

  const emit = defineEmits(['update:value'])

  const onChange = (e) => {
    emit('update:value', e.target.value)
  }
</script>

<style scoped lang="less">
// 文字上移 效果
.jee-text-up {
  position: relative;

  input {

    outline: 0;
    text-indent: 60px;
    transition: all .3s ease-in-out;

  }
  input::-webkit-input-placeholder {
    color: #BFBFBF;
    text-indent: 0;
  }
  input + label {
    pointer-events: none;
    position: absolute;
    left: 0;
    bottom: 6px;
    padding: 2px 11px;
    color: #BFBFBF;
    font-size: 13px;
    text-transform: uppercase;
    transition: all .3s ease-in-out;
    border-radius: 3px;
    background: rgba(122, 184, 147, 0);
    height: 20px;
    line-height: 20px;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  // 三角形
  input + label:after {
    position: absolute;
    content: "";
    width: 0;
    height: 0;
    top: 100%;
    left: 50%;
    margin-left: -3px;
    border-left: 3px solid transparent;
    border-right: 3px solid transparent;
    transition: all .3s ease-in-out;
  }
  input:focus,
  input:active,
  input:valid + label {
    text-indent: 0;
    background: #fff;
  }
  input:focus + label,
  input:active + label,
  input:valid + label {
    color: #fff;
    background: @jee-theme;
    transform: translateY(-33px);
  }
  input:focus + label:after,
  input:active + label:after {
    border-top: 4px solid @jee-theme;
  }

  input:valid {
    text-indent: 0; //文字不下移
  }
  input:valid + label{
    background: #dadada; // 更换背景色
  }

  input:valid + label:after{ // 更换背景色
    border-top: 4px solid #dadada;
  }
}

// 文字上移效果 初版
// .jee-text-up {
//   position: relative;

//   .jee-text-label {
//     position: absolute;
//     z-index: 1;
//     left: 15px;
//     top: 0px;
//     transition: all .3s;
//     color: #bfbfbf;
//     pointer-events: none;
//     height: 20px;
//     line-height: 20px;
//   }

//   input:focus + .jee-text-label, input:valid + .jee-text-label {
//     top: -35px;
//     padding: 1px 10px;
//     border-radius: 5px;
//     background: @jee-theme;
//     color: #fff;
//   }
// }
</style>
