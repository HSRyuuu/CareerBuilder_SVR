/**
 * 메뉴 상수 정의
 */

// 메뉴 키 상수
export const Menu = {
  Home: 'HOME',
  Career: 'CAREER',
  Resume: 'RESUME',
  AI: 'AI',
} as const;

export const MENU_URLS = {
  HOME: '/home',
  EXPERIENCE: '/experience',
  EXPERIENCE_REGISTER: '/experience/register',
  RESUME: '/resume',
  AI: '/ai',
  WELCOME: '/welcome',
  LOGIN: '/welcome/login',
  SIGNUP: '/welcome/signup',
  TRY_CAREER: '/welcome/try/experience',
  AI_EXPERIENCE: '/ai/experience',
  SETTING: '/account/settings',
  MANAGE_PLAN: '/account/manage-plan',
}


export type TMenuKey = (typeof Menu)[keyof typeof Menu];

// 메뉴 아이템 타입
export type TMenuItem = {
  key: TMenuKey;
  path: string;
  label: string;
  icon: string;
};

// 메뉴 목록
export const MENU_ITEMS: TMenuItem[] = [
  { key: Menu.Home, path: MENU_URLS.HOME, label: '홈', icon: 'mdi-home' },
    { key: Menu.AI, path: MENU_URLS.AI, label: 'AI 코칭', icon: 'mdi-brain' },
  { key: Menu.Career, path: MENU_URLS.EXPERIENCE, label: '경험', icon: 'mdi-text-box-multiple' },
  { key: Menu.Resume, path: MENU_URLS.RESUME, label: '이력서', icon: 'mdi-account-details' },
];

// 메뉴 키로 메뉴 아이템 찾기
export const getMenuByKey = (key: TMenuKey): TMenuItem | undefined => {
  return MENU_ITEMS.find((item) => item.key === key);
};

// path로 메뉴 키 찾기
export const getMenuKeyByPath = (path: string): TMenuKey | undefined => {
  const menu = MENU_ITEMS.find((item) => path.startsWith(item.path));
  return menu?.key;
};


