import { http, HttpResponse } from 'msw';

export const handlers = [
  http.get(new RegExp(/testnorge-profil-api\/api\/v1\/profil.*/), () => {
    return HttpResponse.json([]);
  }),
  http.get(new RegExp(/oauth2\/authorization\/aad.*/), () => {
    return HttpResponse.json({});
  }),

  http.get(new RegExp(/tps-messaging-service\/api\/v1\/xml.*/), () => {
    return HttpResponse.json([
      'TPSMELDINGER.XML.NORGE',
      'TPSMELDINGER.XML.UTLAND',
      'TPSMELDINGER.INFO.STANDARD',
    ]);
  }),
  http.post(new RegExp(/tps-messaging-service\/api\/v1\/xml.*/), async ({ request }) => {
    const input = await request?.text();
    return HttpResponse.json(input);
  }),
];
