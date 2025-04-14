export const insertAtPosition = <T>(arr: T[], item: T, position: number): T[] => {
  return [...arr.slice(0, position), item, ...arr.slice(position)];
};

export const deleteAtPosition = <T>(arr: T[], position: number): T[] => {
  return arr.filter((_, index) => index !== position);
};
