import { deriveBestillingsveilederState } from './deriveBestillingsveilederState'

export { deriveBestillingsveilederState }
export const BVOptions = (config: any, environments: any) =>
	deriveBestillingsveilederState(config, environments)
