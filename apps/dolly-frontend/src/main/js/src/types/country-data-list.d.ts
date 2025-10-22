declare module 'country-data-list' {
	export const lookup: {
		countries: (filter: {
			alpha2: string
		}) => Array<{ countryCallingCodes?: string[]; emoji?: string }>
	}
}
