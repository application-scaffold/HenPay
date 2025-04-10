/**
 * 获取渠道用户 工具类
 */
const getChannelUserId = (): string | null => {
  try {
    return localStorage.getItem('channelUserId');
  } catch (error) {
    console.error('LocalStorage access error:', error);
    return null;
  }
};

const setChannelUserId = (value: string): void => {
  try {
    localStorage.setItem('channelUserId', value);
  } catch (error) {
    console.error('LocalStorage write error:', error);
  }
};

export const channelUserIdStorage = {
  get: getChannelUserId,
  set: setChannelUserId
};
