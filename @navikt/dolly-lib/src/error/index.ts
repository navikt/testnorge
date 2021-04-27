export class NotFoundError implements Error {
  message: string;
  name: string;

  constructor() {
    this.message = "Not found error";
    this.name = "NotFoundError";
  }
}
