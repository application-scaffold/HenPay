import { Modal, notification, message, type ModalFuncProps } from 'ant-design-vue'

// 定义ant model类型的点击取消按钮的回调函数
// 支持： 自动关闭（默认）， 用户手动调用函数关闭（不包含蒙层）
// 返回结果： { closeFunc: 关闭函数 , triggerCancel: 是否蒙层关闭 }
interface RejectResult {
  closeFunc: () => void;
  triggerCancel: boolean;
}

// 处理model的confirm
function modelConfirmPromiseRejectFunc(
  antCloseFuncOrMsg: ((...args: any[]) => void) | { triggerCancel: boolean },
  extConfig: Record<string, any> = {},
  reject: (result: RejectResult) => void
): Promise<void> | void {
  // 是否手动关闭 （ 不自动调用关闭接口， 会返回给reject去调用支持异步 ）
  let isCatchFuncClose = false;
  if (extConfig && extConfig.isCatchFuncClose) {
    isCatchFuncClose = true;
  }

  // 定义reject 返回的对象
  let rejectResult: RejectResult = {
    closeFunc: typeof antCloseFuncOrMsg === 'function' ? antCloseFuncOrMsg : () => {},
    triggerCancel: false,
  };

  // 当前是否是蒙层关闭的
  if (typeof antCloseFuncOrMsg === 'object' && antCloseFuncOrMsg.triggerCancel) {
    rejectResult.triggerCancel = true;
    return reject(rejectResult);
  }

  // 自动关闭
  if (!isCatchFuncClose) {
    rejectResult.closeFunc();
  }

  return reject(rejectResult);
}

// 定义 confirmResult 对象的类型
export interface ConfirmResult {
  confirm: (
    title: string,
    content: string,
    okFunc: () => void,
    cancelFunc: (e: any) => void,
    extConfig: ModalFuncProps
  ) => ReturnType<typeof Modal.confirm>;

  confirmPrimary: (
    title: string,
    content: string,
    okFunc: () => void,
    cancelFunc: () => void,
    extConfig: ModalFuncProps
  ) => ReturnType<typeof Modal.confirm>;

  confirmDanger: (
    title: string,
    content: string,
    okFunc: () => void,
    cancelFunc: () => void,
    extConfig: ModalFuncProps
  ) => ReturnType<typeof Modal.confirm>;

  confirmPrimaryPromise: (
    title: string,
    content: string,
    extConfig: ModalFuncProps
  ) => Promise<string>;

  confirmDangerPromise: (
    title: string,
    content: string,
    extConfig: ModalFuncProps
  ) => Promise<string>;

  modalError: (
    title: string,
    content: string,
    okFunc: () => void
  ) => ReturnType<typeof Modal.error>;

  modalSuccess: (
    title: string,
    content: string,
    okFunc: () => void
  ) => ReturnType<typeof Modal.success>;

  modalWarning: (
    title: string,
    content: string,
    okFunc: () => void
  ) => ReturnType<typeof Modal.warning>;

  notification: typeof notification;
  modal: typeof Modal;
  message: typeof message;
}

// 确认提示： 标题， 内容， 点击确定回调函数， 取消回调，  扩展参数
export const confirmResult: ConfirmResult = {
  confirm: (
    title: string = '提示',
    content: string,
    okFunc: () => void,
    cancelFunc: (e: any) => void = () => {},
    extConfig: ModalFuncProps = {}
  ) => {
    return Modal.confirm(
      Object.assign(
        {
          okText: '确定',
          cancelText: '取消',
          title,
          content,
          onOk: okFunc,
          onCancel: cancelFunc,
          confirmLoading: true,
        },
        extConfig
      )
    );
  },

  confirmPrimary: (
    title: string = '提示',
    content: string,
    okFunc: () => void,
    cancelFunc: () => void = () => {},
    extConfig: ModalFuncProps = {}
  ) => {
    return confirmResult.confirm(
      title,
      content,
      okFunc,
      cancelFunc,
      Object.assign({ okType: 'primary' }, extConfig)
    );
  },

  confirmDanger: (
    title: string = '提示',
    content: string,
    okFunc: () => void,
    cancelFunc: () => void = () => {},
    extConfig: ModalFuncProps = {}
  ) => {
    return confirmResult.confirm(
      title,
      content,
      okFunc,
      cancelFunc,
      Object.assign({ okType: 'danger' }, extConfig)
    );
  },

  confirmPrimaryPromise: (
    title: string = '提示',
    content: string,
    extConfig: ModalFuncProps = {}
  ): Promise<string> => {
    return new Promise((resolve, reject) => {
      confirmResult.confirm(
        title,
        content,
        () => {
          resolve('');
        },
        (antCloseFuncOrMsg: any) => {
          return modelConfirmPromiseRejectFunc(antCloseFuncOrMsg, extConfig, reject);
        },
        Object.assign({ okType: 'primary' }, extConfig)
      );
    });
  },

  confirmDangerPromise: (
    title: string = '提示',
    content: string,
    extConfig: ModalFuncProps = {}
  ): Promise<string> => {
    return new Promise((resolve, reject) => {
      return confirmResult.confirm(
        title,
        content,
        () => {
          resolve('');
        },
        (antCloseFuncOrMsg: any) => {
          return modelConfirmPromiseRejectFunc(antCloseFuncOrMsg, extConfig, reject);
        },
        Object.assign({ okType: 'danger' }, extConfig)
      );
    });
  },

  modalError: (
    title: string,
    content: string,
    okFunc: () => void = () => {}
  ) => {
    return Modal.error({ title, content, onOk: okFunc });
  },

  modalSuccess: (
    title: string,
    content: string,
    okFunc: () => void = () => {}
  ) => {
    return Modal.success({ title, content, onOk: okFunc });
  },

  modalWarning: (
    title: string,
    content: string,
    okFunc: () => void = () => {}
  ) => {
    return Modal.warning({ title, content, onOk: okFunc });
  },

  notification,
  modal: Modal,
  message,
};

export default confirmResult;
