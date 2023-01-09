// Request decorators

/**
 * Disse brukes i samsvar med request middleware til Redux.
 * Wrappes rundt actions for å få riktig action navn
 */

export const onFailure = (action) => `${action}_FAILURE`
export const onSuccess = (action) => `${action}_SUCCESS`
export const onRequest = (action) => `${action}_REQUEST`
