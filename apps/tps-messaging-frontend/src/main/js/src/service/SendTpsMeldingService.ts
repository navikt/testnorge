import { postData } from '../api/api';

export const sendTpsMelding = (
  queue: string,
  message: string
): Promise<string> =>
  postData(
    '/tps-messaging-service/api/v1/xml?queue=' + queue,
    message,
    { 'Content-Type': 'application/text' }
  ).then((response: any) => response) as Promise<string>;