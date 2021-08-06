export class NotFoundError implements Error {
  message: string;
  name: string;

  constructor() {
    this.message = "Not found error";
    this.name = "NotFoundError";
  }
}

export class BadRequestError implements Error {
  message: string;
  name: string;
  response: Response;

  constructor(response: Response) {
    this.message = "Bad request error";
    this.name = "BadRequestError";
    this.response = response;
  }
}
