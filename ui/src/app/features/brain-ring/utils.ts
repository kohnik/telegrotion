import {IBrainRingCreateTeamResponse} from './interfaces';

export const setLocalStorageUserData = (data: IBrainRingCreateTeamResponse) => {
  localStorage.setItem('user-data', JSON.stringify(data));
}

export const clearLocalStorageUserData = () => {
  localStorage.removeItem('user-data');
}

export const getLocalStorageUserData = (): IBrainRingCreateTeamResponse | null => {
  try {
    let data = localStorage.getItem('user-data');
    return data ? JSON.parse(data) : null;
  }
  catch (e) {
    return null
  }
}
