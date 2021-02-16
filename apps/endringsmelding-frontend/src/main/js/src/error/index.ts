export class NotFoundError implements Error {
  message: string;
  name: 'NotFoundError';

  constructor() {
    this.message = 'Not found error';
  }
}
