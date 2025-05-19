export interface IUserLocalData {
  roomId: string,
  joinCode: string,
  playerName: string,
  playerId: string
}

export const setLocalStorageUserData = (data: IUserLocalData) => {
  localStorage.setItem('user-data', JSON.stringify(data));
}

export const clearLocalStorageUserData = () => {
  localStorage.removeItem('user-data');
}

export const getLocalStorageUserData = (): IUserLocalData | null => {
  try {
    let data = localStorage.getItem('user-data');
    return data ? JSON.parse(data) : null;
  }
  catch (e) {
    return null
  }
}
